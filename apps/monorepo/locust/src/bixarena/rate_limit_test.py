"""
BixArena Rate Limiting Load Test

This load test demonstrates the rate limiting functionality of the BixArena API Gateway.
It includes multiple test scenarios to verify different rate limit configurations.

Rate Limit Configuration:
- Window: 1 minute (sliding window)
- Algorithm: Sliding window counter with Valkey (Redis)
- Identity: Session-based (JSESSIONID) or IP-based (anonymous)

Expected Behavior:
- Requests within limit: HTTP 200/201/204
- Requests exceeding limit: HTTP 429 Too Many Requests
- Response headers: X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset, Retry-After

Usage:
  # Run with GUI (monitor in real-time)
  uv run locust -f src/bixarena/rate_limit_test.py --host=http://localhost:8113

  # Run headless for 2 minutes with 5 concurrent users
  uv run locust -f src/bixarena/rate_limit_test.py --host=http://localhost:8113 \
    --headless -u 5 -r 5 -t 2m --csv=rate_limit_results

  # Test specific scenario
  uv run locust -f src/bixarena/rate_limit_test.py --host=http://localhost:8113 \
    --headless -u 1 -r 1 -t 1m --tags low-limit
"""

import time
from locust import HttpUser, task, tag, events, between
from locust.exception import StopUser

# Global counters for custom metrics
rate_limited_requests = 0
successful_requests = 0
total_requests = 0


