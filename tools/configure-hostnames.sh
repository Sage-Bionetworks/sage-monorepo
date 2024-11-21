#!/bin/bash
#
# Map the name of the apps in ./apps to 127.0.0.1.

# list of hostnames (defined in alphabetical order)
declare -a hostnames=(
  "127.0.0.1 agora-api"
  "127.0.0.1 agora-mongo"
  "127.0.0.1 iatlas-postgres"
  "127.0.0.1 model-ad-api"
  "127.0.0.1 model-ad-mongo"
  "127.0.0.1 openchallenges-api-gateway"
  "127.0.0.1 openchallenges-app"
  "127.0.0.1 openchallenges-challenge-service"
  "127.0.0.1 openchallenges-config-server"
  "127.0.0.1 openchallenges-core-service"
  "127.0.0.1 openchallenges-elasticsearch"
  "127.0.0.1 openchallenges-grafana"
  "127.0.0.1 openchallenges-image-service"
  "127.0.0.1 openchallenges-mariadb"
  "127.0.0.1 openchallenges-mysqld-exporter"
  "127.0.0.1 openchallenges-nifi-registry"
  "127.0.0.1 openchallenges-nifi"
  "127.0.0.1 openchallenges-opensearch"
  "127.0.0.1 openchallenges-organization-service"
  "127.0.0.1 openchallenges-prometheus"
  "127.0.0.1 openchallenges-schema-registry"
  "127.0.0.1 openchallenges-service-registry"
  "127.0.0.1 openchallenges-thumbor"
  "127.0.0.1 openchallenges-zipkin"
  "127.0.0.1 schematic-api"
)

# add hostnames
for hostname in "${hostnames[@]}"; do
  if ! grep -Fxq "$hostname" /etc/hosts; then
    echo "$hostname" >> /etc/hosts
  fi
done