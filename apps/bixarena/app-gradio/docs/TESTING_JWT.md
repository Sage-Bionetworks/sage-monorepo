# Testing JWT Authentication Implementation

## Quick Test Plan

Follow these steps to test the JWT authentication flow end-to-end:

---

## Test 1: Verify Auth Service JWT Minting

### Step 1.1: Test /token endpoint manually

```bash
# First, get a session by logging in via browser
# Then extract the JSESSIONID cookie and test:

curl -v http://localhost:8115/v1/token \
  -H "Cookie: JSESSIONID=<your-session-id>" \
  -X POST
```

**Expected Result:**

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 900
}
```

**If you get 401 Unauthorized:**

- Session doesn't exist or is invalid
- Need to log in first via browser

---

## Test 2: Login and Get Session Cookie

### Step 2.1: Open browser and navigate to:

```
http://localhost:8100
```

### Step 2.2: Click "Login" button

- Should redirect to Synapse OIDC login
- Complete login with your Synapse credentials

### Step 2.3: Extract JSESSIONID cookie

**Using Chrome DevTools:**

1. Press F12 to open DevTools
2. Go to "Application" tab
3. Under "Storage" → "Cookies" → "http://localhost:8100"
4. Find the `JSESSIONID` cookie and copy its value

**Using curl after login:**

```bash
# This won't work directly - you need to log in via browser first
# Then you can inspect the cookie
```

---

## Test 3: Test JWT Minting from Command Line

Once you have the JSESSIONID:

```bash
# Replace <JSESSIONID> with your actual session ID
SESSION_ID="<JSESSIONID>"

# Mint a JWT
curl -X POST http://localhost:8115/v1/token \
  -H "Cookie: JSESSIONID=$SESSION_ID" \
  -v

# Save the access_token from the response
JWT="<access_token_from_response>"
```

---

## Test 4: Test JWT Validation by BixArena API

### Step 4.1: Call a protected endpoint WITHOUT JWT (should fail when protected)

```bash
# Currently this works because endpoints are permitAll
# After you protect them, this should return 401
curl http://localhost:8112/v1/leaderboards/open-source
```

### Step 4.2: Call the same endpoint WITH JWT

```bash
# Use the JWT from Test 3
curl http://localhost:8112/v1/leaderboards/open-source \
  -H "Authorization: Bearer $JWT" \
  -v
```

**Expected Result:**

- API accepts the JWT and returns data
- Response: 200 OK with leaderboard data

---

## Test 5: Test JWT in Gradio App (Current Implementation)

### Step 5.1: Add a test button to test JWT minting

Create a simple test function in `bixarena_app/page/bixarena_leaderboard.py`:

```python
from bixarena_app.auth.jwt_helper import get_jwt_from_request

def test_jwt_minting(request: gr.Request):
    """Test function to verify JWT minting works"""
    jwt = get_jwt_from_request(request)
    if jwt:
        # Decode the JWT to show it's valid (for testing only!)
        import base64
        import json

        # JWT format: header.payload.signature
        parts = jwt.split('.')
        payload = parts[1]
        # Add padding if needed
        payload += '=' * (4 - len(payload) % 4)
        decoded = json.loads(base64.urlsafe_b64decode(payload))

        return f"""
        ✅ JWT minted successfully!

        Subject: {decoded.get('sub')}
        Roles: {decoded.get('roles')}
        Expires: {decoded.get('exp')}
        Issuer: {decoded.get('iss')}

        Token (first 50 chars): {jwt[:50]}...
        """
    else:
        return "❌ Failed to mint JWT. Are you logged in?"

# Add this to your Gradio interface temporarily
test_btn = gr.Button("Test JWT Minting")
test_output = gr.Textbox(label="JWT Test Result")
test_btn.click(test_jwt_minting, inputs=[request], outputs=[test_output])
```

### Step 5.2: Test in browser

1. Navigate to http://localhost:8100
2. Log in
3. Click the "Test JWT Minting" button
4. Verify JWT is minted and shows your identity

---

## Test 6: End-to-End API Call with JWT

### Step 6.1: Protect an API endpoint

Temporarily update `SecurityConfiguration.java` to require auth:

```java
.authorizeHttpRequests(authz ->
  authz
    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
    .permitAll()
    // PROTECTED - now requires JWT
    .requestMatchers("/v1/leaderboards/**")
    .authenticated()
    // ... rest of config
)
```

### Step 6.2: Rebuild and restart bixarena-api

```bash
nx build bixarena-api
docker-compose -f docker/bixarena/docker-compose.yml restart bixarena-api
```

### Step 6.3: Test unauthenticated access (should fail)

```bash
curl http://localhost:8112/v1/leaderboards/open-source
# Should return 401 Unauthorized
```

### Step 6.4: Test with JWT (should succeed)

```bash
# Get JWT from Test 3
curl http://localhost:8112/v1/leaderboards/open-source \
  -H "Authorization: Bearer $JWT"
