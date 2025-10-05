# BixArena Infra Bootstrap

## Description

This project bootstrap the AWS resources needed to deploy BixArena with Terraform.

## Login with AWS SSO

```bash
aws sso login --profile bixarena-Administrator
```

## Deploy the Terraform Backend

Ensure the `modules.terraform_backend` section is configured in `config.yaml`.

### Initialize the backend module

```bash
nx run bixarena-infra-bootstrap:init --configuration=terraform-backend
```

## Validate the Terraform syntax and config

```bash
nx run bixarena-infra-bootstrap:validate
```

### Review the deployment plan

```bash
nx run bixarena-infra-bootstrap:plan --configuration=terraform-backend
```

### Deploy the backend resources

```bash
nx run bixarena-infra-bootstrap:apply --configuration=terraform-backend
```

## Deploy the modules

```bash
nx run bixarena-infra-bootstrap:init
```

```bash
nx run bixarena-infra-bootstrap:plan
```
