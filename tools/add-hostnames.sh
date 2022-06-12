#!/bin/bash
#
# Map the name of the apps in ./apps to 127.0.0.1.

# list the names of the apps and services
readarray -t app_names < <(find ./apps -mindepth 1 -maxdepth 1 -type d -printf '%P\n')

# add hostnames
for app_name in "${app_names[@]}"; do
    line="127.0.0.1 $app_name"
    if ! grep -Fxq "$line" /etc/hosts; then
      echo $line >> /etc/hosts
    fi
done
