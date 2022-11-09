#!/usr/bin/bash

# Split up SECRETS_MANAGER_SECRETS env var (from ECS) into individual variables, suitable for the application
export $(echo $SECRETS_MANAGER_SECRETS | jq -r 'to_entries[] | [.key,.value] | join("=")')

# Now, start up Shiny
/usr/bin/shiny-server