class RateLimitTestUser(HttpUser):
    """
    User class for testing rate limiting behavior.

    This user simulates realistic traffic patterns and validates rate limit responses.
    """

    # Wait between 0.5 and 1 second between requests (aggressive testing)
    wait_time = between(0.5, 1)

    # Track rate limit hits for reporting
    rate_limit_hits = 0
    successful_requests = 0

    def on_start(self):
        """
        Initialize user session.

        For authenticated testing, you could acquire a JSESSIONID here via:
        1. Call /auth/login endpoint
        2. Follow OAuth flow
        3. Extract JSESSIONID from response cookies
        4. Store in self.client.cookies for subsequent requests

        For now, we test as anonymous user (IP-based rate limiting).
        """
        print(f"Starting load test user: {id(self)}")
        self.client.headers.update(
            {
                "User-Agent": f"Locust-RateLimitTest/{id(self)}",
            }
        )

    def on_stop(self):
        """Report rate limit statistics when user stops."""
        total = self.rate_limit_hits + self.successful_requests
        if total > 0:
            rate_limit_percentage = (self.rate_limit_hits / total) * 100
            print(
                f"User {id(self)} stopped - "
                f"Successful: {self.successful_requests}, "
                f"Rate Limited: {self.rate_limit_hits} "
                f"({rate_limit_percentage:.1f}%)"
            )

    @task(3)
    @tag("high-limit", "anonymous")
    def test_public_stats_endpoint(self):
        """
        Test public stats endpoint (100 req/min limit).

        This is an anonymous endpoint with moderate rate limiting.
        Good for baseline testing.
        """
        with self.client.get(
            "/api/v1/stats", catch_response=True, name="GET /api/v1/stats (100/min)"
        ) as response:
            self._handle_response(response, expected_limit=100)

    @task(3)
    @tag("high-limit", "anonymous")
    def test_example_prompts_endpoint(self):
        """
        Test example prompts endpoint (100 req/min limit).

        Anonymous endpoint with pagination support.
        """
        with self.client.get(
            "/api/v1/example-prompts?page=0&size=10",
            catch_response=True,
            name="GET /api/v1/example-prompts (100/min)",
        ) as response:
            self._handle_response(response, expected_limit=100)

    @task(3)
    @tag("high-limit", "anonymous")
    def test_models_endpoint(self):
        """
        Test models listing endpoint (100 req/min limit).

        Anonymous endpoint for browsing available models.
        """
        with self.client.get(
            "/api/v1/models", catch_response=True, name="GET /api/v1/models (100/min)"
        ) as response:
            self._handle_response(response, expected_limit=100)

    @task(3)
    @tag("high-limit", "anonymous")
    def test_leaderboards_endpoint(self):
        """
        Test leaderboards listing endpoint (100 req/min limit).

        Anonymous endpoint for viewing leaderboards.
        """
        with self.client.get(
            "/api/v1/leaderboards",
            catch_response=True,
            name="GET /api/v1/leaderboards (100/min)",
        ) as response:
            self._handle_response(response, expected_limit=100)

    @task(2)
    @tag("very-high-limit", "anonymous")
    def test_battles_list_endpoint(self):
        """
        Test battles listing endpoint (300 req/min limit).

        High-traffic endpoint with higher rate limit.
        """
        with self.client.get(
            "/api/v1/battles?page=0&size=10",
            catch_response=True,
            name="GET /api/v1/battles (300/min)",
        ) as response:
            self._handle_response(response, expected_limit=300)

    @task(1)
    @tag("low-limit", "anonymous", "auth")
    def test_login_endpoint(self):
        """
        Test login initiation endpoint (20 req/min limit).

        Low limit to prevent auth flow abuse.
        This endpoint should hit rate limits quickly.
        """
        with self.client.get(
            "/auth/login",
            catch_response=True,
            name="GET /auth/login (20/min)",
            allow_redirects=False,  # Don't follow redirects
        ) as response:
            # Login endpoint may return 302 redirect or 204
            if response.status_code in [200, 204, 302]:
                self._handle_response(response, expected_limit=20)
            else:
                self._handle_response(response, expected_limit=20)

    @task(1)
    @tag("medium-limit", "anonymous")
    def test_jwks_endpoint(self):
        """
        Test JWKS endpoint (100 req/min limit).

        Public key endpoint for JWT verification.
        """
        with self.client.get(
            "/.well-known/jwks.json",
            catch_response=True,
            name="GET /.well-known/jwks.json (100/min)",
        ) as response:
            self._handle_response(response, expected_limit=100)

    def _handle_response(self, response, expected_limit: int):
        """
        Handle and validate rate limit response.

        Args:
            response: Locust response object
            expected_limit: Expected rate limit per minute
        """
        global rate_limited_requests, successful_requests, total_requests
        total_requests += 1

        # Check for rate limit headers
        headers = response.headers
        rate_limit = headers.get("X-RateLimit-Limit")
        remaining = headers.get("X-RateLimit-Remaining")
        reset = headers.get("X-RateLimit-Reset")

        # Log rate limit headers for debugging (only every 5th request to reduce noise)
        if rate_limit and total_requests % 5 == 0:
            print(
                f"[{response.request.method} {response.request.url}] "
                f"Limit: {rate_limit}, Remaining: {remaining}, Reset: {reset}s"
            )

        if response.status_code == 429:
            # Rate limit exceeded - this is expected behavior
            self.rate_limit_hits += 1
            rate_limited_requests += 1

            # Fire custom event for rate limit hit
            events.request.fire(
                request_type="RATE_LIMITED",
                name=f"{response.request.method} {response.request.path_url}",
                response_time=response.elapsed.total_seconds() * 1000,
                response_length=len(response.content),
                exception=None,
                context=self.context(),
            )

            # Validate rate limit response
            retry_after = headers.get("Retry-After")

            # Verify required headers are present
            if not rate_limit:
                response.failure(f"429 response missing X-RateLimit-Limit header")
                return

            if not retry_after:
                response.failure(f"429 response missing Retry-After header")
                return

            # Verify rate limit matches expected
            if int(rate_limit) != expected_limit:
                response.failure(
                    f"Rate limit header shows {rate_limit}, expected {expected_limit}"
                )
                return

            # Parse response body
            try:
                error_body = response.json()

                # Validate RateLimitErrorDto schema
                required_fields = [
                    "title",
                    "status",
                    "limit",
                    "window",
                    "retryAfterSeconds",
                ]
                for field in required_fields:
                    if field not in error_body:
                        response.failure(
                            f"429 response missing required field: {field}"
                        )
                        return

                # Validate values
                if error_body.get("status") != 429:
                    response.failure(
                        f"Error status is {error_body.get('status')}, expected 429"
                    )
                    return

                if error_body.get("limit") != expected_limit:
                    response.failure(
                        f"Error body limit is {error_body.get('limit')}, "
                        f"expected {expected_limit}"
                    )
                    return

                # Success - rate limit is working correctly
                response.success()
                print(
                    f"✓ Rate limit enforced: {error_body.get('title')} - "
                    f"Retry after {retry_after}s"
                )

            except Exception as e:
                response.failure(f"Failed to parse 429 response body: {e}")
                return

        elif response.status_code in [200, 201, 204, 302]:
            # Successful response
            self.successful_requests += 1
            successful_requests += 1

            # Verify rate limit headers are present
            if not rate_limit:
                response.failure(f"Success response missing X-RateLimit-Limit header")
                return

            # Verify limit matches expected
            if int(rate_limit) != expected_limit:
                response.failure(
                    f"Rate limit header shows {rate_limit}, expected {expected_limit}"
                )
                return

            response.success()

            # Warn if we're close to limit
            if remaining and int(remaining) < 5:
                print(f"⚠ Approaching rate limit: {remaining} requests remaining")

        else:
            # Unexpected status code
            response.failure(f"Unexpected status code: {response.status_code}")


