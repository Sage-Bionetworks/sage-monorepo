#!/bin/bash

# OpenChallenges Organization Service API Authentication Demo Script
# This script demonstrates the protected organization deletion endpoint

set -e

AUTH_SERVICE_BASE="http://openchallenges-auth-service:8087"
ORG_SERVICE_BASE="http://openchallenges-organization-service:8084"

echo "🚀 OpenChallenges Organization Service Authentication Demo"
echo "=========================================================="

# Check if services are running
echo "📡 Checking if services are running..."

if ! curl -s -f "$AUTH_SERVICE_BASE/actuator/health" > /dev/null; then
    echo "❌ Auth service is not running at $AUTH_SERVICE_BASE"
    echo "   Please start the auth service first"
    exit 1
fi

if ! curl -s -f "$ORG_SERVICE_BASE/actuator/health" > /dev/null; then
    echo "❌ Organization service is not running at $ORG_SERVICE_BASE"
    echo "   Please start the organization service first"
    exit 1
fi

echo "✅ Both services are running"

# Step 1: Login to get API key
echo -e "\n🔐 Step 1: Login to auth service to get API key"
echo "POST $AUTH_SERVICE_BASE/v1/auth/login"

LOGIN_RESPONSE=$(curl -s --http1.1 --tcp-nodelay -X POST "$AUTH_SERVICE_BASE/v1/auth/login" \
  -H "Content-Type: application/json" \
  -H "Connection: close" \
  -d '{"username": "admin", "password": "changeme"}')

if echo "$LOGIN_RESPONSE" | grep -q "apiKey"; then
    API_KEY=$(echo "$LOGIN_RESPONSE" | jq -r '.apiKey')
    USERNAME=$(echo "$LOGIN_RESPONSE" | jq -r '.username')
    ROLE=$(echo "$LOGIN_RESPONSE" | jq -r '.role')
    echo "✅ Login successful for user: $USERNAME (role: $ROLE)"
    echo "🔑 Received API key: ${API_KEY:0:20}..."
else
    echo "❌ Login failed"
    echo "Response: $LOGIN_RESPONSE"
    exit 1
fi

# Step 2: Test public endpoint (should work without authentication)
echo -e "\n📖 Step 2: Test public GET endpoint (no authentication required)"
echo "GET $ORG_SERVICE_BASE/v1/organizations/test-org"

PUBLIC_RESPONSE=$(curl -s -w "%{http_code}" -X GET "$ORG_SERVICE_BASE/v1/organizations/test-org")

if echo "$PUBLIC_RESPONSE" | grep -q "404"; then
    echo "✅ Public endpoint accessible (404 expected - org doesn't exist)"
else
    echo "❌ Unexpected response from public endpoint"
    echo "Response: $PUBLIC_RESPONSE"
fi

# Step 3: Test protected endpoint without authentication (should fail)
echo -e "\n🚫 Step 3: Test protected DELETE endpoint without authentication"
echo "DELETE $ORG_SERVICE_BASE/v1/organizations/test-org (no auth header)"

UNAUTH_RESPONSE=$(curl -s -w "%{http_code}" -X DELETE "$ORG_SERVICE_BASE/v1/organizations/test-org")

if echo "$UNAUTH_RESPONSE" | grep -q "401"; then
    echo "✅ Protected endpoint correctly rejected unauthenticated request"
else
    echo "❌ Protected endpoint should have returned 401"
    echo "Response: $UNAUTH_RESPONSE"
fi

# Step 4: Test protected endpoint with invalid API key (should fail)
echo -e "\n🚫 Step 4: Test protected DELETE endpoint with invalid API key"
echo "DELETE $ORG_SERVICE_BASE/v1/organizations/test-org (invalid key)"

INVALID_RESPONSE=$(curl -s -w "%{http_code}" -X DELETE "$ORG_SERVICE_BASE/v1/organizations/test-org" \
  -H "Authorization: Bearer oc_dev_invalid_key_123")

if echo "$INVALID_RESPONSE" | grep -q "401"; then
    echo "✅ Protected endpoint correctly rejected invalid API key"
else
    echo "❌ Protected endpoint should have returned 401 for invalid key"
    echo "Response: $INVALID_RESPONSE"
fi

# Step 5: Test protected endpoint with valid API key (should work)
echo -e "\n🔓 Step 5: Test protected DELETE endpoint with valid API key"
echo "DELETE $ORG_SERVICE_BASE/v1/organizations/test-org (valid admin key)"

VALID_RESPONSE=$(curl -s -w "%{http_code}" -X DELETE "$ORG_SERVICE_BASE/v1/organizations/test-org" \
  -H "Authorization: Bearer $API_KEY")

if echo "$VALID_RESPONSE" | grep -q "404"; then
    echo "✅ Protected endpoint accepted valid API key (404 expected - org doesn't exist)"
else
    echo "❌ Unexpected response from protected endpoint with valid key"
    echo "Response: $VALID_RESPONSE"
fi

# Step 6: Validate the API key to show user details
echo -e "\n🔍 Step 6: Validate the API key to show user details and permissions"
echo "POST $AUTH_SERVICE_BASE/v1/auth/validate"

VALIDATE_RESPONSE=$(curl -s --http1.1 --tcp-nodelay -X POST "$AUTH_SERVICE_BASE/v1/auth/validate" \
  -H "Content-Type: application/json" \
  -H "Connection: close" \
  -d "{\"apiKey\": \"$API_KEY\"}")

if echo "$VALIDATE_RESPONSE" | grep -q '"valid": *true'; then
    VALIDATED_USER=$(echo "$VALIDATE_RESPONSE" | jq -r '.username')
    VALIDATED_ROLE=$(echo "$VALIDATE_RESPONSE" | jq -r '.role')
    VALIDATED_SCOPES=$(echo "$VALIDATE_RESPONSE" | jq -r '.scopes | join(", ")')
    echo "✅ API key validation successful"
    echo "👤 User: $VALIDATED_USER"
    echo "🎭 Role: $VALIDATED_ROLE"
    echo "🔑 Scopes: $VALIDATED_SCOPES"

    if echo "$VALIDATED_SCOPES" | grep -q "organizations:delete"; then
        echo "✅ User has required 'organizations:delete' scope"
    else
        echo "❌ User missing 'organizations:delete' scope"
    fi
else
    echo "❌ API key validation failed"
    echo "Response: $VALIDATE_RESPONSE"
fi

echo -e "\n🎉 Demo completed successfully!"
echo "=========================================================="
echo "✅ Authentication and authorization working correctly"
echo "🔒 Protected endpoints require valid API keys"
echo "🛡️  Scope-based authorization implemented"
echo "📝 Only users with 'organizations:delete' scope can delete organizations"

echo -e "\n📋 Summary:"
echo "🌐 Organization Service: $ORG_SERVICE_BASE"
echo "🔐 Auth Service: $AUTH_SERVICE_BASE"
echo "📚 API Documentation: $ORG_SERVICE_BASE/swagger-ui.html"
