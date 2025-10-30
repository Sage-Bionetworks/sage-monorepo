# Interpreting Rate Limit Test Results

This guide helps you understand the output from the BixArena rate limiting load tests.

## Quick Answer: Is Rate Limiting Working?

### ✅ Rate Limiting IS Working If You See:

1. **In Console Output:**

   ```
   🚫 RATE LIMIT HIT after 20 requests!
   ✓ Rate limiting is working! X requests were rate limited as expected.
   ```

2. **In Final Statistics:**

   ```
   📊 Request Summary:
     Total Requests:          28
     ✅ Successful:            20 ( 71.4%)
     🚫 Rate Limited:           8 ( 28.6%)
   ```

3. **In Locust Stats Table:**
   - You'll see **two separate request types**:
     - `GET /auth/login (20/min)` - Shows successful requests (200/302)
     - `RATE_LIMITED GET /auth/login (429)` - Shows rate limited requests
   - Both show `0.00%` failures because we mark them as successful validations

### ❌ Rate Limiting NOT Working If You See:

1. **All requests successful with no rate limits:**

   ```
   📊 Request Summary:
     Total Requests:         100
     ✅ Successful:          100 (100.0%)
     🚫 Rate Limited:          0 (  0.0%)

   ⚠ No rate limits hit. Try running longer or with more users to exceed the limits.
   ```

2. **Actual failures in Locust stats** (not rate limits)

## Understanding Locust Output

### Standard Locust Statistics Table

When you see this at the end of a test:

```
Type     Name                                    # reqs      # fails |    Avg     Min     Max    Med |   req/s  failures/s
--------|----------------------------------------|-------|-------------|-------|-------|-------|-------|--------|-----------
GET      BURST /auth/login (20/min)                 20     0(0.00%) |      7       3      14      9 |   12.50        0.00
RATE_LIMITED BURST /auth/login (429)                 8     0(0.00%) |      5       4       7      5 |    5.00        0.00
--------|----------------------------------------|-------|-------------|-------|-------|-------|-------|--------|-----------
         Aggregated                                 28     0(0.00%) |      6       3      14      7 |   17.50        0.00
```

**What this means:**

- **First row**: 20 successful requests (HTTP 200/302) before rate limit
- **Second row**: 8 rate-limited requests (HTTP 429) - **THIS IS GOOD!**
- **0 failures**: Both are marked as successful because we're validating they work correctly
- **Total**: 28 requests sent, rate limiting kicked in after ~20

### Custom Statistics Summary

At the end of tests, you'll see our custom summary:

```
======================================================================
Test Complete - Rate Limiting Statistics
======================================================================

📊 Request Summary:
  Total Requests:          28
  ✅ Successful:            20 ( 71.4%)
  🚫 Rate Limited:           8 ( 28.6%)

✓ Rate limiting is working! 8 requests were rate limited as expected.

======================================================================
```

**Key metrics:**

- **Total Requests**: All HTTP requests made during test
- **Successful**: Requests that got through (200/201/204/302)
- **Rate Limited**: Requests that hit the limit (429)

### Burst Test Output

During a burst test, you'll see real-time output:

```
🚀 Starting BURST TEST - Rapid Fire Mode
======================================================================
Target: /auth/login (20 req/min limit)
Strategy: Send requests as fast as possible
Expected: Rate limit hit around request 20
======================================================================

✓ Request #  5 succeeded - 15 requests remaining
✓ Request # 10 succeeded - 10 requests remaining
✓ Request # 15 succeeded -  5 requests remaining

🚫 RATE LIMIT HIT after 20 requests!

   Successful requests: 19
   Rate limited from request 20 onwards

======================================================================
🎯 BURST TEST COMPLETE
======================================================================
  Total requests sent:     26
  ✅ Successful:            19
  🚫 Rate limited:           7
  Rate limit triggered at: Request #20
======================================================================
```

**This shows:**

1. First 19 requests succeeded (within 20/min limit)
2. Request 20 hit the rate limit
3. Subsequent requests (21-26) were also rate limited
4. **This is perfect behavior!**

## Common Misunderstandings

### "But Locust shows 0% failures!"

**This is correct!** We mark rate-limited requests as "success" because:

- HTTP 429 is **expected behavior** we're testing
- We're validating the rate limiting works correctly
- Locust "failures" are for unexpected errors

To see rate limits, look for:

1. The `RATE_LIMITED` request type in stats
2. The custom summary showing "🚫 Rate Limited: X"
3. Console output showing "🚫 RATE LIMIT HIT"

### "I see 28 requests, all successful"

Check the **Locust statistics breakdown**:

- If you see only ONE request type → Rate limits may not have been hit
- If you see TWO request types (normal + RATE_LIMITED) → Rate limiting IS working

Example of working rate limiting:

```
GET      /auth/login (20/min)           20 requests  ← Successful
RATE_LIMITED /auth/login (429)           8 requests  ← Rate limited ✓
```

### "Rate limit is 20/min but 28 requests succeeded"

Look closer at the stats:

