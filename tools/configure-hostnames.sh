#!/bin/bash
#
# Map the name of the apps in ./apps to 127.0.0.1.

# list of hostnames (defined in alphabetical order)
declare -a hostnames=(
  "127.0.0.1 agora-api"
  "127.0.0.1 agora-gene-api"
  "127.0.0.1 agora-mongo"
  "127.0.0.1 amp-als-apex"
  "127.0.0.1 amp-als-dataset-service"
  "127.0.0.1 amp-als-opensearch"
  "127.0.0.1 amp-als-postgres"
  "127.0.0.1 amp-als-user-service"
  "127.0.0.1 bixarena-postgres"
  "127.0.0.1 iatlas-api"
  "127.0.0.1 iatlas-postgres"
  "127.0.0.1 model-ad-api"
  "127.0.0.1 model-ad-mongo"
  "127.0.0.1 observability-apex"
  "127.0.0.1 observability-grafana"
  "127.0.0.1 observability-loki"
  "127.0.0.1 observability-otel-collector"
  "127.0.0.1 observability-prometheus"
  "127.0.0.1 observability-pyroscope"
  "127.0.0.1 observability-tempo"
  "127.0.0.1 openchallenges-api-gateway"
  "127.0.0.1 openchallenges-app"
  "127.0.0.1 openchallenges-auth-service"
  "127.0.0.1 openchallenges-challenge-service"
  "127.0.0.1 openchallenges-config-server"
  "127.0.0.1 openchallenges-core-service"
  "127.0.0.1 openchallenges-image-service"
  "127.0.0.1 openchallenges-mcp-server"
  "127.0.0.1 openchallenges-opensearch"
  "127.0.0.1 openchallenges-organization-service"
  "127.0.0.1 openchallenges-postgres"
  "127.0.0.1 openchallenges-schema-registry"
  "127.0.0.1 openchallenges-service-registry"
  "127.0.0.1 openchallenges-thumbor"
)

# add hostnames
for hostname in "${hostnames[@]}"; do
  if ! grep -Fxq "$hostname" /etc/hosts; then
    echo "$hostname" >> /etc/hosts
  fi
done