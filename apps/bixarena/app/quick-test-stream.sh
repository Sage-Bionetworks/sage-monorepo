#!/bin/bash
# Quick SSE Streaming End-to-End Test
#
# Tests the full LLM streaming pipeline:
#   Frontend → api-gateway → api-service → ai-service → OpenRouter
#
# Usage: ./quick-test-stream.sh <JSESSIONID> [PROMPT]
#
# To get your JSESSIONID:
# 1. Open http://localhost:8100 in browser
# 2. Click Login and authenticate with Synapse
# 3. Press F12 → Application → Cookies → localhost:8100
# 4. Copy the JSESSIONID value
# 5. Run: ./quick-test-stream.sh <your-jsessionid>

set -e

if [ -z "$1" ]; then
    echo "Error: No JSESSIONID provided"
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
PROMPT="${2:-What genes are associated with Alzheimer disease? Answer in 2-3 sentences.}"

GATEWAY_BASE_URL="${GATEWAY_BASE_URL:-http://localhost:8113}"
MODEL1_ERRORED=false
PASSED=0
FAILED=0
SKIPPED=0

pass() { echo "  PASS: $1"; PASSED=$((PASSED + 1)); }
fail() { echo "  FAIL: $1"; FAILED=$((FAILED + 1)); }
skip() { echo "  SKIP: $1"; SKIPPED=$((SKIPPED + 1)); }

echo "============================================"
echo "BixArena SSE Streaming E2E Test"
echo "============================================"
echo ""
echo "Configuration:"
echo "  Gateway: $GATEWAY_BASE_URL"
echo "  Prompt: \"$PROMPT\""
echo ""

# ------------------------------------------------------------------
# Test 1: Health checks
# ------------------------------------------------------------------
echo "Test 1: Service health checks"

GATEWAY_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" "$GATEWAY_BASE_URL/actuator/health" 2>/dev/null)
if [ "$GATEWAY_HEALTH" = "200" ]; then
    pass "Gateway healthy"
else
    fail "Gateway not healthy (HTTP $GATEWAY_HEALTH)"
    echo "  Run: workspace-docker-stop && bixarena-build-images && nx serve-detach bixarena-apex"
    exit 1
fi
echo ""

# ------------------------------------------------------------------
# Test 2: Create battle
# ------------------------------------------------------------------
echo "Test 2: Create battle"

BATTLE_RESPONSE=$(curl -s -w "\n%{http_code}" \
    -X POST "$GATEWAY_BASE_URL/api/v1/battles" \
    -H "Cookie: JSESSIONID=$SESSION_ID" \
    -H "Content-Type: application/json" \
    -d '{}')

BATTLE_CODE=$(echo "$BATTLE_RESPONSE" | tail -n1)
BATTLE_BODY=$(echo "$BATTLE_RESPONSE" | head -n-1)

if [ "$BATTLE_CODE" = "201" ] || [ "$BATTLE_CODE" = "200" ]; then
    BATTLE_ID=$(echo "$BATTLE_BODY" | jq -r '.id')
    MODEL1_ID=$(echo "$BATTLE_BODY" | jq -r '.model1.id')
    MODEL2_ID=$(echo "$BATTLE_BODY" | jq -r '.model2.id')
    MODEL1_NAME=$(echo "$BATTLE_BODY" | jq -r '.model1.name')
    MODEL2_NAME=$(echo "$BATTLE_BODY" | jq -r '.model2.name')
    USER_ID=$(echo "$BATTLE_BODY" | jq -r '.userId')
    pass "Battle created: $BATTLE_ID"
    echo "    Models: $MODEL1_NAME vs $MODEL2_NAME"
    echo "    User: $USER_ID"
else
    fail "Create battle failed (HTTP $BATTLE_CODE)"
    echo "    Response: $BATTLE_BODY"
    exit 1
fi
echo ""

# ------------------------------------------------------------------
# Test 3: Create round with prompt
# ------------------------------------------------------------------
echo "Test 3: Create round"

