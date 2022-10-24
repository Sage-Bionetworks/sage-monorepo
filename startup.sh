#!/usr/bin/bash
# Pass environment variable from ECS to Shiny
echo "" >> .Renviron
env | grep SECRETS_MANAGER_SECRETS > .Renviron

# Now, start up Shiny
/usr/bin/shiny-server
