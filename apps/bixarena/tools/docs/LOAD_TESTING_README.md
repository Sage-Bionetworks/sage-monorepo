# Monorepo Locust

Load testing suite for the Sage Monorepo applications using [Locust](https://locust.io/).

## Available Test Suites

### BixArena Tests

#### General Application Tests

- **File**: `src/bixarena/locustfile.py`
- **Description**: Basic application load testing for BixArena endpoints
- **Usage**:
  ```bash
  nx serve monorepo-locust
  # or
  uv run locust -f src/bixarena/locustfile.py --host=http://localhost:8000
  ```

#### Rate Limiting Tests

- **File**: `src/bixarena/rate_limit_test.py`
- **Description**: Comprehensive load tests for demonstrating and validating API Gateway rate limiting
- **Documentation**: See [RATE_LIMIT_TESTING.md](./RATE_LIMIT_TESTING.md) for detailed information
- **Quick Start**:

  ```bash
  # Interactive menu
  ./run_rate_limit_tests.sh

  # Or run specific test directly
  ./run_rate_limit_tests.sh burst
  ./run_rate_limit_tests.sh demo
  ```

## Quick Start

### Prerequisites

1. Install dependencies:

   ```bash
   uv sync
   ```

2. Ensure target services are running:
   ```bash
   docker compose up -d
   ```

### Running Tests

#### Interactive Mode (GUI)

Best for exploration and real-time monitoring:

```bash
nx serve monorepo-locust
```

Then open http://localhost:8089 in your browser.

#### Headless Mode (Automated)

Best for CI/CD and automated testing:

```bash
uv run locust -f src/bixarena/locustfile.py \
  --host=http://localhost:8000 \
  --headless \
  -u 10 \
  -r 2 \
  -t 2m \
  --csv=results
```

## Configuration

Configuration is managed via `pyproject.toml`:

```toml
[tool.locust]
headless = false
users = 1
spawn-rate = 1
run-time = "2m"
csv = "locust"
```

## Common Options

- `-u, --users <n>`: Number of concurrent users
- `-r, --spawn-rate <n>`: Users to spawn per second
- `-t, --run-time <time>`: Test duration (e.g., "1m", "30s")
- `--host <url>`: Target host URL
- `--headless`: Run without web UI
- `--csv <prefix>`: Save results to CSV files
- `--html <file>`: Generate HTML report
- `--tags <tags>`: Run only tasks with specified tags

## Output

Results are saved in the following formats:

- CSV: Statistics, failures, exceptions
- HTML: Comprehensive test report
- Console: Real-time progress

## Documentation

- [Rate Limit Testing Guide](./RATE_LIMIT_TESTING.md) - Detailed guide for rate limiting tests
- [Locust Documentation](https://docs.locust.io/) - Official Locust framework docs

## Development

### Adding New Tests

1. Create a new Python file in `src/<project>/`
2. Define user classes inheriting from `HttpUser`
3. Add tasks using `@task` decorator
4. Run with `uv run locust -f src/<project>/<file>.py`

Example:

```python
from locust import HttpUser, task, between

class MyUser(HttpUser):
    wait_time = between(1, 3)

    @task
    def my_task(self):
        self.client.get("/my-endpoint")
```

### Running Specific Test Scenarios

Use tags to organize and run specific test scenarios:

```bash
# Run only tests tagged with "burst"
uv run locust -f src/bixarena/rate_limit_test.py --tags burst

# Run only authenticated tests
uv run locust -f src/bixarena/rate_limit_test.py --tags auth
```