class RateLimitBurstTestUser(HttpUser):
    """
    User class for burst testing - rapidly hits a single endpoint to trigger rate limits.

    This is useful for demonstrating how quickly rate limits are enforced.
    Use with low user count (1-2 users) to see clear rate limiting behavior.
    """

    # No wait time - send requests as fast as possible
    wait_time = between(0, 0.1)

    def on_start(self):
        """Initialize burst test user."""
        print("\n" + "=" * 70)
        print("🚀 Starting BURST TEST - Rapid Fire Mode")
        print("=" * 70)
        print("Target: /auth/login (20 req/min limit)")
        print("Strategy: Send requests as fast as possible")
        print("Expected: Rate limit hit around request 20")
        print("=" * 70 + "\n")
        self.requests_sent = 0
        self.rate_limited_at = None
        self.success_count = 0
        self.rate_limited_count = 0

    @task
    @tag("burst", "low-limit")
    def burst_test_login_endpoint(self):
        """
        Rapidly hit login endpoint (20 req/min limit).

        This should trigger rate limiting within 20 requests.
        """
        self.requests_sent += 1

        with self.client.get(
            "/auth/login",
            catch_response=True,
            name="BURST /auth/login (20/min)",
            allow_redirects=False,
        ) as response:
            if response.status_code == 429:
                self.rate_limited_count += 1
                if not self.rate_limited_at:
                    self.rate_limited_at = self.requests_sent
                    print(f"\n🚫 RATE LIMIT HIT after {self.requests_sent} requests!\n")
                    print(f"   Successful requests: {self.success_count}")
                    print(
                        f"   Rate limited from request {self.requests_sent} onwards\n"
                    )

                # Fire custom event
                events.request.fire(
                    request_type="RATE_LIMITED",
                    name="BURST /auth/login (429)",
                    response_time=response.elapsed.total_seconds() * 1000,
                    response_length=len(response.content),
                    exception=None,
                    context=self.context(),
                )
                response.success()
            elif response.status_code in [200, 204, 302]:
                self.success_count += 1
                response.success()
                if self.requests_sent % 5 == 0:
                    remaining = response.headers.get("X-RateLimit-Remaining")
                    print(
                        f"✓ Request #{self.requests_sent:>3} succeeded - "
                        f"{remaining} requests remaining"
                    )
            else:
                response.failure(f"Unexpected status: {response.status_code}")

        # Stop after demonstrating rate limit
        if self.rate_limited_at and self.requests_sent > self.rate_limited_at + 5:
            # Store results for the test_stop event to print
            self.environment.burst_test_results = {
                "total": self.requests_sent,
                "successful": self.success_count,
                "rate_limited": self.rate_limited_count,
                "triggered_at": self.rate_limited_at,
            }
            raise StopUser()


class RateLimitRecoveryTestUser(HttpUser):
    """
    User class for testing rate limit window recovery.

    This user will:
    1. Hit an endpoint until rate limited
    2. Wait for window to reset (60+ seconds)
    3. Verify requests succeed again
    """

    wait_time = between(0, 0.1)

    def on_start(self):
        """Initialize recovery test."""
        print(f"Starting RECOVERY test user: {id(self)}")
        self.phase = "hitting_limit"  # hitting_limit -> waiting -> verifying_recovery
        self.rate_limited_at = None
        self.wait_started_at = None

    @task
    @tag("recovery", "low-limit")
    def test_rate_limit_recovery(self):
        """
        Test that rate limits reset after the 1-minute window.
        """
        if self.phase == "hitting_limit":
            # Phase 1: Hit endpoint until rate limited
            with self.client.get(
                "/auth/login",
                catch_response=True,
                name="RECOVERY /auth/login (hitting limit)",
                allow_redirects=False,
            ) as response:
                if response.status_code == 429:
                    if not self.rate_limited_at:
                        self.rate_limited_at = time.time()
                        retry_after = response.headers.get("Retry-After", "60")
                        print(
                            f"🚫 Rate limited! Waiting {retry_after}s "
                            f"for window reset..."
                        )
                        self.phase = "waiting"
                        self.wait_started_at = time.time()
                    response.success()
                else:
                    response.success()

        elif self.phase == "waiting":
            # Phase 2: Wait for window to reset (60 seconds)
            elapsed = time.time() - self.wait_started_at
            if elapsed < 65:  # Wait 65 seconds to be safe
                time.sleep(1)
            else:
                print("⏰ Window should have reset. Verifying recovery...")
                self.phase = "verifying_recovery"

        elif self.phase == "verifying_recovery":
            # Phase 3: Verify requests succeed again
            with self.client.get(
                "/auth/login",
                catch_response=True,
                name="RECOVERY /auth/login (after reset)",
                allow_redirects=False,
            ) as response:
                if response.status_code == 429:
                    response.failure(
                        "Still rate limited after window reset! "
                        "Rate limiting may not be working correctly."
                    )
                    raise StopUser()
                elif response.status_code in [200, 204, 302]:
                    remaining = response.headers.get("X-RateLimit-Remaining")
                    print(
                        f"✅ Recovery successful! Window reset. Remaining: {remaining}"
                    )
                    response.success()
                    raise StopUser()  # Test complete
                else:
                    response.failure(f"Unexpected status: {response.status_code}")


