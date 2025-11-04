#!/bin/bash

# Script to run Locust performance tests
# Usage: ./run-locust.sh [scenario] [users] [spawn-rate] [duration]

SCENARIO=${1:-"ECommerceUser"}
USERS=${2:-10}
SPAWN_RATE=${3:-2}
DURATION=${4:-60}
HOST=${5:-"http://localhost:8080"}

echo "=========================================="
echo "Starting Locust Performance Tests"
echo "=========================================="
echo "Scenario: ${SCENARIO}"
echo "Users: ${USERS}"
echo "Spawn Rate: ${SPAWN_RATE} users/second"
echo "Duration: ${DURATION} seconds"
echo "Host: ${HOST}"
echo "=========================================="

# Install dependencies if needed
if [ ! -d "venv" ]; then
    echo "Creating virtual environment..."
    python3 -m venv venv
    source venv/bin/activate
    pip install -r requirements.txt
else
    source venv/bin/activate
fi

# Run Locust
locust -f locustfile.py \
    --host=${HOST} \
    --users=${USERS} \
    --spawn-rate=${SPAWN_RATE} \
    --run-time=${DURATION}s \
    --headless \
    --html=reports/report_${SCENARIO}_${USERS}users_$(date +%Y%m%d_%H%M%S).html \
    --csv=reports/report_${SCENARIO}_${USERS}users_$(date +%Y%m%d_%H%M%S) \
    --loglevel=INFO \
    --class-name=${SCENARIO}

echo ""
echo "=========================================="
echo "Performance tests completed!"
echo "Reports saved in: reports/"
echo "=========================================="