- 20 requests with HTTP 200/302 (successful)
- 8 requests with HTTP 429 (rate limited)
- **Total = 28**, but only 20 actually succeeded

The rate limiting worked correctly!

## Validating Rate Limit Headers

The test automatically validates:

### All Responses Must Have:

- `X-RateLimit-Limit`: The configured limit (e.g., 20, 100, 300)
- `X-RateLimit-Remaining`: Countdown of remaining requests
- `X-RateLimit-Reset`: Seconds until window resets

### 429 Responses Must Additionally Have:

- `Retry-After`: Seconds to wait before retrying
- JSON body with RateLimitErrorDto schema:
  ```json
  {
    "title": "Rate Limit Exceeded",
    "status": 429,
    "detail": "Too many requests. Please try again later.",
    "limit": 20,
    "window": "1 minute",
    "retryAfterSeconds": 42
  }
  ```

If any of these are missing, the test will show actual failures.

## Expected Rate Limit Percentages

Different tests have different expected percentages:

### Burst Test (1 user, low-limit endpoint)

- **Expected**: ~30-40% rate limited
- **Why**: Rapidly hitting a 20/min endpoint will quickly exceed the limit

### General Test (5 users, mixed endpoints)

- **Expected**: ~10-20% rate limited
- **Why**: Users spread across multiple endpoints with different limits

### Stress Test (50 users, mixed endpoints)

- **Expected**: ~50-70% rate limited
- **Why**: High concurrency will push most endpoints over their limits

## Viewing Detailed Results

### CSV Files

After headless tests, check CSV files in `./results/`:

```bash
# View overall statistics
cat ./results/rate_limit_burst_stats.csv

# View request timeline
cat ./results/rate_limit_burst_stats_history.csv
```

Look for TWO entries in stats:

1. Normal request type (e.g., `GET,BURST /auth/login (20/min)`)
2. Rate limited type (e.g., `RATE_LIMITED,BURST /auth/login (429)`)

### HTML Report

Open the HTML report in a browser:

```bash
# Linux
firefox ./results/rate_limit_burst_report.html

# macOS
open ./results/rate_limit_burst_report.html

# Windows
start ./results/rate_limit_burst_report.html
```

In the report, look for:

- **Statistics tab**: Shows both request types separately
- **Charts tab**: Request rate should spike then level off as limits hit
- **Failures tab**: Should be empty (429s are not failures)

## Troubleshooting

### Problem: No rate limits hit

**Possible causes:**

1. Test duration too short - Run for at least 1 minute
2. Not enough users - Try burst test with 1 user hitting one endpoint
3. Rate limiting disabled - Check gateway configuration
4. Wrong endpoint - Verify endpoint has rate limiting configured

**Solutions:**

```bash
# Run burst test - WILL hit rate limits
./run_rate_limit_tests.sh burst

# Run with more aggressive settings
uv run locust -f src/bixarena/rate_limit_test.py \
  --host=http://localhost:8113 \
  --headless \
  -u 10 \
  -r 10 \
  -t 2m \
  --tags low-limit
```

### Problem: All requests are rate limited

**Possible causes:**

1. Previous test didn't clean up
2. Multiple users sharing same IP
3. Rate limit window hasn't reset

**Solutions:**

1. Wait 60 seconds between tests
2. Restart Valkey: `docker compose restart bixarena-valkey`
3. Clear rate limit keys in Valkey

### Problem: Inconsistent results

**Possible causes:**

1. Multiple API Gateway instances
2. Valkey synchronization issues
3. Clock skew

**Solutions:**

1. Use single gateway instance for testing
2. Check Valkey connectivity
3. Verify system time is correct

## Quick Reference

| What You See                              | What It Means                    | Status   |
| ----------------------------------------- | -------------------------------- | -------- |
| `RATE_LIMITED` request type in stats      | Rate limiting triggered          | ✅ Good  |
| "🚫 Rate Limited: X" in summary           | X requests hit the limit         | ✅ Good  |
| "🚫 RATE LIMIT HIT after N requests"      | Limit hit after N requests       | ✅ Good  |
| All requests show 0% failure              | Tests validating correctly       | ✅ Good  |
| Two request types (normal + RATE_LIMITED) | Rate limiting working            | ✅ Good  |
| Only one request type, no RATE_LIMITED    | Rate limits not hit              | ⚠️ Check |
| Actual failures in stats                  | Validation errors                | ❌ Bad   |
| "⚠ No rate limits hit" message           | Need longer/more aggressive test | ⚠️ Check |

## Summary

**Rate limiting IS working correctly when you see:**

1. ✅ `RATE_LIMITED` as a separate request type in Locust stats
2. ✅ Custom summary showing "🚫 Rate Limited: X requests"
3. ✅ Console messages about rate limits being hit
4. ✅ Proper percentages (some successful, some rate limited)

**Your original burst test result showing 28 requests with 0 failures is CORRECT** - you just need to look at the breakdown to see that some were successful (200/302) and some were rate limited (429), both marked as "success" because they validated correctly.