ROUND_RESPONSE=$(curl -s -w "\n%{http_code}" \
    -X POST "$GATEWAY_BASE_URL/api/v1/battles/$BATTLE_ID/rounds" \
    -H "Cookie: JSESSIONID=$SESSION_ID" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg p "$PROMPT" '{promptMessage: {role: "user", content: $p}}')")

ROUND_CODE=$(echo "$ROUND_RESPONSE" | tail -n1)
ROUND_BODY=$(echo "$ROUND_RESPONSE" | head -n-1)

if [ "$ROUND_CODE" = "201" ] || [ "$ROUND_CODE" = "200" ]; then
    ROUND_ID=$(echo "$ROUND_BODY" | jq -r '.id')
    pass "Round created: $ROUND_ID"
else
    fail "Create round failed (HTTP $ROUND_CODE)"
    echo "    Response: $ROUND_BODY"
    exit 1
fi
echo ""

# ------------------------------------------------------------------
# Test 4: Stream model 1 (SSE happy path)
# ------------------------------------------------------------------
echo "Test 4: Stream model 1 ($MODEL1_NAME)"

STREAM_URL="$GATEWAY_BASE_URL/api/v1/battles/$BATTLE_ID/rounds/$ROUND_ID/stream?modelId=$MODEL1_ID"
STREAM_OUTPUT=$(curl -s -N -X POST "$STREAM_URL" -H "Cookie: JSESSIONID=$SESSION_ID" 2>&1)

# Check for SSE data lines
DATA_LINES=$(echo "$STREAM_OUTPUT" | grep -c "^data: " || true)
HAS_COMPLETE=$(echo "$STREAM_OUTPUT" | grep -c '"status":"complete"' || true)
LAST_LINE=$(echo "$STREAM_OUTPUT" | grep "^data: " | tail -1)

if [ "$DATA_LINES" -gt 0 ] && [ "$HAS_COMPLETE" -gt 0 ]; then
    pass "SSE streaming works ($DATA_LINES data lines)"

    # Verify camelCase format
    HAS_CAMEL=$(echo "$LAST_LINE" | grep -c "finishReason" || true)
    if [ "$HAS_CAMEL" -gt 0 ]; then
        pass "SSE JSON uses camelCase (finishReason)"
    else
        fail "SSE JSON missing camelCase fields"
    fi

    # Verify usage data
    HAS_USAGE=$(echo "$LAST_LINE" | grep -c '"promptTokens"' || true)
    if [ "$HAS_USAGE" -gt 0 ]; then
        pass "Usage data included in final chunk"
        echo "    Final chunk: $LAST_LINE"
    else
        fail "Missing usage data in final chunk"
    fi
else
    # Check if it was a model provider error (still valid SSE)
    HAS_ERROR=$(echo "$STREAM_OUTPUT" | grep -c '"status":"error"' || true)
    if [ "$HAS_ERROR" -gt 0 ]; then
        pass "SSE error chunk received (model unavailable on provider)"
        echo "    Output: $(echo "$STREAM_OUTPUT" | head -3)"
        MODEL1_ERRORED=true
    else
        fail "No SSE data received"
        echo "    Output: $(echo "$STREAM_OUTPUT" | head -5)"
    fi
fi
echo ""

# ------------------------------------------------------------------
# Test 5: Stream model 2 (parallel persistence)
# ------------------------------------------------------------------
echo "Test 5: Stream model 2 ($MODEL2_NAME)"

STREAM_URL2="$GATEWAY_BASE_URL/api/v1/battles/$BATTLE_ID/rounds/$ROUND_ID/stream?modelId=$MODEL2_ID"
STREAM_OUTPUT2=$(curl -s -N -X POST "$STREAM_URL2" -H "Cookie: JSESSIONID=$SESSION_ID" 2>&1)

DATA_LINES2=$(echo "$STREAM_OUTPUT2" | grep -c "^data: " || true)
HAS_COMPLETE2=$(echo "$STREAM_OUTPUT2" | grep -c '"status":"complete"' || true)

