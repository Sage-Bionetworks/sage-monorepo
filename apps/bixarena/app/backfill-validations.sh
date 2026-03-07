#!/bin/bash
# Backfill Battle Validations
#
# Runs AI-based validation on all battles that don't have an effective validation.
# The /validations/run endpoint auto-sets the effective validation when none exists.
#
# Usage: ./backfill-validations.sh <JSESSIONID>
#
# Prerequisites:
#   - Admin account (JSESSIONID from an admin user)
#   - API gateway running on localhost:8113
#   - AI service running
#
# To get your JSESSIONID:
# 1. Open http://localhost:8100 in browser
# 2. Click Login and authenticate with Synapse
# 3. Press F12 -> Application -> Cookies -> localhost:8100
# 4. Copy the JSESSIONID value

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
DELAY="${DELAY:-1}"

echo "============================================"
echo "BixArena Battle Validation Backfill"
echo "============================================"
echo ""
echo "Configuration:"
echo "  Gateway: $GATEWAY_BASE_URL"
echo "  Page size: $PAGE_SIZE"
echo "  Delay between requests: ${DELAY}s"
echo ""

# Counters
TOTAL=0
VALIDATED=0
SKIPPED=0
FAILED=0

# Get total number of battles
TOTAL_BATTLES=$(curl -s "${GATEWAY_BASE_URL}/api/v1/battles?pageNumber=0&pageSize=1" \
    -H "Cookie: JSESSIONID=${SESSION_ID}" | jq -r '.page.totalElements')

if [ "$TOTAL_BATTLES" = "null" ] || [ -z "$TOTAL_BATTLES" ]; then
    echo "Error: Failed to fetch battles. Check your JSESSIONID and gateway."
    exit 1
fi

TOTAL_PAGES=$(( (TOTAL_BATTLES + PAGE_SIZE - 1) / PAGE_SIZE ))
echo "Found $TOTAL_BATTLES battles across $TOTAL_PAGES page(s)"
echo ""

for page in $(seq 0 $((TOTAL_PAGES - 1))); do
    echo "--- Page $((page + 1)) of $TOTAL_PAGES ---"

    # Fetch battles for this page
    BATTLES_JSON=$(curl -s "${GATEWAY_BASE_URL}/api/v1/battles?pageNumber=${page}&pageSize=${PAGE_SIZE}" \
        -H "Cookie: JSESSIONID=${SESSION_ID}")

    # Extract all battle IDs and their validation status
    ALL_BATTLES=$(echo "$BATTLES_JSON" | jq -r '.battles[] | "\(.id) \(.effectiveValidationId // "null")"')

    if [ -z "$ALL_BATTLES" ]; then
        echo "  No battles on this page."
        continue
    fi

    while IFS=' ' read -r BATTLE_ID VALIDATION_ID; do
        if [ "$VALIDATION_ID" != "null" ]; then
            SKIPPED=$((SKIPPED + 1))
            echo "  [SKIP] $BATTLE_ID -> already has effective validation"
            continue
        fi

        TOTAL=$((TOTAL + 1))

        RESPONSE=$(curl -s -w "\n%{http_code}" \
            -X POST "${GATEWAY_BASE_URL}/api/v1/battles/${BATTLE_ID}/validations/run" \
            -H "Cookie: JSESSIONID=${SESSION_ID}" \
            -H "Content-Type: application/json" \
            -d '{}')

        HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
        BODY=$(echo "$RESPONSE" | head -n-1)

        if [ "$HTTP_CODE" = "201" ]; then
            IS_BIO=$(echo "$BODY" | jq -r '.isBiomedical')
            CONFIDENCE=$(echo "$BODY" | jq -r '.confidence')
            VALIDATED=$((VALIDATED + 1))
            echo "  [OK]   $BATTLE_ID -> isBiomedical=$IS_BIO confidence=$CONFIDENCE"
        else
            FAILED=$((FAILED + 1))
            DETAIL=$(echo "$BODY" | jq -r '.detail // .message // "unknown error"' 2>/dev/null)
            echo "  [FAIL] $BATTLE_ID -> HTTP $HTTP_CODE: $DETAIL"
        fi

        sleep "$DELAY"
    done <<< "$ALL_BATTLES"
done

echo ""
echo "============================================"
echo "Backfill Complete"
echo "============================================"
echo "  Validated: $VALIDATED"
echo "  Already had validation: $SKIPPED"
echo "  Failed: $FAILED"
echo "  Total processed: $((VALIDATED + SKIPPED + FAILED))"
echo ""

if [ "$FAILED" -gt 0 ]; then
    echo "Some battles failed validation. Common causes:"
    echo "  - Battle has no rounds/messages (mock data)"
    echo "  - AI service temporarily unavailable"
    echo "Re-run this script to retry failed battles."
fi
