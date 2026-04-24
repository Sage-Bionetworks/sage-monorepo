#!/bin/bash
# Backfill Battle Categorizations
#
# Runs AI-based categorization on all biomedical, ended battles that don't
# have an effective categorization. The /categorizations/run endpoint
# auto-sets the effective categorization when none exists, and returns 409
# if the battle is not biomedical (which the backfill treats as a skip).
#
# Usage: ./backfill-battle-categorizations.sh <JSESSIONID>
#
# Prerequisites:
#   - Admin account (JSESSIONID from an admin user)
#   - API reachable at $GATEWAY_BASE_URL (default: http://localhost:8113)
#   - AI service running
#   - Battles have been validated (effectiveValidationId populated)
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
echo "BixArena Battle Categorization Backfill"
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
NO_ROUNDS=0
NOT_BIOMEDICAL=0
FAILED=0

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

    BATTLES_JSON=$(curl -s "${GATEWAY_BASE_URL}/api/v1/battles?pageNumber=${page}&pageSize=${PAGE_SIZE}" \
        -H "Cookie: JSESSIONID=${SESSION_ID}")

    # Extract id + effectiveCategorizationId + endedAt for each battle
    ALL_BATTLES=$(echo "$BATTLES_JSON" | jq -r '.battles[] | "\(.id) \(.effectiveCategorizationId // "null") \(.endedAt // "null")"')

    if [ -z "$ALL_BATTLES" ]; then
        echo "  No battles on this page."
        continue
    fi

    while IFS=' ' read -r BATTLE_ID CATEGORIZATION_ID ENDED_AT; do
        if [ "$CATEGORIZATION_ID" != "null" ]; then
            SKIPPED=$((SKIPPED + 1))
            echo "  [SKIP] $BATTLE_ID -> already has effective categorization"
            continue
        fi

        if [ "$ENDED_AT" = "null" ]; then
            SKIPPED=$((SKIPPED + 1))
            echo "  [SKIP] $BATTLE_ID -> not ended"
            continue
        fi

        RESPONSE=$(curl -s -w "\n%{http_code}" \
            -X POST "${GATEWAY_BASE_URL}/api/v1/battles/${BATTLE_ID}/categorizations/run" \
            -H "Cookie: JSESSIONID=${SESSION_ID}")

        HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
        BODY=$(echo "$RESPONSE" | head -n-1)

        case "$HTTP_CODE" in
            201)
                STATUS=$(echo "$BODY" | jq -r '.status')
                CATEGORIES=$(echo "$BODY" | jq -r '.categories | join(",")')
                case "$STATUS" in
                    matched)
                        CATEGORIZED=$((CATEGORIZED + 1))
                        echo "  [OK]    $BATTLE_ID -> categories=[$CATEGORIES]"
                        ;;
                    abstained)
                        NO_FIT=$((NO_FIT + 1))
                        echo "  [NOFIT] $BATTLE_ID -> classifier declared no fit (persisted)"
                        ;;
                    failed)
                        FAILED=$((FAILED + 1))
                        echo "  [FAIL]  $BATTLE_ID -> ai classifier error (persisted, retryable)"
                        ;;
                    *)
                        FAILED=$((FAILED + 1))
                        echo "  [FAIL]  $BATTLE_ID -> unknown status=$STATUS"
                        ;;
                esac
                ;;
            404)
                # BattleRoundNotFoundException — battle exists but has no rounds
                # to collect prompts from. Not an error, just nothing to do.
                NO_ROUNDS=$((NO_ROUNDS + 1))
                echo "  [SKIP]  $BATTLE_ID -> no rounds (404)"
                ;;
            409)
                NOT_BIOMEDICAL=$((NOT_BIOMEDICAL + 1))
                echo "  [SKIP]  $BATTLE_ID -> not biomedical (409)"
                ;;
            *)
                FAILED=$((FAILED + 1))
                DETAIL=$(echo "$BODY" | jq -r '.detail // .message // "unknown error"' 2>/dev/null)
                echo "  [FAIL]  $BATTLE_ID -> HTTP $HTTP_CODE: $DETAIL"
                ;;
        esac

        sleep "$DELAY"
    done <<< "$ALL_BATTLES"
done

echo ""
echo "============================================"
echo "Backfill Complete"
echo "============================================"
echo "  Categorized (status=matched): $CATEGORIZED"
echo "  Already had categorization / not ended: $SKIPPED"
echo "  No category fit (status=abstained): $NO_FIT"
echo "  No rounds (404): $NO_ROUNDS"
echo "  Not biomedical (409): $NOT_BIOMEDICAL"
echo "  Classifier failed (status=failed or HTTP error): $FAILED"
echo "  Total processed: $((CATEGORIZED + SKIPPED + NO_FIT + NO_ROUNDS + NOT_BIOMEDICAL + FAILED))"
echo ""

if [ "$FAILED" -gt 0 ]; then
    echo "Some battles failed categorization. Common causes:"
    echo "  - Battle has no rounds/messages"
    echo "  - Battle has no effective validation yet (run validation backfill first)"
    echo "  - AI service temporarily unavailable"
    echo "Re-run this script to retry failed battles."
fi
