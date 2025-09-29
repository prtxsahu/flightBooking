# Flight Search API Load Testing

## Overview
This Python script performs comprehensive load testing on the flight search API with realistic parameters.

## Features
- ✅ Uses all 98 airports from the database for source/destination combinations
- ✅ Random passenger counts (1-9)  
- ✅ Random dates between October 1-30, 2025
- ✅ Configurable concurrency (default: 10)
- ✅ Configurable test duration (default: 15 seconds)
- ✅ Comprehensive metrics collection

## Requirements
```bash
pip3 install aiohttp
```

## Usage

### Basic Usage (10 concurrency, 15 seconds)
```bash
python3 load_test_search.py
```

### Custom Concurrency and Duration
```bash
# 20 concurrent users for 30 seconds
python3 load_test_search.py 20 30

# 5 concurrent users for 60 seconds  
python3 load_test_search.py 5 60
```

## Test Results

### Latest Test Results (10 concurrency, 15 seconds):
- **Total Requests**: 2,622
- **Throughput**: 174.4 requests/second
- **Success Rate**: 78.4% (2XX responses)
- **Average Latency**: 46.3ms
- **95th Percentile**: 99.7ms

### Response Distribution:
- **200 OK**: 2,056 (78.4%) - Successful searches
- **500 Internal Server Error**: 566 (21.6%) - Server errors

## Files Generated
- `load_test_results.json` - Detailed test results in JSON format
- `airports.txt` - List of 98 airports used for testing

## Test Scenarios
Each request uses:
- **Source Airport**: Randomly selected from 98 airports
- **Destination Airport**: Randomly selected (different from source)
- **Departure Date**: Random date between 2025-10-01 and 2025-10-30
- **Passenger Count**: Random number between 1-9

## Metrics Collected
1. **Response Codes**: 2XX, 4XX, 5XX counts and percentages
2. **Latency**: Min, Max, Mean, Median, 95th, 99th percentiles
3. **Throughput**: Requests per second
4. **Error Analysis**: Timeout and connection errors
5. **Duration**: Total test runtime

## Understanding the Results

### Good Performance Indicators:
- High 2XX response rate (>90%)
- Low average latency (<100ms)
- Consistent throughput
- Few timeout/connection errors

### Areas for Investigation:
- High 5XX error rate may indicate server overload
- High latency may suggest database query optimization needed
- Low throughput may indicate capacity limits

## Customization
The script can be easily modified to:
- Test different date ranges
- Adjust passenger count ranges
- Test specific airport pairs
- Add more detailed metrics
- Change request timing patterns
