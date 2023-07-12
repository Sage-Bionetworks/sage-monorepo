# OpenChallenges Config Server

## Configuration

Update the file `.env` with the required information.

### Git Backend

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

### Authentication with SSH

Generate the SSH key for connecting to the EC2 instance that we will create as part of the stack.

```console
ssh-keygen -t ed25519 -C "your_email@example.com" -f ~/.ssh/openchallenges-config-server -N ''
```

Add the public key generated to the "Deploy keys" of the GitHub repo that hosts the config.

Fetch the host key for GitHub:

```console
$ ssh-keyscan -t ed25519 github.com
# github.com:22 SSH-2.0-babeld-dc5ec9be
github.com ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIOMqqnkVzrm0SdG6UOoqKLsabgH5C9okWi0dh2l9GKJl
```

### Data source: Vault

TBA

## Get config from the Config Server

Build and start the config server (development server):

```console
nx build openchallenges-config-server
nx serve openchallenges-config-server
```

Try to fetch the config of the organization service:

```console
$ curl -s --user "openchallenges:changeme" \
  http://openchallenges-config-server:8090/openchallenges-organization-service/development/test | jq
{
  "name": "openchallenges-organization-service",
  "profiles": [
    "dev"
  ],
  "label": "test",
  "version": null,
  "state": null,
  "propertySources": [
    {
      "name": "https://github.com/Sage-Bionetworks/openchallenges-config-server-repository.git/openchallenges-organization-service-dev.yml",
      "source": {
        "openchallenges-organization-service.welcome-message": "Welcome to the Organization service."
      }
    }
  ]
}
```