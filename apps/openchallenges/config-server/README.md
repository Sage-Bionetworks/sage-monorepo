# OpenChallenges Config Server

## Overview

This Spring Cloud Config Server provides externalized configuration in a distributed system of
OpenChallenges.

## Configuration

The config of the config server lives in two files:

- `.env`
- `src/main/resources/application.yml`

## Config Storage Backends

This config server pulls the configuration of the OpenChallenges distributed system from two storage
backends. By order of priority:

- Git repository

## Git Storage Backend

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

This host key is currently included in the example config file `.env.example` and can be left as is.

After creating `.env` from `.env.example`, the variables shown below must be set:

- `GIT_PRIVATE_KEY`: The private SSH key previously generated. The key must be folded in one line by
  replacing line breaks by `\n`.

## Test: Fetch the config of a component

Build and start the config server (development server):

```console
nx serve openchallenges-config-server
```

Try to fetch the config of the organization service:

```console
$ curl -s --user "openchallenges:changeme" \
  http://openchallenges-config-server:8090/openchallenges-organization-service/dev/test-2 | jq
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