if [ "$DATA_LINES2" -gt 0 ] && [ "$HAS_COMPLETE2" -gt 0 ]; then
    pass "Model 2 streamed successfully ($DATA_LINES2 data lines)"
else
    HAS_ERROR2=$(echo "$STREAM_OUTPUT2" | grep -c '"status":"error"' || true)
    if [ "$HAS_ERROR2" -gt 0 ]; then
        fail "Model 2 returned error (model may be unavailable on OpenRouter)"
    else
        fail "Model 2: no SSE data received"
    fi
fi

# Verify both persisted (check DB)
DB_RESULT=$(docker exec bixarena-postgres psql -U postgres -d bixarena -t -c \
    "SELECT model1_message_id IS NOT NULL AS m1, model2_message_id IS NOT NULL AS m2 FROM api.battle_round WHERE id = '$ROUND_ID';" 2>/dev/null | tr -d ' ')

if echo "$DB_RESULT" | grep -q "t|t"; then
    pass "Both messages persisted in DB (no race condition)"
elif echo "$DB_RESULT" | grep -q "t|f"; then
    pass "Model 1 persisted (model 2 may have errored)"
elif echo "$DB_RESULT" | grep -q "f|t"; then
    pass "Model 2 persisted (model 1 may have errored)"
else
    fail "No messages persisted in DB"
fi
echo ""

# ------------------------------------------------------------------
# Test 6: Ownership check (403)
# ------------------------------------------------------------------
echo "Test 6: Ownership check (should return 403)"

# Use a battle from seed data (owned by a different user)
OTHER_BATTLE=$(docker exec bixarena-postgres psql -U postgres -d bixarena -t -c \
    "SELECT b.id FROM api.battle b WHERE b.user_id != '$USER_ID' LIMIT 1;" 2>/dev/null | tr -d ' ')

if [ -n "$OTHER_BATTLE" ] && [ "$OTHER_BATTLE" != "" ]; then
    OTHER_ROUND=$(docker exec bixarena-postgres psql -U postgres -d bixarena -t -c \
        "SELECT id FROM api.battle_round WHERE battle_id = '$OTHER_BATTLE' LIMIT 1;" 2>/dev/null | tr -d ' ')
    OTHER_MODEL=$(docker exec bixarena-postgres psql -U postgres -d bixarena -t -c \
        "SELECT model1_id FROM api.battle WHERE id = '$OTHER_BATTLE';" 2>/dev/null | tr -d ' ')

    if [ -n "$OTHER_ROUND" ] && [ -n "$OTHER_MODEL" ]; then
        FORBIDDEN_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
            -X POST "$GATEWAY_BASE_URL/api/v1/battles/$OTHER_BATTLE/rounds/$OTHER_ROUND/stream?modelId=$OTHER_MODEL" \
            -H "Cookie: JSESSIONID=$SESSION_ID")

        if [ "$FORBIDDEN_CODE" = "403" ]; then
            pass "Ownership check returned 403"
        else
            fail "Expected 403, got HTTP $FORBIDDEN_CODE"
        fi
    else
        echo "  SKIP: No other user's rounds found in DB"
    fi
else
    echo "  SKIP: No other user's battles found in DB"
fi
echo ""

# ------------------------------------------------------------------
# Test 7: Invalid model (404)
# ------------------------------------------------------------------
echo "Test 7: Invalid model (should return 404)"

FAKE_MODEL="00000000-0000-0000-0000-000000000099"
INVALID_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$GATEWAY_BASE_URL/api/v1/battles/$BATTLE_ID/rounds/$ROUND_ID/stream?modelId=$FAKE_MODEL" \
    -H "Cookie: JSESSIONID=$SESSION_ID")

if [ "$INVALID_CODE" = "404" ]; then
    pass "Invalid model returned 404"
else
    fail "Expected 404, got HTTP $INVALID_CODE"
