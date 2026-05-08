#!/usr/bin/env bash
# =============================================================================
# BixArena Quest Management API — Interactive Demo Script
# =============================================================================
# Demonstrates all quest and quest-post CRUD operations via the API gateway.
#
# Usage:
#   ./demo-quest-api.sh <JSESSIONID>
#
# Prerequisites:
#   - API gateway running on localhost:8113
#   - An admin user's JSESSIONID cookie value
#   - curl and jq installed
# =============================================================================

set -euo pipefail

# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------
BASE_URL="http://localhost:8113/api/v1"
QUEST_ID="demo-quest-$(date +%s)"
CONTENT_TYPE="Content-Type: application/json"

# ---------------------------------------------------------------------------
# Parse arguments
# ---------------------------------------------------------------------------
if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <JSESSIONID>"
  echo ""
  echo "  JSESSIONID   The value of your JSESSIONID cookie (admin user)"
  exit 1
fi

JSESSIONID="$1"
COOKIE="Cookie: JSESSIONID=${JSESSIONID}"

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
step=0

pause() {
  echo ""
  read -r -p "Press Enter to continue to the next step (or Ctrl-C to quit)..."
  echo ""
}

header() {
  step=$((step + 1))
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  echo "  Step ${step}: $1"
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
}

api_call() {
  # $1 = caller label (ADMIN / ANONYMOUS), $2 = HTTP method, $3 = path,
  # $4 = optional JSON body
  local caller="$1" method="$2" path="$3" body="${4:-}"
  local url="${BASE_URL}${path}"
  local use_cookie=true

  if [[ "${caller}" == "ANONYMOUS" ]]; then
    use_cookie=false
  fi

  echo "→ [${caller}] ${method} ${url}"
  if [[ -n "${body}" ]]; then
    echo "  Body: $(echo "${body}" | jq -c .)"
  fi
  echo ""

  local http_code response
  if [[ -n "${body}" && "${use_cookie}" == true ]]; then
    response=$(curl -s -w "\n%{http_code}" -X "${method}" "${url}" \
      -H "${CONTENT_TYPE}" -H "${COOKIE}" -d "${body}")
  elif [[ -n "${body}" ]]; then
    response=$(curl -s -w "\n%{http_code}" -X "${method}" "${url}" \
      -H "${CONTENT_TYPE}" -d "${body}")
  elif [[ "${use_cookie}" == true ]]; then
    response=$(curl -s -w "\n%{http_code}" -X "${method}" "${url}" \
      -H "${COOKIE}")
  else
    response=$(curl -s -w "\n%{http_code}" -X "${method}" "${url}")
  fi

  http_code=$(echo "${response}" | tail -n1)
  local body_out
  body_out=$(echo "${response}" | sed '$d')

  echo "← HTTP ${http_code}"
  if [[ -n "${body_out}" ]]; then
    echo "${body_out}" | jq . 2>/dev/null || echo "${body_out}"
  fi
  echo ""

  # Fail on 4xx/5xx (except when we intentionally expect errors)
  if [[ "${http_code}" -ge 400 && "${EXPECT_ERROR:-}" != "true" ]]; then
    echo "ERROR: Request failed with status ${http_code}. Aborting."
    exit 1
  fi
}

# Shorthand: admin request (authenticated with JSESSIONID)
api_admin() {
  api_call "ADMIN" "$@"
}

# Shorthand: anonymous request (no cookie)
api_anon() {
  api_call "ANONYMOUS" "$@"
}

