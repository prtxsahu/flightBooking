#!/usr/bin/env python3
"""
Flight Search API Load Testing Script

This script performs load testing on the flight search API with:
- 98 airports for source/destination combinations
- Random passenger counts (1-9)
- Random dates (Oct 1-30, 2025)
- Concurrent requests (configurable)
- Metrics collection (latency, throughput, response codes)
"""

import asyncio
import aiohttp
import random
import time
import json
from datetime import datetime, timedelta
from collections import defaultdict, Counter
import statistics
import sys

class FlightSearchLoadTester:
    def __init__(self, base_url="http://localhost:8080", concurrency=10, duration=15):
        self.base_url = base_url
        self.concurrency = concurrency
        self.duration = duration
        self.airports = []
        self.results = {
            'total_requests': 0,
            'response_codes': Counter(),
            'latencies': [],
            'errors': [],
            'start_time': None,
            'end_time': None
        }
        
    def load_airports(self, filename='airports.txt'):
        """Load airport codes from file"""
        try:
            with open(filename, 'r') as f:
                self.airports = [line.strip() for line in f if line.strip()]
            print(f"✅ Loaded {len(self.airports)} airports")
            return True
        except FileNotFoundError:
            print(f"❌ Error: {filename} not found. Please ensure airports are exported.")
            return False
    
    def generate_random_request_params(self):
        """Generate random search parameters"""
        source = random.choice(self.airports)
        destination = random.choice([a for a in self.airports if a != source])
        
        # Random date between Oct 1-30, 2025
        start_date = datetime(2025, 10, 1)
        end_date = datetime(2025, 10, 30)
        random_days = random.randint(0, 29)
        departure_date = (start_date + timedelta(days=random_days)).strftime('%Y-%m-%d')
        
        # Random passenger count 1-9
        passenger_count = random.randint(1, 9)
        
        return {
            'source': source,
            'destination': destination,
            'departureDate': departure_date,
            'passengerCount': passenger_count
        }
    
    async def make_search_request(self, session, params):
        """Make a single search request and record metrics"""
        url = f"{self.base_url}/api/v1/search/flights"
        start_time = time.time()
        
        try:
            async with session.get(url, params=params, timeout=aiohttp.ClientTimeout(total=30)) as response:
                end_time = time.time()
                latency = (end_time - start_time) * 1000  # Convert to milliseconds
                
                # Read response body (but don't process it to save memory)
                await response.read()
                
                # Record metrics
                self.results['total_requests'] += 1
                self.results['response_codes'][response.status] += 1
                self.results['latencies'].append(latency)
                
                # Log 5XX responses with detailed information
                if 500 <= response.status < 600:
                    print(f"❌ 5XX Error: {response.status} - {params['source']}→{params['destination']} "
                          f"on {params['departureDate']} for {params['passengerCount']} passengers "
                          f"(latency: {latency:.1f}ms)")
                
                # Log sample requests
                if self.results['total_requests'] % 50 == 0:
                    print(f"📊 Requests: {self.results['total_requests']}, "
                          f"Latest: {response.status} in {latency:.1f}ms "
                          f"({params['source']}→{params['destination']})")
                
                return response.status, latency
                
        except asyncio.TimeoutError:
            end_time = time.time()
            latency = (end_time - start_time) * 1000
            self.results['total_requests'] += 1
            self.results['response_codes']['TIMEOUT'] += 1
            self.results['errors'].append(f"Timeout: {params}")
            return 'TIMEOUT', latency
            
        except Exception as e:
            end_time = time.time()
            latency = (end_time - start_time) * 1000
            self.results['total_requests'] += 1
            self.results['response_codes']['ERROR'] += 1
            self.results['errors'].append(f"Error: {str(e)} - {params}")
            return 'ERROR', latency
    
    async def worker(self, session, worker_id):
        """Worker coroutine that makes continuous requests"""
        worker_requests = 0
        while time.time() - self.results['start_time'] < self.duration:
            params = self.generate_random_request_params()
            await self.make_search_request(session, params)
            worker_requests += 1
            
            # Small delay to prevent overwhelming the server
            await asyncio.sleep(0.01)  # 10ms between requests per worker
            
        print(f"🔧 Worker {worker_id} completed {worker_requests} requests")
    
    async def run_load_test(self):
        """Run the main load test"""
        print(f"🚀 Starting load test:")
        print(f"   • Concurrency: {self.concurrency}")
        print(f"   • Duration: {self.duration} seconds")
        print(f"   • Target: {self.base_url}")
        print(f"   • Airports: {len(self.airports)}")
        print(f"   • Date range: 2025-10-01 to 2025-10-30")
        print(f"   • Passengers: 1-9")
        print("─" * 50)
        
        self.results['start_time'] = time.time()
        
        # Create HTTP session with connection pooling
        connector = aiohttp.TCPConnector(
            limit=self.concurrency * 2,  # Connection pool size
            limit_per_host=self.concurrency * 2
        )
        
        async with aiohttp.ClientSession(connector=connector) as session:
            # Create worker tasks
            tasks = [
                asyncio.create_task(self.worker(session, i)) 
                for i in range(self.concurrency)
            ]
            
            # Wait for all workers to complete
            await asyncio.gather(*tasks)
        
        self.results['end_time'] = time.time()
        
    def print_results(self):
        """Print comprehensive test results"""
        duration = self.results['end_time'] - self.results['start_time']
        total_requests = self.results['total_requests']
        
        print("\n" + "=" * 60)
        print("🏁 LOAD TEST RESULTS")
        print("=" * 60)
        
        # Basic Stats
        print(f"📊 Test Summary:")
        print(f"   • Total Requests: {total_requests:,}")
        print(f"   • Duration: {duration:.2f} seconds")
        print(f"   • Throughput: {total_requests/duration:.2f} requests/second")
        print(f"   • Concurrency: {self.concurrency}")
        
        # Response Code Distribution
        print(f"\n📈 Response Codes:")
        success_codes = 0
        client_errors = 0
        server_errors = 0
        
        for code, count in sorted(self.results['response_codes'].items()):
            percentage = (count / total_requests) * 100
            print(f"   • {code}: {count:,} ({percentage:.1f}%)")
            
            if isinstance(code, int):
                if 200 <= code < 300:
                    success_codes += count
                elif 400 <= code < 500:
                    client_errors += count
                elif 500 <= code < 600:
                    server_errors += count
        
        print(f"\n🎯 Response Summary:")
        print(f"   • 2XX (Success): {success_codes:,} ({(success_codes/total_requests)*100:.1f}%)")
        print(f"   • 4XX (Client Error): {client_errors:,} ({(client_errors/total_requests)*100:.1f}%)")
        print(f"   • 5XX (Server Error): {server_errors:,} ({(server_errors/total_requests)*100:.1f}%)")
        
        # Latency Statistics
        if self.results['latencies']:
            latencies = self.results['latencies']
            print(f"\n⚡ Latency Statistics (milliseconds):")
            print(f"   • Min: {min(latencies):.1f}ms")
            print(f"   • Max: {max(latencies):.1f}ms")
            print(f"   • Mean: {statistics.mean(latencies):.1f}ms")
            print(f"   • Median: {statistics.median(latencies):.1f}ms")
            
            # Percentiles
            sorted_latencies = sorted(latencies)
            p95 = sorted_latencies[int(0.95 * len(sorted_latencies))]
            p99 = sorted_latencies[int(0.99 * len(sorted_latencies))]
            print(f"   • 95th percentile: {p95:.1f}ms")
            print(f"   • 99th percentile: {p99:.1f}ms")
        
        # Error Summary
        if self.results['errors']:
            print(f"\n❌ Errors ({len(self.results['errors'])}):")
            error_summary = Counter()
            for error in self.results['errors'][:10]:  # Show first 10 errors
                error_type = error.split(':')[0]
                error_summary[error_type] += 1
            
            for error_type, count in error_summary.items():
                print(f"   • {error_type}: {count}")
            
            if len(self.results['errors']) > 10:
                print(f"   • ... and {len(self.results['errors']) - 10} more")
        
        # Generate JSON report
        self.save_json_report()
        
        print(f"\n💾 Detailed report saved to: load_test_results.json")
        print("=" * 60)
    
    def save_json_report(self):
        """Save detailed results to JSON file"""
        report = {
            'test_config': {
                'base_url': self.base_url,
                'concurrency': self.concurrency,
                'duration': self.duration,
                'airports_count': len(self.airports)
            },
            'results': {
                'total_requests': self.results['total_requests'],
                'duration': self.results['end_time'] - self.results['start_time'],
                'throughput': self.results['total_requests'] / (self.results['end_time'] - self.results['start_time']),
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
        
        with open('load_test_results.json', 'w') as f:
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
    tester = FlightSearchLoadTester(concurrency=concurrency, duration=duration)
    
    # Load airports
    if not tester.load_airports():
        return 1
    
    print(f"🛫 Testing with {len(tester.airports)} airports")
    print("Sample airports:", tester.airports[:10])
    
    # Run the test
    try:
        await tester.run_load_test()
        tester.print_results()
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
