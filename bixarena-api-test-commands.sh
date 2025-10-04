# BixArena API Test Commands

## 1. Test Example Prompts Endpoint (Our New Feature!)
# List all example prompts with pagination
curl -X GET "http://localhost:8112/v1/example-prompts?pageNumber=0&pageSize=10&sort=created_at&direction=desc" \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" | jq '.'

# Test filtering example prompts by source and active status
curl -X GET "http://localhost:8112/v1/example-prompts?source=pubmedqa&active=true&search=diabetes&pageSize=5" \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" | jq '.'

## 2. Test Models Endpoint
# List all models with search and filtering
curl -X GET "http://localhost:8112/v1/models?pageNumber=0&pageSize=5&search=gpt&active=true&sort=name&direction=asc" \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" | jq '.'

# Test models with organization filter
curl -X GET "http://localhost:8112/v1/models?organization=OpenAI&license=commercial" \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" | jq '.'

## 3. Test Leaderboards Endpoint
# List all available leaderboards
curl -X GET "http://localhost:8112/v1/leaderboards" \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" | jq '.'

# Get specific leaderboard entries (you may need to replace 'open-source' with actual leaderboard ID)
curl -X GET "http://localhost:8112/v1/leaderboards/open-source?pageNumber=0&pageSize=10&sort=rank&direction=asc" \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" | jq '.'

## Additional Health Check
curl -X GET "http://localhost:8112/actuator/health" \
  -H "Accept: application/json" | jq '.'