# ---------------------------------------------------------------------------
# Demo starts here
# ---------------------------------------------------------------------------
echo ""
echo "╔══════════════════════════════════════════════════════════════════════════╗"
echo "║          BixArena Quest Management API — Interactive Demo              ║"
echo "╠══════════════════════════════════════════════════════════════════════════╣"
echo "║  Gateway:  ${BASE_URL}"
echo "║  Quest ID: ${QUEST_ID}"
echo "╚══════════════════════════════════════════════════════════════════════════╝"
echo ""
echo "This script will walk you through every quest API operation:"
echo "  1.  Create a quest"
echo "  2.  Update quest metadata"
echo "  3.  Create Post 0 — public (no gates)"
echo "  4.  Create Post 1 — progress-gated (requires 100 battles)"
echo "  5.  Create Post 2 — tier-gated (requires knight tier)"
echo "  6.  Get the quest (anonymous — shows content gating)"
echo "  7.  Lower progress gate on Post 1 (requiredProgress 100 → 0)"
echo "  8.  Re-fetch as anonymous (Post 1 now unlocked)"
echo "  9.  Restore Post 1 gate (requiredProgress 0 → 100)"
echo "  10. Get the quest (admin — shows gate bypass)"
echo "  11. Reorder posts"
echo "  12. Delete a post"
echo "  13. Get contributors (empty list expected)"
echo "  14. Delete the quest (cleanup)"
echo ""
pause

# ── Step 1: Create a quest ──────────────────────────────────────────────────
header "Create a new quest"
echo "Creating quest '${QUEST_ID}' with a goal of 500 blocks."
echo "Start date: 2026-03-01, End date: 2026-06-30, activePostIndex: 0"
echo ""

api_admin POST "/quests" '{
  "questId": "'"${QUEST_ID}"'",
  "title": "Demo Quest: Build the Arena",
  "description": "A demo quest to showcase the Quest Management API.",
  "goal": 500,
  "startDate": "2026-03-01T00:00:00Z",
  "endDate": "2026-06-30T23:59:59Z",
  "activePostIndex": 0
}'

pause

# ── Step 2: Update quest metadata ────────────────────────────────────────────
header "Update quest metadata"
echo "Changing the title, description, and goal to 1000 blocks."
echo ""

api_admin PUT "/quests/${QUEST_ID}" '{
  "questId": "'"${QUEST_ID}"'",
  "title": "Demo Quest: Build the Grand Arena",
  "description": "An updated demo quest with a higher goal!",
  "goal": 1000,
  "startDate": "2026-03-01T00:00:00Z",
  "endDate": "2026-06-30T23:59:59Z",
  "activePostIndex": 0
}'

pause

# ── Step 3: Create three posts ───────────────────────────────────────────────
header "Create Post 0 — Public post (no gates)"
echo "A simple public post with two images, no unlock requirements."
echo ""

api_admin POST "/quests/${QUEST_ID}/posts" '{
  "title": "Week 1: Foundation Laid",
  "description": "The arena foundation has been placed. 200 blocks of stone and dirt form the base layer.",
  "date": "2026-03-07",
  "images": [
    "https://picsum.photos/seed/arena1/800/450",
    "https://picsum.photos/seed/arena2/800/450"
  ]
}'

pause

header "Create Post 1 — Progress-gated post (requires 100 battles)"
echo "This post is locked until the quest reaches 100 total battles."
echo ""

api_admin POST "/quests/${QUEST_ID}/posts" '{
  "title": "Week 2: Walls Rising",
  "description": "The outer walls are taking shape. Stone brick walls rise 10 blocks high around the perimeter.",
  "date": "2026-03-14",
  "images": [
    "https://picsum.photos/seed/arena3/800/450"
  ],
  "requiredProgress": 100
}'

pause

header "Create Post 2 — Tier-gated post (requires knight tier)"
echo "This post is only visible to knight-tier and champion-tier contributors."
echo ""

api_admin POST "/quests/${QUEST_ID}/posts" '{
  "title": "Week 3: Secret Chamber",
  "description": "A hidden room beneath the arena has been revealed — only for dedicated builders!",
  "date": "2026-03-21",
  "images": [
    "https://picsum.photos/seed/arena4/800/450",
    "https://picsum.photos/seed/arena5/800/450",
    "https://picsum.photos/seed/arena6/800/450"
  ],
  "requiredTier": "knight"
}'

pause

# ── Step 4: Get the quest (anonymous) ────────────────────────────────────────
header "Get the quest (anonymous view — content gating in effect)"
echo "Fetching the quest without authentication."
echo "Post 0 (public) should have full content."
echo "Post 1 (progress-gated, 100 battles) should have null description and empty images."
echo "Post 2 (tier-gated, knight) should have null description and empty images."
echo ""

