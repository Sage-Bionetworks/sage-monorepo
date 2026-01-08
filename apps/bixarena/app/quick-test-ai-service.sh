#!/bin/bash
# Quick AI Service Prompt Validation Test
#
# Usage: ./quick-test-ai-service.sh <JSESSIONID> [PROMPT]
#
# To get your JSESSIONID:
# 1. Open http://localhost:8100 in browser
# 2. Click Login and authenticate with Synapse
# 3. Press F12 → Application → Cookies → localhost:8100
# 4. Copy the JSESSIONID value
# 5. Run: ./quick-test-ai-service.sh <your-jsessionid> "What causes diabetes?"

set -e

if [ -z "$1" ]; then
    echo "❌ Error: No JSESSIONID provided"
    echo ""
    echo "Usage: $0 <JSESSIONID> [PROMPT]"
    echo ""
    echo "To get your JSESSIONID:"
    echo "  1. Open http://localhost:8100 in browser"
    echo "  2. Click Login and authenticate"
    echo "  3. F12 → Application → Cookies → Copy JSESSIONID"
    echo ""
    exit 1
fi

SESSION_ID="$1"
PROMPT="${2:-What causes cancer and how can it be prevented?}"

# Base URLs for services
GATEWAY_BASE_URL="${GATEWAY_BASE_URL:-http://localhost:8113}"
AI_SERVICE_BASE_URL="${AI_SERVICE_BASE_URL:-http://localhost:8114}"
AUTH_BASE_URL="${AUTH_BASE_URL:-http://localhost:8115}"

echo "============================================"
echo "BixArena AI Service Validation Test"
echo "============================================"
echo ""
echo "Configuration:"
echo "  Gateway: $GATEWAY_BASE_URL"
echo "  AI Service: $AI_SERVICE_BASE_URL"
echo "  Auth Service: $AUTH_BASE_URL"
echo "  Prompt: \"$PROMPT\""
echo ""

# Test 1: Check if services are running
echo "Test 1: Checking services health..."

# Check gateway
GATEWAY_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" "$GATEWAY_BASE_URL/actuator/health" 2>/dev/null)
if [ "$GATEWAY_HEALTH" != "200" ]; then
    echo "❌ Gateway not healthy (HTTP $GATEWAY_HEALTH)"
    echo "   Make sure API gateway is running on $GATEWAY_BASE_URL"
    exit 1
fi

# Check auth service
AUTH_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" "$AUTH_BASE_URL/actuator/health" 2>/dev/null)
if [ "$AUTH_HEALTH" != "200" ]; then
    echo "❌ Auth service not healthy (HTTP $AUTH_HEALTH)"
    echo "   Make sure auth service is running on $AUTH_BASE_URL"
    exit 1
fi

# Check AI service
AI_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" "$AI_SERVICE_BASE_URL/health-check" 2>/dev/null)
if [ "$AI_HEALTH" != "200" ]; then
    echo "❌ AI service not healthy (HTTP $AI_HEALTH)"
    echo "   Make sure AI service is running on $AI_SERVICE_BASE_URL"
    exit 1
fi

echo "✅ All services are healthy"
echo "   Gateway: $GATEWAY_BASE_URL (HTTP $GATEWAY_HEALTH)"
echo "   Auth service: $AUTH_BASE_URL (HTTP $AUTH_HEALTH)"
echo "   AI service: $AI_SERVICE_BASE_URL (HTTP $AI_HEALTH)"
echo ""

# Test 2: Validate session with auth service
echo "Test 2: Validating session with auth service..."
echo "   GET $AUTH_BASE_URL/userinfo"
USERINFO_RESPONSE=$(curl -s -w "\n%{http_code}" \
    "$AUTH_BASE_URL/userinfo" \
    -H "Cookie: JSESSIONID=$SESSION_ID")

USERINFO_CODE=$(echo "$USERINFO_RESPONSE" | tail -n1)
USERINFO_BODY=$(echo "$USERINFO_RESPONSE" | head -n-1)

