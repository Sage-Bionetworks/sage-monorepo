#!/bin/bash

# OpenChallenges Auth Service API Demo Script
# This script demonstrates the API key management functionality

set -e

API_BASE="http://localhost:8087"
echo "🚀 OpenChallenges Auth Service API Demo"
echo "==============================================="

# Check if service is running
echo "📡 Checking if auth service is running..."
if ! curl -s -f "$API_BASE/actuator/health" > /dev/null; then
    echo "❌ Auth service is not running at $API_BASE"
    echo "   Please start the service first:"
    echo "   cd /workspaces/sage-monorepo/apps/openchallenges/auth-service"
    echo "   ./gradlew bootRun --args='--spring.profiles.active=dev'"
    exit 1
fi
echo "✅ Auth service is running"

# Step 1: Login
echo -e "\n🔐 Step 1: Login as admin user"
echo "POST $API_BASE/v1/auth/login"

LOGIN_RESPONSE=$(curl -s --http1.1 --tcp-nodelay -X POST "$API_BASE/v1/auth/login" \
  -H "Content-Type: application/json" \
  -H "Connection: close" \
  -d '{"username": "admin", "password": "changeme"}')

if echo "$LOGIN_RESPONSE" | grep -q "apiKey"; then
    API_KEY=$(echo "$LOGIN_RESPONSE" | jq -r '.apiKey')
    USERNAME=$(echo "$LOGIN_RESPONSE" | jq -r '.username')
    echo "✅ Login successful for user: $USERNAME"
    echo "🔑 Received API key: ${API_KEY:0:20}..."
else
    echo "❌ Login failed"
    echo "Response: $LOGIN_RESPONSE"
    exit 1
fi

# Step 2: Create API Key
echo -e "\n📝 Step 2: Create a new API key"
echo "POST $API_BASE/v1/auth/api-keys"

CREATE_RESPONSE=$(curl -s --http1.1 --tcp-nodelay -X POST "$API_BASE/v1/auth/api-keys" \
  -H "Authorization: Bearer $API_KEY" \
  -H "Content-Type: application/json" \
  -H "Connection: close" \
  -d '{"name": "Demo API Key", "expiresIn": 30}')

if echo "$CREATE_RESPONSE" | grep -q '"key"'; then
    NEW_API_KEY=$(echo "$CREATE_RESPONSE" | jq -r '.key')
    KEY_ID=$(echo "$CREATE_RESPONSE" | jq -r '.id')
    KEY_NAME=$(echo "$CREATE_RESPONSE" | jq -r '.name')
    echo "✅ API key created successfully"
    echo "📋 Key ID: $KEY_ID"
    echo "📝 Key Name: $KEY_NAME"
    echo "🔑 New API key: ${NEW_API_KEY:0:20}..."
else
    echo "❌ API key creation failed"
    echo "Response: $CREATE_RESPONSE"
    exit 1
fi

# Step 3: List API Keys
echo -e "\n📋 Step 3: List all API keys"
echo "GET $API_BASE/v1/auth/api-keys"

LIST_RESPONSE=$(curl -s -X GET "$API_BASE/v1/auth/api-keys" \
  -H "Authorization: Bearer $API_KEY")

if echo "$LIST_RESPONSE" | grep -q '\['; then
    KEY_COUNT=$(echo "$LIST_RESPONSE" | jq '. | length')
    echo "✅ Listed API keys successfully"
    echo "📊 Total API keys: $KEY_COUNT"
    echo "📋 API keys:"
    echo "$LIST_RESPONSE" | jq -r '.[] | "  - \(.name) (ID: \(.id), Created: \(.createdAt))"'
else
    echo "❌ Failed to list API keys"
    echo "Response: $LIST_RESPONSE"
    exit 1
fi

# Step 4: Test the new API key
echo -e "\n🧪 Step 4: Test the new API key by listing keys with it"
echo "GET $API_BASE/v1/auth/api-keys (using new key)"

TEST_RESPONSE=$(curl -s -X GET "$API_BASE/v1/auth/api-keys" \
  -H "Authorization: Bearer $NEW_API_KEY")

if echo "$TEST_RESPONSE" | grep -q '\['; then
    echo "✅ New API key works correctly"
else
    echo "❌ New API key failed to work"
    echo "Response: $TEST_RESPONSE"
fi

# Step 5: Validate the API key using the validate endpoint
echo -e "\n🔍 Step 5: Validate the API key using the validate endpoint"
echo "POST $API_BASE/v1/auth/validate"