api_anon GET "/quests/${QUEST_ID}"

pause

# ── Unlock progress-gated post (3 sub-steps) ──────────────────────────────
header "Lower the progress gate on Post 1 (requiredProgress 100 → 0)"
echo "Post 1 requires 100 battles (totalBlocks) to unlock."
echo "In production, totalBlocks increases as users complete battles."
echo "Since we cannot simulate real battles in this demo, we lower the"
echo "requiredProgress threshold to 0 to illustrate the unlock effect."
echo ""

api_admin PUT "/quests/${QUEST_ID}/posts/1" '{
  "title": "Week 2: Walls Rising",
  "description": "The outer walls are taking shape. Stone brick walls rise 10 blocks high around the perimeter.",
  "date": "2026-03-14",
  "images": [
    "https://picsum.photos/seed/arena3/800/450"
  ],
  "requiredProgress": 0
}'

pause

header "Re-fetch as anonymous — Post 1 should now show full content"
echo "The progress gate has been lowered to 0, so Post 1 is now unlocked"
echo "for everyone. Post 2 (tier-gated) remains locked."
echo ""

api_anon GET "/quests/${QUEST_ID}"

pause

header "Restore Post 1 gate (requiredProgress 0 → 100)"
echo "Restoring the original gate for the remaining demo steps."
echo ""

api_admin PUT "/quests/${QUEST_ID}/posts/1" '{
  "title": "Week 2: Walls Rising",
  "description": "The outer walls are taking shape. Stone brick walls rise 10 blocks high around the perimeter.",
  "date": "2026-03-14",
  "images": [
    "https://picsum.photos/seed/arena3/800/450"
  ],
  "requiredProgress": 100
}'

pause

# ── Step 6: Get the quest (admin — ungated) ──────────────────────────────────
header "Get the quest (admin view — ungated content)"
echo "Fetching the quest via the admin endpoint. All posts are returned with"
echo "full content (description and images) regardless of unlock requirements."
echo ""

api_admin GET "/admin/quests/${QUEST_ID}"

pause

# ── Reorder posts ────────────────────────────────────────────────────
header "Reorder posts — Reverse the order"
echo "Current order: [0, 1, 2] → New order: [2, 1, 0]"
echo "Post 2 (Secret Chamber) becomes index 0, Post 0 (Foundation) becomes index 2."
echo "The two-pass reorder avoids unique constraint violations."
echo ""

api_admin PUT "/quests/${QUEST_ID}/posts/reorder" '{
  "postIndexes": [2, 1, 0]
}'

echo "Verifying the new order by fetching the quest..."
echo ""

api_admin GET "/admin/quests/${QUEST_ID}"

pause

# ── Step 9: Delete a post ────────────────────────────────────────────────────
header "Delete Post at index 1"
echo "Removing the middle post (originally 'Walls Rising')."
echo "Remaining posts are automatically reindexed to close the gap."
echo ""

api_admin DELETE "/quests/${QUEST_ID}/posts/1"

echo "Fetching the quest to confirm the post was removed..."
echo ""

api_admin GET "/admin/quests/${QUEST_ID}"

pause

# ── Step 10: Get contributors ────────────────────────────────────────────────
header "List quest contributors"
echo "Fetching contributors for this demo quest."
echo "Expected: empty list (no battles have been completed for this quest)."
echo ""

api_anon GET "/quests/${QUEST_ID}/contributors?minBattles=1&limit=10"

pause

# ── Step 11: Cleanup — Delete the quest ──────────────────────────────────────
header "Delete the quest (cleanup)"
echo "Removing the demo quest and all its posts from the database."
echo ""

api_admin DELETE "/quests/${QUEST_ID}"

echo "Verifying deletion by trying to fetch the quest (expect 404)..."
echo ""

EXPECT_ERROR=true api_anon GET "/quests/${QUEST_ID}"

# ── Done ─────────────────────────────────────────────────────────────────────
echo "╔══════════════════════════════════════════════════════════════════════════╗"
echo "║                         Demo complete!                                 ║"
echo "╚══════════════════════════════════════════════════════════════════════════╝"
