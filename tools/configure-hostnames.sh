#!/bin/bash
#
# Map the name of the apps in ./apps to 127.0.0.1.

# list of hostnames (defined in alphabetical order)
declare -a hostnames=(
  "127.0.0.1 challenge-registry-api-gateway"
  "127.0.0.1 challenge-registry-auth-service"
  "127.0.0.1 challenge-registry-config-service"
  "127.0.0.1 challenge-registry-core-service"
  "127.0.0.1 challenge-registry-keycloak"
  "127.0.0.1 challenge-registry-mariadb"
  "127.0.0.1 challenge-registry-mongo"
  "127.0.0.1 challenge-registry-organization-service"
  "127.0.0.1 challenge-registry-postgres"
  "127.0.0.1 challenge-registry-rabbitmq"
  "127.0.0.1 challenge-service-registry"
  "127.0.0.1 challenge-service"
  "127.0.0.1 challenge-user-service"
  "127.0.0.1 schematic-api"
)

# add hostnames
for hostname in "${hostnames[@]}"; do
  if ! grep -Fxq "$hostname" /etc/hosts; then
    echo "$hostname" >> /etc/hosts
  fi
done