# Should return 200 OK with data
```

### Step 6.5: Test from Gradio app

1. Navigate to http://localhost:8100
2. Log in
3. Go to "Leaderboard" page
4. Should load successfully (Gradio mints JWT automatically)

---

## Test 7: Verify JWT Validation Logs

Check the bixarena-api logs to see JWT validation:

```bash
docker logs bixarena-api --tail 50 --follow
```

Look for:

- JWT decoder fetching JWKS from auth service
- JWT validation success/failure
- Role extraction from JWT claims

---

## Automated Testing Script

Here's a complete test script you can run:

```bash
#!/bin/bash
set -e

echo "=== BixArena JWT Authentication Test ==="
echo ""

# Step 1: Check services are running
echo "Step 1: Checking services..."
docker ps --filter "name=bixarena" --format "{{.Names}}" | grep -q "bixarena-auth-service" || {
  echo "❌ Auth service not running"
  exit 1
}
echo "✅ Services running"
echo ""

# Step 2: You need to manually get JSESSIONID from browser after login
echo "Step 2: Please log in via browser and get your JSESSIONID"
echo "Navigate to: http://localhost:8100"
echo "After login, open DevTools → Application → Cookies → Copy JSESSIONID value"
echo ""
read -p "Enter your JSESSIONID: " SESSION_ID
echo ""

# Step 3: Mint JWT
echo "Step 3: Minting JWT from session..."
JWT_RESPONSE=$(curl -s -X POST http://localhost:8115/v1/token \
  -H "Cookie: JSESSIONID=$SESSION_ID")

JWT=$(echo $JWT_RESPONSE | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JWT" ]; then
  echo "❌ Failed to mint JWT"
  echo "Response: $JWT_RESPONSE"
  exit 1
fi

echo "✅ JWT minted successfully"
echo "Token (first 50 chars): ${JWT:0:50}..."
echo ""

# Step 4: Test API call with JWT
echo "Step 4: Testing API call with JWT..."
API_RESPONSE=$(curl -s -w "\n%{http_code}" \
  http://localhost:8112/v1/leaderboards/open-source \
  -H "Authorization: Bearer $JWT")

HTTP_CODE=$(echo "$API_RESPONSE" | tail -n1)
BODY=$(echo "$API_RESPONSE" | head -n-1)

if [ "$HTTP_CODE" = "200" ]; then
  echo "✅ API call successful (HTTP $HTTP_CODE)"
  echo "Response preview: $(echo $BODY | head -c 100)..."
else
  echo "❌ API call failed (HTTP $HTTP_CODE)"
  echo "Response: $BODY"
  exit 1
fi

echo ""
echo "=== All tests passed! 🎉 ==="
```

Save this as `test-jwt-auth.sh` and run it!

---

## Quick Manual Test (No Script)

**Easiest way to test right now:**

1. **Login via browser:**

   - Open http://localhost:8100
   - Click "Login" → Complete Synapse login
   - You should see your username in the UI

2. **Test JWT minting via curl:**

   ```bash
   # Get your session ID from browser DevTools
   # Then run:
   curl -X POST http://localhost:8115/v1/token \
     -H "Cookie: JSESSIONID=<YOUR_SESSION_ID>" \
     -v
   ```

3. **Verify JWT is valid:**
   ```bash
   # Copy the access_token from step 2
   # Test it against the API:
   curl http://localhost:8112/v1/leaderboards/open-source \
     -H "Authorization: Bearer <YOUR_JWT>"
   ```

If all three steps work, your JWT authentication is working! ✅

---

## Troubleshooting

### "401 Unauthorized" when minting JWT

- Session cookie is invalid or expired
- JSESSIONID doesn't match any active session
- Try logging in again

### "Failed to mint JWT" in Gradio logs

- AUTH_BASE_URL_SSR not set correctly
- Auth service is down
- Network connectivity issue

### API returns 401 even with JWT

- JWT signature is invalid
- Auth service JWKS endpoint not reachable from bixarena-api
- JWT is expired
- Check bixarena-api logs for JWT validation errors

### JWT Decoder errors in logs

- bixarena-api can't reach auth service JWKS endpoint
- Check AUTH_SERVICE_BASE_URL configuration
- Verify network connectivity between containers
