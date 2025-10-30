# BixArena Rate Limiting Load Test

This document explains how to use the Locust load tests to demonstrate and validate the BixArena API Gateway rate limiting functionality.

## Overview

The BixArena API Gateway implements distributed rate limiting using:

- **Technology**: Valkey (Redis-compatible) with sliding window algorithm
- **Window**: 1 minute (60 seconds)
- **Identity**: Session-based (authenticated) or IP-based (anonymous)
- **Response**: HTTP 429 Too Many Requests with retry information

## Rate Limit Configuration

### Endpoint Rate Limits

| Endpoint                  | Method | Limit (req/min) | Type          |
| ------------------------- | ------ | --------------- | ------------- |
| `/api/v1/stats`           | GET    | 100             | Anonymous     |
| `/api/v1/example-prompts` | GET    | 100             | Anonymous     |
| `/api/v1/models`          | GET    | 100             | Anonymous     |
| `/api/v1/leaderboards`    | GET    | 100             | Anonymous     |
| `/api/v1/battles`         | GET    | 300             | Mixed         |
| `/api/v1/battles`         | POST   | 30              | Authenticated |
| `/api/v1/battles/{id}`    | GET    | 300             | Mixed         |
| `/api/v1/battles/{id}`    | PATCH  | 30              | Authenticated |
| `/api/v1/battles/{id}`    | DELETE | 30              | Authenticated |
| `/auth/login`             | GET    | 20              | Anonymous     |
| `/auth/callback`          | GET    | 20              | Anonymous     |
| `/.well-known/jwks.json`  | GET    | 100             | Anonymous     |

### Response Headers

All responses include rate limit information:

- `X-RateLimit-Limit`: Maximum requests per minute
- `X-RateLimit-Remaining`: Remaining requests in current window
- `X-RateLimit-Reset`: Seconds until window resets

429 responses additionally include:

- `Retry-After`: Seconds to wait before retrying

### Response Format (429)

```json
{
  "title": "Rate Limit Exceeded",
  "status": 429,
  "detail": "Too many requests. Please try again later.",
  "limit": 100,
  "window": "1 minute",
  "retryAfterSeconds": 42
}
```

## Test Scenarios

The load test includes three specialized user classes:

### 1. RateLimitTestUser (General Testing)

Tests multiple endpoints with realistic traffic patterns.

**Endpoints tested**:

- Public stats (100/min)
- Example prompts (100/min)
- Models listing (100/min)
- Leaderboards (100/min)
- Battles listing (300/min)
- Login endpoint (20/min)
- JWKS endpoint (100/min)

**Validates**:

- Rate limit headers are present and correct
- 429 responses have proper error format
- Rate limits match configured values
- Retry-After headers are present

### 2. RateLimitBurstTestUser (Burst Testing)

Rapidly hits a single endpoint to quickly trigger rate limiting.

**Purpose**: Demonstrate how fast rate limits are enforced

**Behavior**:

1. Sends requests as fast as possible to `/auth/login` (20/min limit)
2. Reports when rate limit is hit
3. Stops after demonstrating rate limiting

### 3. RateLimitRecoveryTestUser (Window Reset Testing)

Tests that rate limits properly reset after the 1-minute window.

**Behavior**:

1. Hits endpoint until rate limited
2. Waits 65 seconds for window reset
3. Verifies requests succeed again
4. Reports success or failure

## Running the Tests

### Prerequisites

1. **Start BixArena services**:

   ```bash
   # Make sure API Gateway, Auth Service, and Valkey are running
   docker compose up -d
   ```

2. **Install dependencies**:
   ```bash
   cd /workspaces/sage-monorepo/apps/monorepo/locust
   uv sync
   ```

### Test Execution

#### Option 1: Interactive GUI (Recommended for Exploration)

```bash
cd /workspaces/sage-monorepo
nx serve monorepo-locust
```

Or directly:

```bash
cd /workspaces/sage-monorepo/apps/monorepo/locust
uv run locust -f src/bixarena/rate_limit_test.py --host=http://localhost:8113
```

Then:

1. Open browser to http://localhost:8089
2. Configure:
   - Number of users: 5-10 for general testing
   - Spawn rate: 1-2 users per second
   - Host: http://localhost:8113
3. Click "Start swarming"
4. Monitor results in real-time

**Tips**:

