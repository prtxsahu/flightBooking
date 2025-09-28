#!/bin/bash

# Neo4j Initialization Script
# This script waits for Neo4j to be ready and then loads the initialization data

set -e

echo "Waiting for Neo4j to be ready..."

# Wait for Neo4j to be available
until cypher-shell -u neo4j -p neo4j_pass "RETURN 1" > /dev/null 2>&1; do
    echo "Neo4j is not ready yet. Waiting..."
    sleep 5
done

echo "Neo4j is ready! Loading initialization data..."

# Load the initialization script
cypher-shell -u neo4j -p neo4j_pass < /var/lib/neo4j/import/01-init-neo4j.cypher

echo "Neo4j initialization completed successfully!"

# Keep the script running to prevent container exit
tail -f /dev/null
