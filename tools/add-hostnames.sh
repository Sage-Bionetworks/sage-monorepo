#!/bin/bash
#
# Map the name of the apps in ./apps to 127.0.0.1.

# # list the names of the apps and services
# readarray -t app_names < <(find ./apps -mindepth 1 -maxdepth 1 -type d -printf '%P\n')

# # add hostnames
# for app_name in "${app_names[@]}"; do
#     line="127.0.0.1 $app_name"
#     if ! grep -Fxq "$line" /etc/hosts; then
#       echo $line >> /etc/hosts
#     fi
# done

# list of hostnames (defined in alphabetical order)
echo "127.0.0.1 challenge-api-gateway" >> /etc/hosts
echo "127.0.0.1 challenge-core-service" >> /etc/hosts
echo "127.0.0.1 challenge-elasticsearch" >> /etc/hosts
echo "127.0.0.1 challenge-kibana" >> /etc/hosts
echo "127.0.0.1 challenge-logstash" >> /etc/hosts
echo "127.0.0.1 challenge-keycloak" >> /etc/hosts
echo "127.0.0.1 challenge-mariadb" >> /etc/hosts
echo "127.0.0.1 challenge-mongodb" >> /etc/hosts
echo "127.0.0.1 challenge-platform" >> /etc/hosts
echo "127.0.0.1 challenge-postgresql" >> /etc/hosts
echo "127.0.0.1 challenge-registry" >> /etc/hosts
echo "127.0.0.1 challenge-service-registry" >> /etc/hosts
echo "127.0.0.1 challenge-user-service" >> /etc/hosts