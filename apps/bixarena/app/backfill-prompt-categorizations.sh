#!/bin/bash
# Backfill Example Prompt Categorizations
#
# Runs AI-based categorization on all example prompts that don't have an
# effective categorization. The /categorizations/run endpoint auto-sets the
# effective categorization when none exists.
#
# Usage: ./backfill-prompt-categorizations.sh <JSESSIONID>
#
# Prerequisites:
#   - Admin account (JSESSIONID from an admin user)
#   - API reachable at $GATEWAY_BASE_URL (default: http://localhost:8113)
#   - AI service running
#
# To get your JSESSIONID:
# 1. Log in to the BixArena web app in your browser as an admin user.
# 2. Open browser DevTools -> Application -> Cookies.
# 3. Copy the value of the JSESSIONID cookie.

set -e

if [ -z "$1" ]; then
    echo "Error: No JSESSIONID provided"
    echo ""
    echo "Usage: $0 <JSESSIONID>"
    echo ""
    exit 1
fi

SESSION_ID="$1"
GATEWAY_BASE_URL="${GATEWAY_BASE_URL:-http://localhost:8113}"
PAGE_SIZE=100
DELAY="${DELAY:-3}"

echo "============================================"
echo "BixArena Example Prompt Categorization Backfill"
echo "============================================"
echo ""
echo "Configuration:"
echo "  Gateway: $GATEWAY_BASE_URL"
echo "  Page size: $PAGE_SIZE"
echo "  Delay between requests: ${DELAY}s"
echo ""

# Counters
CATEGORIZED=0
SKIPPED=0
NO_FIT=0
FAILED=0

# Get total number of example prompts
TOTAL_PROMPTS=$(curl -s "${GATEWAY_BASE_URL}/api/v1/example-prompts?pageNumber=0&pageSize=1" \
    -H "Cookie: JSESSIONID=${SESSION_ID}" | jq -r '.totalElements')

if [ "$TOTAL_PROMPTS" = "null" ] || [ -z "$TOTAL_PROMPTS" ]; then
    echo "Error: Failed to fetch example prompts. Check your JSESSIONID and gateway."
    exit 1
fi

TOTAL_PAGES=$(( (TOTAL_PROMPTS + PAGE_SIZE - 1) / PAGE_SIZE ))
echo "Found $TOTAL_PROMPTS example prompts across $TOTAL_PAGES page(s)"
echo ""

for page in $(seq 0 $((TOTAL_PAGES - 1))); do
    echo "--- Page $((page + 1)) of $TOTAL_PAGES ---"

    PROMPTS_JSON=$(curl -s "${GATEWAY_BASE_URL}/api/v1/example-prompts?pageNumber=${page}&pageSize=${PAGE_SIZE}" \
        -H "Cookie: JSESSIONID=${SESSION_ID}")

    # Extract id + effectiveCategorizationId for each prompt
    ALL_PROMPTS=$(echo "$PROMPTS_JSON" | jq -r '.examplePrompts[] | "\(.id) \(.effectiveCategorizationId // "null")"')

    if [ -z "$ALL_PROMPTS" ]; then
        echo "  No prompts on this page."
        continue
    fi

    while IFS=' ' read -r PROMPT_ID CATEGORIZATION_ID; do
        if [ "$CATEGORIZATION_ID" != "null" ]; then
            SKIPPED=$((SKIPPED + 1))
            echo "  [SKIP] $PROMPT_ID -> already has effective categorization"
            continue
        fi

        RESPONSE=$(curl -s -w "\n%{http_code}" \
            -X POST "${GATEWAY_BASE_URL}/api/v1/example-prompts/${PROMPT_ID}/categorizations/run" \
            -H "Cookie: JSESSIONID=${SESSION_ID}")

        HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
        BODY=$(echo "$RESPONSE" | head -n-1)

        case "$HTTP_CODE" in
            201)
                STATUS=$(echo "$BODY" | jq -r '.status')
                CATEGORY=$(echo "$BODY" | jq -r '.category')
                case "$STATUS" in
                    matched)
                        CATEGORIZED=$((CATEGORIZED + 1))
                        echo "  [OK]    $PROMPT_ID -> category=$CATEGORY"
                        ;;
                    abstained)
                        NO_FIT=$((NO_FIT + 1))
                        echo "  [NOFIT] $PROMPT_ID -> classifier declared no fit (persisted)"
                        ;;
                    failed)
                        FAILED=$((FAILED + 1))
                        echo "  [FAIL]  $PROMPT_ID -> ai classifier error (persisted, retryable)"
                        ;;
                    *)
                        FAILED=$((FAILED + 1))
                        echo "  [FAIL]  $PROMPT_ID -> unknown status=$STATUS"
                        ;;
                esac
                ;;
            *)
                FAILED=$((FAILED + 1))
                DETAIL=$(echo "$BODY" | jq -r '.detail // .message // "unknown error"' 2>/dev/null)
                echo "  [FAIL]  $PROMPT_ID -> HTTP $HTTP_CODE: $DETAIL"
                ;;
        esac

        sleep "$DELAY"
    done <<< "$ALL_PROMPTS"
done

echo ""
echo "============================================"
echo "Backfill Complete"
echo "============================================"
echo "  Categorized (status=matched): $CATEGORIZED"
echo "  Already had categorization: $SKIPPED"
echo "  No category fit (status=abstained): $NO_FIT"
echo "  Classifier failed (status=failed or HTTP error): $FAILED"
echo "  Total processed: $((CATEGORIZED + SKIPPED + NO_FIT + FAILED))"
echo ""

if [ "$FAILED" -gt 0 ]; then
    echo "Some prompts failed categorization. Common causes:"
    echo "  - AI service temporarily unavailable"
    echo "  - OpenRouter rate limit"
    echo "Re-run this script to retry failed prompts."
fi