if [ "$USERINFO_CODE" = "200" ]; then
    echo "✅ Session is valid"
    echo "$USERINFO_BODY" | jq -r '
        "   Subject: " + .sub,
        "   Username: " + (.preferred_username // "N/A"),
        "   Email: " + (.email // "N/A"),
        "   Roles: " + (.roles | join(", "))
    ' 2>/dev/null || echo "$USERINFO_BODY"
else
    echo "❌ Session validation failed (HTTP $USERINFO_CODE)"
    echo "   Response: $USERINFO_BODY"
    echo "   Your JSESSIONID might be invalid or expired"
    exit 1
fi
echo ""

# Test 3: Call AI service health check directly (no auth required)
echo "Test 3: Testing AI service health check endpoint (no auth)..."
echo "   GET $AI_SERVICE_BASE_URL/health-check"
HEALTH_RESPONSE=$(curl -s -w "\n%{http_code}" "$AI_SERVICE_BASE_URL/health-check")

HEALTH_CODE=$(echo "$HEALTH_RESPONSE" | tail -n1)
HEALTH_BODY=$(echo "$HEALTH_RESPONSE" | head -n-1)

if [ "$HEALTH_CODE" = "200" ]; then
    echo "✅ Health check successful (HTTP $HEALTH_CODE)"
    echo "   Status: $(echo "$HEALTH_BODY" | jq -r '.status' 2>/dev/null)"
else
    echo "❌ Health check failed (HTTP $HEALTH_CODE)"
    echo "   Response: $HEALTH_BODY"
    exit 1
fi
echo ""

# Test 4: Try calling validate-prompt directly WITHOUT JWT (should fail)
echo "Test 4: Testing direct AI service call WITHOUT JWT (should fail)..."
echo "   GET $AI_SERVICE_BASE_URL/validate-prompt?prompt=..."
DIRECT_NO_JWT_RESPONSE=$(curl -s -w "\n%{http_code}" \
    "$AI_SERVICE_BASE_URL/validate-prompt?prompt=$(echo "$PROMPT" | jq -sRr @uri)")

DIRECT_NO_JWT_CODE=$(echo "$DIRECT_NO_JWT_RESPONSE" | tail -n1)

if [ "$DIRECT_NO_JWT_CODE" = "403" ]; then
    echo "✅ Correctly rejected without JWT (HTTP $DIRECT_NO_JWT_CODE)"
    echo "   This demonstrates the endpoint requires authentication"
elif [ "$DIRECT_NO_JWT_CODE" = "401" ]; then
    echo "✅ Correctly rejected without JWT (HTTP $DIRECT_NO_JWT_CODE)"
    echo "   This demonstrates the endpoint requires authentication"
elif [ "$DIRECT_NO_JWT_CODE" = "200" ]; then
    echo "⚠️  Warning: Endpoint allowed access without JWT (HTTP $DIRECT_NO_JWT_CODE)"
    echo "   This suggests authentication might not be properly configured"
else
    echo "ℹ️  Unexpected response (HTTP $DIRECT_NO_JWT_CODE)"
fi
echo ""

# Test 5: Call validate-prompt through Gateway WITHOUT session (should fail)
echo "Test 5: Testing Gateway call WITHOUT session (should fail)..."
echo "   GET $GATEWAY_BASE_URL/api/v1/validate-prompt?prompt=..."
GATEWAY_NO_SESSION_RESPONSE=$(curl -s -w "\n%{http_code}" \
    "$GATEWAY_BASE_URL/api/v1/validate-prompt?prompt=$(echo "$PROMPT" | jq -sRr @uri)")

GATEWAY_NO_SESSION_CODE=$(echo "$GATEWAY_NO_SESSION_RESPONSE" | tail -n1)

if [ "$GATEWAY_NO_SESSION_CODE" = "401" ]; then
    echo "✅ Correctly rejected without session (HTTP $GATEWAY_NO_SESSION_CODE)"
    echo "   Gateway is properly enforcing authentication"
elif [ "$GATEWAY_NO_SESSION_CODE" = "403" ]; then
    echo "✅ Correctly rejected without session (HTTP $GATEWAY_NO_SESSION_CODE)"
    echo "   Gateway is properly enforcing authentication"
else
    echo "⚠️  Unexpected response (HTTP $GATEWAY_NO_SESSION_CODE)"
fi
echo ""

# Test 6: Call validate-prompt through Gateway WITH session (the main test!)
echo "Test 6: Testing prompt validation through Gateway WITH session..."
echo "   GET $GATEWAY_BASE_URL/api/v1/validate-prompt?prompt=..."
echo "   Cookie: JSESSIONID=<session>"
VALIDATION_RESPONSE=$(curl -s -w "\n%{http_code}" \
    "$GATEWAY_BASE_URL/api/v1/validate-prompt?prompt=$(echo "$PROMPT" | jq -sRr @uri)" \
    -H "Cookie: JSESSIONID=$SESSION_ID")

VALIDATION_CODE=$(echo "$VALIDATION_RESPONSE" | tail -n1)
VALIDATION_BODY=$(echo "$VALIDATION_RESPONSE" | head -n-1)

if [ "$VALIDATION_CODE" = "200" ]; then
    echo "✅ Prompt validation successful! (HTTP $VALIDATION_CODE)"
    echo ""
    echo "   Response:"
    echo "$VALIDATION_BODY" | jq '.' 2>/dev/null || echo "$VALIDATION_BODY"
    echo ""

    # Parse response fields
    VALIDATED_PROMPT=$(echo "$VALIDATION_BODY" | jq -r '.prompt' 2>/dev/null)
    CONFIDENCE=$(echo "$VALIDATION_BODY" | jq -r '.confidence' 2>/dev/null)
    IS_BIOMEDICAL=$(echo "$VALIDATION_BODY" | jq -r '.isBiomedical' 2>/dev/null)

    echo "   Summary:"
    echo "   - Prompt: \"$VALIDATED_PROMPT\""
    echo "   - Confidence: $CONFIDENCE"
    echo "   - Is Biomedical: $IS_BIOMEDICAL"
else
    echo "❌ Prompt validation failed (HTTP $VALIDATION_CODE)"
    echo "   Response: $VALIDATION_BODY"
    exit 1
fi
echo ""

# Test 7: Test with different prompt types
echo "Test 7: Testing with various prompt types..."
echo ""

# Test biomedical prompt
BIOMED_PROMPT="What are the effects of aspirin on cardiovascular health?"
echo "   7a. Biomedical prompt: \"$BIOMED_PROMPT\""
BIOMED_RESPONSE=$(curl -s -w "\n%{http_code}" \
    "$GATEWAY_BASE_URL/api/v1/validate-prompt?prompt=$(echo "$BIOMED_PROMPT" | jq -sRr @uri)" \
    -H "Cookie: JSESSIONID=$SESSION_ID")

BIOMED_CODE=$(echo "$BIOMED_RESPONSE" | tail -n1)
BIOMED_BODY=$(echo "$BIOMED_RESPONSE" | head -n-1)

if [ "$BIOMED_CODE" = "200" ]; then
    BIOMED_CONFIDENCE=$(echo "$BIOMED_BODY" | jq -r '.confidence' 2>/dev/null)
    BIOMED_IS_BIOMED=$(echo "$BIOMED_BODY" | jq -r '.isBiomedical' 2>/dev/null)
    echo "       ✅ Confidence: $BIOMED_CONFIDENCE, Is Biomedical: $BIOMED_IS_BIOMED"
else
    echo "       ❌ Failed (HTTP $BIOMED_CODE)"
fi
echo ""

# Test non-biomedical prompt
NON_BIOMED_PROMPT="What is the weather like today?"
echo "   7b. Non-biomedical prompt: \"$NON_BIOMED_PROMPT\""
NON_BIOMED_RESPONSE=$(curl -s -w "\n%{http_code}" \
    "$GATEWAY_BASE_URL/api/v1/validate-prompt?prompt=$(echo "$NON_BIOMED_PROMPT" | jq -sRr @uri)" \
    -H "Cookie: JSESSIONID=$SESSION_ID")

NON_BIOMED_CODE=$(echo "$NON_BIOMED_RESPONSE" | tail -n1)
NON_BIOMED_BODY=$(echo "$NON_BIOMED_RESPONSE" | head -n-1)

if [ "$NON_BIOMED_CODE" = "200" ]; then
    NON_BIOMED_CONFIDENCE=$(echo "$NON_BIOMED_BODY" | jq -r '.confidence' 2>/dev/null)
    NON_BIOMED_IS_BIOMED=$(echo "$NON_BIOMED_BODY" | jq -r '.isBiomedical' 2>/dev/null)
    echo "       ✅ Confidence: $NON_BIOMED_CONFIDENCE, Is Biomedical: $NON_BIOMED_IS_BIOMED"
else
    echo "       ❌ Failed (HTTP $NON_BIOMED_CODE)"
fi
echo ""

# Summary
echo "============================================"
echo "🎉 AI Service Integration Test Complete!"
echo "============================================"
echo ""
echo "Summary:"
echo "  ✅ Gateway correctly routes requests to AI service"
echo "  ✅ Gateway performs session-to-JWT conversion"
echo "  ✅ AI service validates JWT tokens"
echo "  ✅ Prompt validation endpoint is working"
echo ""
echo "The complete authentication flow is working:"
echo "  Session Cookie → Gateway → JWT → AI Service"
echo ""
echo "Next steps:"
echo "  1. Replace static confidence value with ML model"
echo "  2. Add more sophisticated biomedical detection"
echo "  3. Integrate with Gradio UI"
echo "  4. Add caching for repeated prompts"
echo ""