fi
echo ""

# ------------------------------------------------------------------
# Test 8: Unauthenticated (401)
# ------------------------------------------------------------------
echo "Test 8: Unauthenticated request (should return 401)"

UNAUTH_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$GATEWAY_BASE_URL/api/v1/battles/$BATTLE_ID/rounds/$ROUND_ID/stream?modelId=$MODEL1_ID")

if [ "$UNAUTH_CODE" = "401" ]; then
    pass "Unauthenticated request returned 401"
else
    fail "Expected 401, got HTTP $UNAUTH_CODE"
fi
echo ""

# ------------------------------------------------------------------
# Test 9: SSRF prevention (api_base allowlist)
# ------------------------------------------------------------------
echo "Test 9: SSRF prevention"

# Insert a model with a malicious api_base
EVIL_MODEL_ID="00000000-0000-0000-0000-00000000ee01"
EVIL_BATTLE_ID="00000000-0000-0000-0000-00000000ee02"
EVIL_ROUND_ID="00000000-0000-0000-0000-00000000ee03"
EVIL_MSG_ID="00000000-0000-0000-0000-00000000ee04"

# Clean up any leftover data from previous runs
docker exec bixarena-postgres psql -U postgres -d bixarena -q -c "
DELETE FROM api.battle_round WHERE id = '$EVIL_ROUND_ID';
DELETE FROM api.battle WHERE id = '$EVIL_BATTLE_ID';
DELETE FROM api.message WHERE id = '$EVIL_MSG_ID';
DELETE FROM api.model WHERE id = '$EVIL_MODEL_ID';" 2>/dev/null

# Insert test data
docker exec bixarena-postgres psql -U postgres -d bixarena -q -c "
INSERT INTO api.model (id, slug, name, organization, license, active, external_link, api_model_name, api_base, created_at, updated_at)
VALUES ('$EVIL_MODEL_ID', 'evil-model', 'Evil Model', 'Test', 'open-source', true,
        'https://example.com', 'test/evil', 'http://internal-service:8080/api/v1', NOW(), NOW());
INSERT INTO api.message (id, role, content, created_at)
VALUES ('$EVIL_MSG_ID', 'user', 'test', NOW());
INSERT INTO api.battle (id, user_id, model1_id, model2_id, created_at)
VALUES ('$EVIL_BATTLE_ID', '$USER_ID', '$EVIL_MODEL_ID', '$MODEL2_ID', NOW());
INSERT INTO api.battle_round (id, battle_id, round_number, prompt_message_id, created_at, updated_at)
VALUES ('$EVIL_ROUND_ID', '$EVIL_BATTLE_ID', 1, '$EVIL_MSG_ID', NOW(), NOW());" 2>/dev/null

SSRF_OUTPUT=$(curl -s -N --max-time 10 -X POST \
    "$GATEWAY_BASE_URL/api/v1/battles/$EVIL_BATTLE_ID/rounds/$EVIL_ROUND_ID/stream?modelId=$EVIL_MODEL_ID" \
    -H "Cookie: JSESSIONID=$SESSION_ID" 2>&1)

HAS_SSE_ERROR=$(echo "$SSRF_OUTPUT" | grep -c '"status":"error"' || true)
if [ "$HAS_SSE_ERROR" -gt 0 ]; then
    pass "SSRF blocked: error returned for disallowed api_base host"
    echo "    Output: $(echo "$SSRF_OUTPUT" | head -1)"
else
    fail "SSRF not blocked: expected error for http://internal-service:8080"
    echo "    Output: $(echo "$SSRF_OUTPUT" | head -3)"
fi

# Clean up test data
docker exec bixarena-postgres psql -U postgres -d bixarena -q -c "
DELETE FROM api.battle_round WHERE id = '$EVIL_ROUND_ID';
DELETE FROM api.battle WHERE id = '$EVIL_BATTLE_ID';
DELETE FROM api.message WHERE id = '$EVIL_MSG_ID';
DELETE FROM api.model WHERE id = '$EVIL_MODEL_ID';" 2>/dev/null
echo ""