- Use "Charts" tab to see request rate and response times
- Use "Failures" tab to see rate limit responses (they're not actual failures!)
- Use "Exceptions" tab to see any unexpected errors

#### Option 2: Headless (Automated Testing)

**General Rate Limit Test** (2 minutes, 5 users):

```bash
uv run locust -f src/bixarena/rate_limit_test.py \
  --host=http://localhost:8113 \
  --headless \
  -u 5 \
  -r 1 \
  -t 2m \
  --csv=rate_limit_general
```

**Burst Test** (Hit low-limit endpoint rapidly):

```bash
uv run locust -f src/bixarena/rate_limit_test.py \
  --host=http://localhost:8113 \
  --headless \
  -u 1 \
  -r 1 \
  -t 1m \
  --tags burst \
  --csv=rate_limit_burst
```

**Recovery Test** (Verify window reset):

```bash
uv run locust -f src/bixarena/rate_limit_test.py \
  --host=http://localhost:8113 \
  --headless \
  -u 1 \
  -r 1 \
  -t 2m \
  --tags recovery \
  --csv=rate_limit_recovery
```

**Test Specific Endpoints**:

```bash
# Test only low-limit endpoints (login, callback)
uv run locust -f src/bixarena/rate_limit_test.py \
  --host=http://localhost:8113 \
  --headless \
  -u 3 \
  -r 1 \
  -t 2m \
  --tags low-limit

# Test only high-limit endpoints (battles, stats)
uv run locust -f src/bixarena/rate_limit_test.py \
  --host=http://localhost:8113 \
  --headless \
  -u 10 \
  -r 2 \
  -t 2m \
  --tags high-limit
```

### Command-Line Options

- `-u, --users`: Number of concurrent users
- `-r, --spawn-rate`: Users to spawn per second
- `-t, --run-time`: Test duration (e.g., "1m", "30s", "2h")
- `--headless`: Run without web UI
- `--tags`: Run only tasks with specific tags
- `--csv`: CSV output file prefix
- `--html`: Generate HTML report
- `--host`: Target host URL

## Interpreting Results

### Expected Behavior

#### Phase 1: Initial Requests (Within Limit)

- Status: 200/204/302
- Headers: `X-RateLimit-Remaining` decreases with each request
- Response: Normal data

#### Phase 2: Rate Limit Exceeded

- Status: 429
- Headers: `X-RateLimit-Remaining: 0`, `Retry-After: <seconds>`
- Response: RateLimitErrorDto JSON

#### Phase 3: Window Reset (After ~60 seconds)

- Status: 200/204/302 again
- Headers: `X-RateLimit-Remaining` resets to limit
- Response: Normal data resumes

### Console Output

The test provides detailed console output:

```
Starting load test user: 140234567890
[GET http://localhost:8113/api/v1/stats] Limit: 100, Remaining: 95, Reset: 42s
✓ Request 5 succeeded (95 remaining)
⚠ Approaching rate limit: 4 requests remaining
🚫 Rate limit hit after 20 requests
✓ Rate limit enforced: Rate Limit Exceeded - Retry after 38s
```

### CSV Output Files

After headless tests, CSV files are generated:

- `rate_limit_general_stats.csv`: Overall statistics per endpoint
- `rate_limit_general_stats_history.csv`: Timeline of response times
- `rate_limit_general_failures.csv`: Rate limit responses (not failures!)
- `rate_limit_general_exceptions.csv`: Unexpected errors

**Key Metrics**:

- **Request Count**: Total requests sent
- **Failure Count**: Number of 429 responses (expected!)
- **Median Response Time**: Typical response time
- **95th Percentile**: Response time for 95% of requests
- **Requests/s**: Throughput

### Locust Web UI

When running with GUI:

1. **Statistics Tab**:

   - View per-endpoint request counts and response times
   - 429 responses appear as "failures" but this is expected behavior

2. **Charts Tab**:

   - Total Requests per Second (should spike then level off as rate limits hit)
   - Response Times (should stay low, even for 429s)
   - Number of Users (active concurrent users)

3. **Failures Tab**:
   - Rate limit 429s appear here
   - Should show proper error messages with retry information

## Monitoring Rate Limiting

### Prometheus Metrics

The API Gateway exposes rate limiting metrics at `http://localhost:8113/actuator/prometheus`:

```
# Rate limit checks
bixarena_gateway_rate_limit_checks_total{key_type="ip"}

# Rate limits exceeded
bixarena_gateway_rate_limit_exceeded_total{key_type="ip",path="/api/v1/stats"}

# Valkey operation latency
bixarena_gateway_rate_limit_valkey_latency_seconds{operation="check"}

# Errors
bixarena_gateway_rate_limit_errors_total{error_type="valkey_timeout"}
```

### Valkey Monitoring

Check rate limit keys in Valkey:

```bash
# Connect to Valkey
docker exec -it bixarena-valkey redis-cli -p 8116

# List rate limit keys
KEYS bixarena:gateway:ratelimit:*

# Check specific key (sorted set with timestamps)
ZRANGE bixarena:gateway:ratelimit:ip:127.0.0.1 0 -1 WITHSCORES

# Count requests in current window
ZCOUNT bixarena:gateway:ratelimit:ip:127.0.0.1 <now-60s> <now>

# Check TTL
TTL bixarena:gateway:ratelimit:ip:127.0.0.1
```

## Troubleshooting

### Issue: No Rate Limiting Occurring

**Possible causes**:

1. Rate limiting disabled in configuration

   - Check `apps/bixarena/api-gateway/src/main/resources/application.yml`
   - Verify `app.rate-limit.enabled: true`

2. Valkey not running

   - Check: `docker ps | grep valkey`
   - Start: `docker compose up -d bixarena-valkey`

3. Testing wrong host/port
   - API Gateway runs on port **8113**
   - Use `--host=http://localhost:8113`

### Issue: All Requests Failing

**Possible causes**:

1. Services not running

   - Check: `docker compose ps`
   - Start: `docker compose up -d`

2. Wrong port
   - API Gateway: 8113
   - Not 8000 (app) or 8112 (auth)

### Issue: Rate Limits Not Resetting

**Possible causes**:

1. Valkey persistence issue

   - Check Valkey logs: `docker logs bixarena-valkey`
   - Restart: `docker compose restart bixarena-valkey`

2. System clock drift
   - Valkey uses timestamps for sliding window
   - Ensure system clock is accurate

### Issue: Inconsistent Rate Limiting

**Possible causes**:

1. Multiple API Gateway instances

   - Rate limiting is per-instance without coordination
   - Use single instance or implement distributed coordination

2. IP address changes
   - Docker networking may use different source IPs
   - Use session-based rate limiting (requires authentication)

## Advanced Testing

### Testing with Authentication

To test session-based rate limiting:

1. **Acquire session cookie**:

   ```python
   def on_start(self):
       # Perform login flow
       response = self.client.get("/auth/login", allow_redirects=True)
       # Extract JSESSIONID from cookies
       jsessionid = response.cookies.get("JSESSIONID")
       # Store for subsequent requests
       self.client.cookies.set("JSESSIONID", jsessionid)
   ```

2. **Make authenticated requests**:
   - Cookie automatically included in requests
   - Rate limiting now per-session instead of per-IP

### Testing Multiple User Sessions

```python
class AuthenticatedRateLimitUser(HttpUser):
    def on_start(self):
        # Each user gets unique session
        self.authenticate()

    def authenticate(self):
        # Implement OAuth flow
        pass
```

### Stress Testing Rate Limits

To verify rate limiting under extreme load:

```bash
# 50 concurrent users hitting low-limit endpoint
uv run locust -f src/bixarena/rate_limit_test.py \
  --host=http://localhost:8113 \
  --headless \
  -u 50 \
  -r 10 \
  -t 5m \
  --tags low-limit
```

Expected behavior:

- Most requests should be rate limited (429)
- Gateway should remain stable and responsive
- 429 responses should be fast (<50ms)

## Reference

### Related Documentation

- [RATE_LIMITING.md](../../../apps/bixarena/api-gateway/RATE_LIMITING.md) - Rate limiting architecture
- [Locust Documentation](https://docs.locust.io/) - Load testing framework
- [RFC 6585](https://tools.ietf.org/html/rfc6585#section-4) - HTTP 429 Too Many Requests

### Configuration Files

- `apps/bixarena/api-gateway/src/main/resources/application.yml` - Gateway configuration
- `apps/bixarena/api-gateway/src/main/resources/routes.yml` - Route-specific limits

### Monitoring

- Prometheus metrics: http://localhost:8113/actuator/prometheus
- Health check: http://localhost:8113/actuator/health
- Locust UI: http://localhost:8089 (when running with GUI)