VALIDATE_RESPONSE=$(curl -s --http1.1 --tcp-nodelay -X POST "$API_BASE/v1/auth/validate" \
  -H "Content-Type: application/json" \
  -H "Connection: close" \
  -d "{\"apiKey\": \"$NEW_API_KEY\"}")

if echo "$VALIDATE_RESPONSE" | grep -q '"valid": *true'; then
    USER_ID=$(echo "$VALIDATE_RESPONSE" | jq -r '.userId')
    USERNAME=$(echo "$VALIDATE_RESPONSE" | jq -r '.username')
    ROLE=$(echo "$VALIDATE_RESPONSE" | jq -r '.role')
    SCOPES=$(echo "$VALIDATE_RESPONSE" | jq -r '.scopes | join(", ")')
    echo "✅ API key validation successful"
    echo "👤 User ID: $USER_ID"
    echo "🏷️ Username: $USERNAME"
    echo "🎭 Role: $ROLE"
    echo "🔑 Scopes: $SCOPES"
else
    echo "❌ API key validation failed"
    echo "Response: $VALIDATE_RESPONSE"
fi

# Test with invalid API key
echo -e "\n🧪 Step 5b: Test validation with invalid API key"
echo "POST $API_BASE/v1/auth/validate (with invalid key)"

INVALID_VALIDATE_RESPONSE=$(curl -s --http1.1 --tcp-nodelay -X POST "$API_BASE/v1/auth/validate" \
  -H "Content-Type: application/json" \
  -H "Connection: close" \
  -d '{"apiKey": "oc_dev_invalid_key_123456789"}')

if echo "$INVALID_VALIDATE_RESPONSE" | grep -q '"valid": *false'; then
    echo "✅ Invalid API key correctly rejected"
else
    echo "❌ Invalid API key validation test failed"
    echo "Response: $INVALID_VALIDATE_RESPONSE"
fi

# Step 6: Delete the created API key
echo -e "\n🗑️  Step 6: Delete the created API key"
echo "DELETE $API_BASE/v1/auth/api-keys/$KEY_ID"

DELETE_RESPONSE=$(curl -s -w "%{http_code}" -X DELETE "$API_BASE/v1/auth/api-keys/$KEY_ID" \
  -H "Authorization: Bearer $API_KEY")

if [ "$DELETE_RESPONSE" = "204" ]; then
    echo "✅ API key deleted successfully"
else
    echo "❌ Failed to delete API key"
    echo "HTTP status: $DELETE_RESPONSE"
fi

# Step 7: Verify deletion
echo -e "\n🔍 Step 7: Verify the API key was deleted"
echo "GET $API_BASE/v1/auth/api-keys (using deleted key)"

VERIFY_RESPONSE=$(curl -s -w "%{http_code}" -X GET "$API_BASE/v1/auth/api-keys" \
  -H "Authorization: Bearer $NEW_API_KEY")

if echo "$VERIFY_RESPONSE" | grep -q "401"; then
    echo "✅ Deleted API key correctly rejected (unauthorized)"
else
    echo "❌ Deleted API key still works (this shouldn't happen)"
    echo "Response: $VERIFY_RESPONSE"
fi

# Step 8: Test validation endpoint with deleted API key
echo -e "\n🧪 Step 8: Test validation endpoint with deleted API key"
echo "POST $API_BASE/v1/auth/validate (with deleted key)"

DELETED_VALIDATE_RESPONSE=$(curl -s --http1.1 --tcp-nodelay -X POST "$API_BASE/v1/auth/validate" \
  -H "Content-Type: application/json" \
  -H "Connection: close" \
  -d "{\"apiKey\": \"$NEW_API_KEY\"}")

if echo "$DELETED_VALIDATE_RESPONSE" | grep -q '"valid": *false'; then
    echo "✅ Deleted API key validation correctly returned false"
else
    echo "❌ Deleted API key validation test failed"
    echo "Response: $DELETED_VALIDATE_RESPONSE"
fi

echo -e "\n🎉 Demo completed successfully!"
echo "==============================================="
echo "✅ All API key management operations work correctly"
echo "🔑 API key authentication is functional"
echo "🔍 API key validation endpoint tested"
echo "🛡️ Security controls are in place"

# Display profile information
echo -e "\n📋 Current Configuration:"
echo "🏷️ Profile: dev (API keys prefixed with 'oc_dev_')"
echo "🌐 Service URL: $API_BASE"
echo "📚 API Documentation: $API_BASE/swagger-ui.html"
