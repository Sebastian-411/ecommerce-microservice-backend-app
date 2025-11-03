# Locust Performance and Stress Testing

This directory contains Locust-based performance and stress tests for the e-commerce microservices application.

## Overview

Locust is used to simulate realistic load scenarios and stress test the microservices architecture. The tests cover various user behaviors and system loads.

## Test Scenarios

### 1. ECommerceUser (Default)
Simulates typical e-commerce user behavior:
- Browsing product catalog (high frequency)
- Viewing product details
- Creating products
- Viewing user profiles
- Creating orders

**Weight Distribution:**
- Browse products: 3x
- View product details: 2x
- Other operations: 1x

### 2. SequentialOrderFlow
Simulates complete order placement flow:
- Step 1: Browse products
- Step 2: View product details
- Step 3: Create order

### 3. HighLoadUser
High-frequency API calls for stress testing:
- Rapid concurrent operations
- Multiple endpoint testing

### 4. StressTestUser
Extreme stress testing scenario:
- Maximum load simulation
- Very fast request rate

## Installation

### Option 1: Using Docker
```bash
docker-compose -f docker-compose-locust.yml up
```

Then access Locust UI at: `http://localhost:8089`

### Option 2: Local Installation

1. Create virtual environment:
```bash
python3 -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

2. Install dependencies:
```bash
pip install -r requirements.txt
```

## Running Tests

### Interactive Mode (Web UI)

1. Start the microservices application
2. Run Locust:
```bash
locust -f locustfile.py --host=http://localhost:8080
```

3. Open browser: `http://localhost:8089`

4. Configure test:
   - Number of users (total users to simulate)
   - Spawn rate (users spawned per second)
   - Host URL (default: http://localhost:8080)

### Headless Mode (Command Line)

Run tests from command line:

```bash
./run-locust.sh [scenario] [users] [spawn-rate] [duration]
```

Examples:

```bash
# Run default scenario with 10 users
./run-locust.sh ECommerceUser 10 2 60

# Run stress test with 100 users
./run-locust.sh StressTestUser 100 10 120

# Run order flow scenario
./run-locust.sh OrderFlowUser 20 5 90
```

### Specific Scenarios

#### Test ECommerceUser:
```bash
locust -f locustfile.py --host=http://localhost:8080 --users=50 --spawn-rate=5 --run-time=5m --class-name=ECommerceUser
```

#### Test Sequential Order Flow:
```bash
locust -f locustfile.py --host=http://localhost:8080 --users=20 --spawn-rate=2 --run-time=3m --class-name=OrderFlowUser
```

#### Stress Test:
```bash
locust -f locustfile.py --host=http://localhost:8080 --users=200 --spawn-rate=20 --run-time=10m --class-name=StressTestUser
```

## Performance Metrics

Locust provides the following metrics:

1. **Requests per second (RPS)**: Number of requests handled per second
2. **Response time**: 
   - Min: Minimum response time
   - Max: Maximum response time
   - Median: 50th percentile
   - 95th percentile: 95% of requests below this time
   - 99th percentile: 99% of requests below this time
3. **Failures**: Number and percentage of failed requests
4. **User count**: Current number of simulated users

## Test Results

Test results are saved in the `reports/` directory:
- HTML reports: Visual representation of test results
- CSV files: Raw data for analysis

## Real-World Test Scenarios

### Scenario 1: Normal Load
- Users: 50
- Spawn rate: 5 users/second
- Duration: 5 minutes
- Expected RPS: ~100-200

### Scenario 2: Peak Load
- Users: 200
- Spawn rate: 20 users/second
- Duration: 10 minutes
- Expected RPS: ~500-800

### Scenario 3: Stress Test
- Users: 500
- Spawn rate: 50 users/second
- Duration: 15 minutes
- Expected RPS: ~1000+

### Scenario 4: Spike Test
- Users: 1000
- Spawn rate: 100 users/second
- Duration: 2 minutes
- Expected RPS: ~2000+

## Analyzing Results

### Key Metrics to Monitor

1. **Response Times**:
   - 95th percentile should be < 500ms for good UX
   - 99th percentile should be < 1000ms

2. **Error Rate**:
   - Should be < 0.1% under normal load
   - Should be < 1% under stress

3. **Throughput**:
   - Monitor requests per second
   - Ensure system can handle expected load

### Performance Targets

| Metric | Normal Load | Peak Load | Stress Test |
|--------|------------|-----------|-------------|
| RPS | 100-200 | 500-800 | 1000+ |
| 95th percentile | < 500ms | < 1000ms | < 2000ms |
| Error rate | < 0.1% | < 0.5% | < 1% |

## Best Practices

1. **Start Small**: Begin with low user counts and gradually increase
2. **Monitor Resources**: Watch CPU, memory, and network usage during tests
3. **Test Incrementally**: Test one scenario at a time
4. **Baseline First**: Establish baseline metrics before making changes
5. **Realistic Load**: Use realistic wait times between requests
6. **Monitor Errors**: Pay attention to error rates and types

## Troubleshooting

### Issue: Connection Refused
- Ensure microservices are running
- Check host URL and port

### Issue: High Error Rate
- Check microservice health
- Verify database connectivity
- Check resource limits (CPU, memory)

### Issue: Slow Response Times
- Check database performance
- Verify network latency
- Check service dependencies

## Integration with CI/CD

Add to Jenkins pipeline:

```groovy
stage('Performance Tests') {
    steps {
        sh '''
            cd tests/performance
            ./run-locust.sh ECommerceUser 50 5 300
        '''
    }
    post {
        always {
            archiveArtifacts 'tests/performance/reports/**'
        }
    }
}
```

## Custom Test Scenarios

To create custom test scenarios:

1. Create a new class extending `HttpUser`
2. Define `@task` methods with appropriate weights
3. Use `wait_time = between(min, max)` for realistic behavior
4. Add error handling with `catch_response=True`

Example:
```python
class CustomUser(HttpUser):
    wait_time = between(1, 3)
    
    @task(3)
    def my_custom_task(self):
        with self.client.get("/api/endpoint", catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Failed: {response.status_code}")
```

## Notes

- Ensure all microservices are running before starting tests
- Start with lower user counts to avoid overwhelming the system
- Monitor system resources during testing
- Review reports after each test run to identify bottlenecks

