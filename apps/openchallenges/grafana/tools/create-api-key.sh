#!/usr/bin/env bash

api_token_name=apikeycurl

# Delete the existing admin key
curl -X DELETE \
  --silent \
  -H "Content-Type: application/json" \
  http://openchallenges:changeme@openchallenges-grafana:3000/api/auth/keys/2

# TODO: Read username, password and host from .env file
curl -X POST \
  --silent \
  -H "Content-Type: application/json" \
  -d '{"name":"'${api_token_name}'", "role": "Admin"}' \
  http://openchallenges:changeme@openchallenges-grafana:3000/api/auth/keys