#!/usr/bin/env python3
"""
Flight Booking API Load Testing Script

This script performs load testing on the flight booking API with:
- Random flight IDs from 1-8000
- Random seat counts from 1-3
- Concurrent requests (configurable)
- Metrics collection (latency, throughput, response codes)
"""

import asyncio
import aiohttp
import random
import time
import json
from datetime import datetime
from collections import defaultdict, Counter
import statistics
import sys

class FlightBookingLoadTester:
    def __init__(self, base_url="http://localhost:8080", concurrency=10, duration=60):
        self.base_url = base_url
        self.concurrency = concurrency
        self.duration = duration
        self.results = {
            'total_requests': 0,
            'response_codes': Counter(),
            'latencies': [],
            'errors': [],
            'start_time': None,
            'end_time': None
        }
        
    def generate_random_booking_request(self):
        """Generate random booking request parameters"""
        # Random flight IDs from 1-8000
        num_flights = random.randint(1, 3)  # 1-3 flight legs
        flight_ids = [random.randint(1, 8000) for _ in range(num_flights)]
        
        # Random seat count from 1-3
        seat_count = random.randint(1, 3)
        
        return {
            'flightIds': flight_ids,
            'seatCount': seat_count
        }
    
    async def make_booking_request(self, session, request_data):
        """Make a single booking request and record metrics"""
        url = f"{self.base_url}/api/v1/booking/initiate"
        start_time = time.time()
        
        try:
            async with session.post(url, json=request_data, timeout=aiohttp.ClientTimeout(total=30)) as response:
                end_time = time.time()
                latency = (end_time - start_time) * 1000  # Convert to milliseconds
                
                # Read response body (but don't process it to save memory)
                response_text = await response.text()
                
                # Record metrics
                self.results['total_requests'] += 1
                self.results['response_codes'][response.status] += 1
                self.results['latencies'].append(latency)
                
                # Log 5XX responses with detailed information
                if 500 <= response.status < 600:
                    print(f"❌ 5XX Error: {response.status} - Flights {request_data['flightIds']} "
                          f"for {request_data['seatCount']} seats (latency: {latency:.1f}ms)")
                
                # Log sample requests
                if self.results['total_requests'] % 50 == 0:
                    print(f"📊 Requests: {self.results['total_requests']}, "
                          f"Latest: {response.status} in {latency:.1f}ms "
                          f"(Flights: {request_data['flightIds']}, Seats: {request_data['seatCount']})")
                
                return response.status, latency
                
        except asyncio.TimeoutError:
            end_time = time.time()
            latency = (end_time - start_time) * 1000
            self.results['total_requests'] += 1
            self.results['response_codes']['TIMEOUT'] += 1
            self.results['errors'].append(f"Timeout: {request_data}")
            return 'TIMEOUT', latency
            
        except Exception as e:
            end_time = time.time()
            latency = (end_time - start_time) * 1000
            self.results['total_requests'] += 1
            self.results['response_codes']['ERROR'] += 1
            self.results['errors'].append(f"Error: {str(e)} - {request_data}")
            return 'ERROR', latency
    
    async def worker(self, session, worker_id):
        """Worker coroutine that makes continuous requests"""
        request_count = 0
        
        while time.time() - self.results['start_time'] < self.duration:
            try:
                # Generate random booking request
                request_data = self.generate_random_booking_request()
                
                # Make the request
                status, latency = await self.make_booking_request(session, request_data)
                request_count += 1
                
                # Small delay to prevent overwhelming the server
                await asyncio.sleep(0.01)
                
            except Exception as e:
                print(f"Worker {worker_id} error: {e}")
                await asyncio.sleep(0.1)
        
        print(f"🔧 Worker {worker_id} completed {request_count} requests")
    
    async def run_load_test(self):
        """Run the load test with specified concurrency and duration"""
        print(f"🚀 Starting load test:")
        print(f"   • Concurrency: {self.concurrency}")
        print(f"   • Duration: {self.duration} seconds")
        print(f"   • Target: {self.base_url}")
        print(f"   • Flight IDs: 1-8000")
        print(f"   • Seat Count: 1-3")
        print("─" * 50)
        
        # Record start time
        self.results['start_time'] = time.time()
        
        # Create HTTP session with connection pooling
        connector = aiohttp.TCPConnector(limit=100, limit_per_host=30)
        timeout = aiohttp.ClientTimeout(total=30)
        
        async with aiohttp.ClientSession(connector=connector, timeout=timeout) as session:
            # Create worker tasks
            tasks = []
            for i in range(self.concurrency):
                task = asyncio.create_task(self.worker(session, i))
                tasks.append(task)
            
            # Wait for all workers to complete
            await asyncio.gather(*tasks)
        
        # Record end time
        self.results['end_time'] = time.time()
    
    def print_results(self):
        """Print comprehensive test results"""
        print("\n" + "=" * 60)
        print("🏁 LOAD TEST RESULTS")
        print("=" * 60)
        
        # Test summary
        actual_duration = self.results['end_time'] - self.results['start_time']
        throughput = self.results['total_requests'] / actual_duration if actual_duration > 0 else 0
        
        print(f"📊 Test Summary:")
        print(f"   • Total Requests: {self.results['total_requests']:,}")
        print(f"   • Duration: {actual_duration:.2f} seconds")
        print(f"   • Throughput: {throughput:.2f} requests/second")
        print(f"   • Concurrency: {self.concurrency}")
        
        # Response codes breakdown
        print(f"\n📈 Response Codes:")
        for status_code, count in sorted(self.results['response_codes'].items()):
            percentage = (count / self.results['total_requests'] * 100) if self.results['total_requests'] > 0 else 0
            print(f"   • {status_code}: {count:,} ({percentage:.1f}%)")
        
        # Response summary
        success_count = sum(count for code, count in self.results['response_codes'].items() 
                          if isinstance(code, int) and 200 <= code < 300)
        client_error_count = sum(count for code, count in self.results['response_codes'].items() 
                               if isinstance(code, int) and 400 <= code < 500)
        server_error_count = sum(count for code, count in self.results['response_codes'].items() 
                               if isinstance(code, int) and 500 <= code < 600)
        
        print(f"\n🎯 Response Summary:")
        print(f"   • 2XX (Success): {success_count:,} ({success_count/self.results['total_requests']*100:.1f}%)")
        print(f"   • 4XX (Client Error): {client_error_count:,} ({client_error_count/self.results['total_requests']*100:.1f}%)")
        print(f"   • 5XX (Server Error): {server_error_count:,} ({server_error_count/self.results['total_requests']*100:.1f}%)")
        
        # Latency statistics
        if self.results['latencies']:
            print(f"\n⚡ Latency Statistics (milliseconds):")
            print(f"   • Min: {min(self.results['latencies']):.1f}ms")
            print(f"   • Max: {max(self.results['latencies']):.1f}ms")
            print(f"   • Mean: {statistics.mean(self.results['latencies']):.1f}ms")
            print(f"   • Median: {statistics.median(self.results['latencies']):.1f}ms")
            
            # Percentiles
            sorted_latencies = sorted(self.results['latencies'])
            p95_index = int(len(sorted_latencies) * 0.95)
            p99_index = int(len(sorted_latencies) * 0.99)
            
            print(f"   • 95th percentile: {sorted_latencies[p95_index]:.1f}ms")
            print(f"   • 99th percentile: {sorted_latencies[p99_index]:.1f}ms")
        
        # Error details
        if self.results['errors']:
            print(f"\n❌ Errors ({len(self.results['errors'])}):")
            error_types = Counter(error.split(':')[0] for error in self.results['errors'])
            for error_type, count in error_types.most_common(5):
                print(f"   • {error_type}: {count}")
        
        print("=" * 60)
    
    def save_results(self):
        """Save detailed results to JSON file"""
        actual_duration = self.results['end_time'] - self.results['start_time']
        throughput = self.results['total_requests'] / actual_duration if actual_duration > 0 else 0
        
        report = {
            'test_config': {
                'concurrency': self.concurrency,
                'duration': self.duration,
                'base_url': self.base_url,
                'flight_id_range': '1-8000',
                'seat_count_range': '1-3'
            },
            'results': {
                'total_requests': self.results['total_requests'],
                'actual_duration': actual_duration,
                'throughput': throughput,
                'response_codes': dict(self.results['response_codes']),
                'latency_stats': {
                    'min': min(self.results['latencies']) if self.results['latencies'] else 0,
                    'max': max(self.results['latencies']) if self.results['latencies'] else 0,
                    'mean': statistics.mean(self.results['latencies']) if self.results['latencies'] else 0,
                    'median': statistics.median(self.results['latencies']) if self.results['latencies'] else 0,
                },
                'error_count': len(self.results['errors']),
                'timestamp': datetime.now().isoformat()
            }
        }
        
        with open('load_test_booking_results.json', 'w') as f:
            json.dump(report, f, indent=2)

async def main():
    """Main function"""
    # Parse command line arguments
    concurrency = 10
    duration = 60
    
    if len(sys.argv) > 1:
        concurrency = int(sys.argv[1])
    if len(sys.argv) > 2:
        duration = int(sys.argv[2])
    
    # Create and run load tester
    tester = FlightBookingLoadTester(concurrency=concurrency, duration=duration)
    
    print(f"🛫 Testing Flight Booking API")
    print(f"Sample request: {{'flightIds': [1234, 5678], 'seatCount': 2}}")
    
    # Run the test
    try:
        await tester.run_load_test()
        tester.print_results()
        tester.save_results()
        print(f"\n💾 Detailed report saved to: load_test_booking_results.json")
        return 0
    except KeyboardInterrupt:
        print("\n🛑 Test interrupted by user")
        tester.results['end_time'] = time.time()
        tester.print_results()
        return 1
    except Exception as e:
        print(f"\n❌ Test failed with error: {e}")
        return 1

if __name__ == "__main__":
    exit_code = asyncio.run(main())
    sys.exit(exit_code)
