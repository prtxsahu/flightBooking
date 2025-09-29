# Scripts Directory

This directory contains utility scripts for the flight booking system.

## Structure

- **data-generation/**: Scripts for generating test data
- **load-testing/**: Scripts for performance testing

## Data Generation Scripts

- `generate-flights.py` - Python script to generate flight data
- `flight-generation-summary.json` - Summary of generated data
- `airports.txt` - List of airports used in data generation

## Load Testing Scripts

- `load_test_search.py` - Python script for load testing the search API
- `load_test_results.json` - Results from load testing
- `README_load_test.md` - Documentation for load testing

## Usage

Run the scripts from their respective directories:

```bash
# Generate data
cd scripts/data-generation
python generate-flights.py

# Run load tests
cd scripts/load-testing
python load_test_search.py
```