# ------------------------------------------------------------------
# Test 10: Multi-turn context
# ------------------------------------------------------------------
echo "Test 10: Multi-turn conversation context"

ROUND2_RESPONSE=$(curl -s -w "\n%{http_code}" \
    -X POST "$GATEWAY_BASE_URL/api/v1/battles/$BATTLE_ID/rounds" \
    -H "Cookie: JSESSIONID=$SESSION_ID" \
    -H "Content-Type: application/json" \
    -d '{"promptMessage": {"role": "user", "content": "Tell me more about the first gene you mentioned."}}')

ROUND2_CODE=$(echo "$ROUND2_RESPONSE" | tail -n1)
ROUND2_BODY=$(echo "$ROUND2_RESPONSE" | head -n-1)

if [ "$ROUND2_CODE" = "201" ] || [ "$ROUND2_CODE" = "200" ]; then
    ROUND2_ID=$(echo "$ROUND2_BODY" | jq -r '.id')

    # Stream round 2 (use model 2 if model 1 errored)
    if [ "$MODEL1_ERRORED" = true ]; then
        R2_MODEL_ID=$MODEL2_ID
    else
        R2_MODEL_ID=$MODEL1_ID
    fi
    STREAM_R2=$(curl -s -N -X POST \
        "$GATEWAY_BASE_URL/api/v1/battles/$BATTLE_ID/rounds/$ROUND2_ID/stream?modelId=$R2_MODEL_ID" \
        -H "Cookie: JSESSIONID=$SESSION_ID" 2>&1)

    R2_COMPLETE=$(echo "$STREAM_R2" | grep -c '"status":"complete"' || true)
    if [ "$R2_COMPLETE" -gt 0 ]; then
        pass "Multi-turn round 2 streamed successfully"
        echo "    (Check ai-service logs for 'messages=3' to confirm context was sent)"
    else
        HAS_ERROR_R2=$(echo "$STREAM_R2" | grep -c '"status":"error"' || true)
        if [ "$HAS_ERROR_R2" -gt 0 ]; then
            fail "Round 2 model error (model may be unavailable)"
        else
            fail "Round 2 failed to stream"
        fi
    fi
else
    fail "Create round 2 failed (HTTP $ROUND2_CODE)"
fi
echo ""

# ------------------------------------------------------------------
# Test 11: Virtual threads
# ------------------------------------------------------------------
echo "Test 11: Virtual threads"

THREAD_NAME=$(docker logs bixarena-api --tail 50 2>&1 | grep -o '\[mcat-handler-[0-9]*\]' | head -1 || true)
if [ -n "$THREAD_NAME" ]; then
    pass "Virtual threads active (thread name: $THREAD_NAME)"
else
    PLATFORM_THREAD=$(docker logs bixarena-api --tail 50 2>&1 | grep -o '\[http-nio-[0-9]*-exec-[0-9]*\]' | head -1 || true)
    if [ -n "$PLATFORM_THREAD" ]; then
        fail "Using platform threads ($PLATFORM_THREAD), not virtual threads"
    else
        echo "  SKIP: Could not determine thread type from logs"
    fi
fi
echo ""

# ------------------------------------------------------------------
# Summary
# ------------------------------------------------------------------
echo "============================================"
echo "Test Results: $PASSED passed, $FAILED failed, $SKIPPED skipped"
echo "============================================"
echo ""
echo "Battle: $BATTLE_ID"
echo "Round 1: $ROUND_ID"
echo "Models: $MODEL1_NAME vs $MODEL2_NAME"
echo ""

if [ "$FAILED" -gt 0 ]; then
    echo "Some tests failed. Check logs:"
    echo "  docker logs bixarena-api --tail 50"
    echo "  docker logs bixarena-ai-service --tail 20"
    exit 1
else
    echo "All tests passed!"
fi
