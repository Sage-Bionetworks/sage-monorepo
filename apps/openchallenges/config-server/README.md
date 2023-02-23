# OpenChallenges Config Server

## Configuration

Update the file `.env` with the required information.

### Data source: GitHub repository

One of the data sources of the config server is a GitHub repository where the configuration files of
the OC services are stored. This GitHub repository should be private and not include sensitive
information like credentials, which should be stored in the project Vault instance.

```console
GIT_URI=https://github.com/Sage-Bionetworks/openchallenges-config-server-repository.git

# Name of the branch or tag
GIT_DEFAULT_LABEL=test

# The token only needs read access to the repo
GIT_USERNAME=<username>
GIT_TOKEN=<token>
```

### Data source: Vault

TBA