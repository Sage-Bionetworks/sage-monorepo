#!/usr/bin/bash

# Split up SECRETS_MANAGER_SECRETS env var (from ECS) into individual variables, suitable for the application
# TODO This doesn't seem to work
export $(echo $SECRETS_MANAGER_SECRETS | jq -r 'to_entries[] | [.key,.value] | join("=")')

# TODO This is the 'old' way.  Remove once the above works
# Pass environment variable from ECS to Shiny
echo "" >> .Renviron
echo $SECRETS_MANAGER_SECRETS | jq -r 'to_entries[] | [.key,.value] | join("=")' >> .Renviron

# Now, start up Shiny
/usr/bin/shiny-server
