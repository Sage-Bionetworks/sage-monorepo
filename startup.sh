#!/usr/bin/bash

# Pass environment variable from ECS to Shiny
echo "" >> .Renviron
echo $SECRETS_MANAGER_SECRETS | jq -r 'to_entries[] | [.key,.value] | join("=")' >> .Renviron

# Now, start up Shiny
/usr/bin/shiny-server
