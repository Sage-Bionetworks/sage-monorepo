#!/bin/bash

# Script to start Storybook composition with all child storybooks
# This ensures child storybooks start before the composition host to avoid CORS issues
# See https://github.com/storybookjs/storybook/issues/18405 and https://github.com/storybookjs/storybook/issues/17696

set -e

echo "🚀 Starting child storybooks..."

# Start child storybooks in background
echo "   Starting Agora storybook on port 4401..."
nx start agora-storybook --ci > /dev/null 2>&1 &
AGORA_PID=$!

echo "   Starting Explorers storybook on port 4402..."
nx start explorers-storybook --ci > /dev/null 2>&1 &
EXPLORERS_PID=$!

# Function to cleanup background processes on exit
cleanup() {
  echo ""
  echo "🛑 Shutting down storybooks..."
  kill $AGORA_PID $EXPLORERS_PID 2>/dev/null || true
  exit 0
}

# Trap Ctrl+C and other termination signals
trap cleanup SIGINT SIGTERM EXIT

# Function to wait for a URL to be ready
wait_for_url() {
  local url=$1
  local name=$2
  local max_attempts=60
  local attempt=0

  while [ $attempt -lt $max_attempts ]; do
    if curl -s -f "$url" > /dev/null 2>&1; then
      echo "   ✓ $name is ready"
      return 0
    fi
    attempt=$((attempt + 1))
    sleep 1
  done

  echo "   ✗ $name failed to start after $max_attempts seconds"
  return 1
}

# Wait for child storybooks to be ready
echo "   Waiting for child storybooks to start..."
wait_for_url "http://localhost:4401" "Agora storybook" || cleanup
wait_for_url "http://localhost:4402" "Explorers storybook" || cleanup

# Wait for story indexes to be ready (needed for composition to work on first run)
echo "   Waiting for story indexes..."
wait_for_url "http://localhost:4401/index.json" "Agora story index" || cleanup
wait_for_url "http://localhost:4402/index.json" "Explorers story index" || cleanup

echo ""
echo "✨ Starting composition host on port 4400..."
echo "   View at: http://localhost:4400"
echo ""
echo "Press Ctrl+C to stop all storybooks"

# Start composition storybook in foreground
nx run storybook:storybook-dev