@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """Print test configuration when test starts."""
    print("\n" + "=" * 70)
    print("BixArena Rate Limiting Load Test")
    print("=" * 70)
    print(f"Host: {environment.host}")
    print(
        f"Users: {environment.runner.target_user_count if hasattr(environment.runner, 'target_user_count') else 'N/A'}"
    )
    print("\nRate Limit Configuration:")
    print("  - Window: 1 minute (sliding)")
    print("  - Algorithm: Sliding window counter")
    print("  - Identity: IP-based (anonymous)")
    print("\nEndpoints Under Test:")
    print("  - /api/v1/stats (100 req/min)")
    print("  - /api/v1/example-prompts (100 req/min)")
    print("  - /api/v1/models (100 req/min)")
    print("  - /api/v1/leaderboards (100 req/min)")
    print("  - /api/v1/battles (300 req/min)")
    print("  - /auth/login (20 req/min) ⚠️ Low limit")
    print("  - /.well-known/jwks.json (100 req/min)")
    print("\nExpected Behavior:")
    print("  - Requests within limit: HTTP 200/204/302")
    print("  - Requests exceeding limit: HTTP 429")
    print("  - Headers: X-RateLimit-*, Retry-After")
    print("=" * 70 + "\n")


@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """Print summary when test completes."""
    global rate_limited_requests, successful_requests, total_requests

    print("\n" + "=" * 70)

    # Check if this was a burst test
    if hasattr(environment, "burst_test_results"):
        results = environment.burst_test_results
        print("🎯 BURST TEST COMPLETE")
        print("=" * 70)
        print(f"  Total requests sent:     {results['total']}")
        print(f"  ✅ Successful:            {results['successful']}")
        print(f"  🚫 Rate limited:          {results['rate_limited']}")
        print(f"  Rate limit triggered at: Request #{results['triggered_at']}")
        print("=" * 70)
        print("\n✓ Rate limiting demonstrated successfully!")
        print("  The /auth/login endpoint (20 req/min limit) correctly")
        print(
            f"  rate limited requests starting at request #{results['triggered_at']}."
        )
    else:
        # Regular test summary
        print("Test Complete - Rate Limiting Statistics")
        print("=" * 70)

        if total_requests > 0:
            rate_limit_percentage = (rate_limited_requests / total_requests) * 100
            success_percentage = (successful_requests / total_requests) * 100

            print(f"\n📊 Request Summary:")
            print(f"  Total Requests:      {total_requests:>6}")
            print(
                f"  ✅ Successful:        {successful_requests:>6} ({success_percentage:>5.1f}%)"
            )
            print(
                f"  🚫 Rate Limited:      {rate_limited_requests:>6} ({rate_limit_percentage:>5.1f}%)"
            )

            if rate_limited_requests > 0:
                print(
                    f"\n✓ Rate limiting is working! {rate_limited_requests} requests were "
                    f"rate limited as expected."
                )
            else:
                print(
                    "\n⚠ No rate limits hit. Try running longer or with more users "
                    "to exceed the limits."
                )
        else:
            print("\nNo requests were made during the test.")

    print("\n" + "=" * 70)
    print("📈 Locust Statistics Summary (above)")
    print("=" * 70)
    print("Note: In Locust stats, rate-limited (429) requests appear as")
    print("      'RATE_LIMITED' request type for better visibility.")
    print("\nBoth successful and rate-limited requests show 0% failures")
    print("because we're validating they work correctly, not treating")
    print("rate limits as errors.")
    print("=" * 70 + "\n")
