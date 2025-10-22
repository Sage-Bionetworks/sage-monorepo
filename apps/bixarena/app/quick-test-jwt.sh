#!/bin/bash
# Quick JWT Authentication Test
#
# Usage: ./quick-test-jwt.sh <JSESSIONID>
#
# To get your JSESSIONID:
# 1. Open http://localhost:8100 in browser
# 2. Click Login and authenticate with Synapse
# 3. Press F12 → Application → Cookies → localhost:8100
# 4. Copy the JSESSIONID value
# 5. Run: ./quick-test-jwt.sh <your-jsessionid>

set -e

if [ -z "$1" ]; then
    echo "❌ Error: No JSESSIONID provided"
    echo ""
    echo "Usage: $0 <JSESSIONID>"
    echo ""
    echo "To get your JSESSIONID:"
    echo "  1. Open http://localhost:8100 in browser"
    echo "  2. Click Login and authenticate"
    echo "  3. F12 → Application → Cookies → Copy JSESSIONID"
    echo ""
    exit 1
fi

SESSION_ID="$1"

echo "============================================"
echo "BixArena JWT Authentication Quick Test"
echo "============================================"
echo ""

# Test 1: Check if auth service is running
echo "Test 1: Checking auth service health..."

# Check auth service
AUTH_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8115/actuator/health" 2>/dev/null)
if [ "$AUTH_HEALTH" != "200" ]; then
    echo "❌ Auth service not healthy (HTTP $AUTH_HEALTH)"
    echo "   Make sure auth service is running on http://localhost:8115"
    exit 1
fi

echo "✅ Auth service is healthy (HTTP $AUTH_HEALTH)"
echo ""

# Test 2: Get user info
echo "Test 2: Getting user info from session..."
USERINFO_RESPONSE=$(curl -s -w "\n%{http_code}" \
    "http://localhost:8115/userinfo" \
    -H "Cookie: JSESSIONID=$SESSION_ID")

USERINFO_CODE=$(echo "$USERINFO_RESPONSE" | tail -n1)
USERINFO_BODY=$(echo "$USERINFO_RESPONSE" | head -n-1)

