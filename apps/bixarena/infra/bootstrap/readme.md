Files in this directory are used to install prerequisites to the project.

## devzone
This directory contains a CDK script to create a Route53 zone
for the devevelopment stack (typically dev.bioarena.io).
After the zone is created, name servers must be added to the 
parent zone (typically bioarena.io in the SageIT account).
This enables easier management of DNS records for development
resource (namely, creating endpoints for development stacks).

Note: the zone has been created, the records added. There should
be nothing more to do with this, except creating records for 
developer stacks in the zone.