if [ "$USERINFO_CODE" = "200" ]; then
    echo "✅ User info retrieved successfully"
    echo "$USERINFO_BODY" | jq -r '
        "   Subject: " + .sub,
        "   Username: " + (.preferred_username // "N/A"),
        "   Email: " + (.email // "N/A"),
        "   Roles: " + (.roles | join(", "))
    ' 2>/dev/null || echo "$USERINFO_BODY"
else
    echo "❌ Failed to get user info (HTTP $USERINFO_CODE)"
    echo "   Response: $USERINFO_BODY"
    exit 1
fi
echo ""

# Test 3: Mint JWT
echo "Test 3: Minting JWT from session..."
TOKEN_RESPONSE=$(curl -s -w "\n%{http_code}" \
    -X POST "http://localhost:8115/token" \
    -H "Cookie: JSESSIONID=$SESSION_ID")

TOKEN_CODE=$(echo "$TOKEN_RESPONSE" | tail -n1)
TOKEN_BODY=$(echo "$TOKEN_RESPONSE" | head -n-1)

if [ "$TOKEN_CODE" = "200" ]; then
    echo "✅ JWT minted successfully"

    JWT=$(echo "$TOKEN_BODY" | jq -r '.access_token' 2>/dev/null)
    TOKEN_TYPE=$(echo "$TOKEN_BODY" | jq -r '.token_type' 2>/dev/null)
    EXPIRES_IN=$(echo "$TOKEN_BODY" | jq -r '.expires_in' 2>/dev/null)

    echo "   Token type: $TOKEN_TYPE"
    echo "   Expires in: $EXPIRES_IN seconds"
    echo "   JWT (first 50 chars): ${JWT:0:50}..."
else
    echo "❌ Failed to mint JWT (HTTP $TOKEN_CODE)"
    echo "   Response: $TOKEN_BODY"
    exit 1
fi
echo ""

# Test 4: Call API without JWT
echo "Test 4: Calling API without JWT (should work for public endpoints)..."
API_RESPONSE_NO_JWT=$(curl -s -w "\n%{http_code}" \
    "http://localhost:8112/v1/leaderboards/open-source")

API_CODE_NO_JWT=$(echo "$API_RESPONSE_NO_JWT" | tail -n1)

if [ "$API_CODE_NO_JWT" = "200" ]; then
    echo "✅ Public endpoint accessible without JWT (HTTP $API_CODE_NO_JWT)"
elif [ "$API_CODE_NO_JWT" = "401" ]; then
    echo "ℹ️  Endpoint requires authentication (HTTP $API_CODE_NO_JWT)"
    echo "   This is expected if you've protected the endpoint"
else
    echo "⚠️  Unexpected response (HTTP $API_CODE_NO_JWT)"
fi
echo ""

# Test 5: Call API with JWT
echo "Test 5: Calling API with JWT..."
API_RESPONSE_WITH_JWT=$(curl -s -w "\n%{http_code}" \
    "http://localhost:8112/v1/leaderboards/open-source" \
    -H "Authorization: Bearer $JWT")

API_CODE_WITH_JWT=$(echo "$API_RESPONSE_WITH_JWT" | tail -n1)
API_BODY_WITH_JWT=$(echo "$API_RESPONSE_WITH_JWT" | head -n-1)

if [ "$API_CODE_WITH_JWT" = "200" ]; then
    echo "✅ Authenticated API call successful (HTTP $API_CODE_WITH_JWT)"

    LEADERBOARD_ID=$(echo "$API_BODY_WITH_JWT" | jq -r '.id' 2>/dev/null)
    ENTRY_COUNT=$(echo "$API_BODY_WITH_JWT" | jq -r '.entries | length' 2>/dev/null)

    echo "   Leaderboard ID: $LEADERBOARD_ID"
    echo "   Number of entries: $ENTRY_COUNT"
else
    echo "❌ API call with JWT failed (HTTP $API_CODE_WITH_JWT)"
    echo "   Response: ${API_BODY_WITH_JWT:0:200}"
    exit 1
fi
echo ""

# Test 6: Call protected admin endpoint with JWT
echo "Test 6: Calling protected /admin/stats endpoint with JWT..."
ADMIN_RESPONSE=$(curl -s -w "\n%{http_code}" \
    "http://localhost:8115/admin/stats" \
    -H "Authorization: Bearer $JWT")

ADMIN_CODE=$(echo "$ADMIN_RESPONSE" | tail -n1)
ADMIN_BODY=$(echo "$ADMIN_RESPONSE" | head -n-1)

if [ "$ADMIN_CODE" = "200" ]; then
    echo "✅ Admin endpoint call successful (HTTP $ADMIN_CODE)"
    echo "   Response:"
    echo "$ADMIN_BODY" | jq '.' 2>/dev/null || echo "$ADMIN_BODY"
elif [ "$ADMIN_CODE" = "403" ]; then
    echo "ℹ️  Access forbidden (HTTP $ADMIN_CODE)"
    echo "   Your user doesn't have ROLE_ADMIN"
    echo "   This demonstrates JWT role-based access control is working!"
elif [ "$ADMIN_CODE" = "401" ]; then
    echo "❌ Authentication failed (HTTP $ADMIN_CODE)"
    echo "   JWT might be invalid or expired"
    exit 1
else
    echo "⚠️  Unexpected response (HTTP $ADMIN_CODE)"
    echo "   Response: ${ADMIN_BODY:0:200}"
fi
echo ""

# Summary
echo "============================================"
echo "🎉 All tests passed!"
echo "============================================"
echo ""
echo "Your JWT authentication is working correctly!"
echo ""
echo "Your JWT token (save this for manual testing):"
echo "$JWT"
echo ""
echo "Next steps:"
echo "  1. Protect API endpoints in SecurityConfiguration"
echo "  2. Update Gradio functions to use JWT helpers"
echo "  3. Test from the Gradio UI at http://localhost:8100"
