# BixArena Terraform Backend

## Description

This project bootstrap the AWS resources needed to deploy BixArena Terraform backend used to store
the Terraform states of other Terraform projects.

## Login with AWS SSO

```bash
aws sso login --profile bixarena-dev-Developer
```

## Deploy the Terraform Backend

Ensure the `modules.terraform_backend` section is configured in `config.yaml`. This information can
be override using environment variables shown below.

- `TERRAFORM_BACKEND_BUCKET_NAME`
- `TERRAFORM_BACKEND_BUCKET_REGION`
- `TERRAFORM_BACKEND_DYNAMODB_TABLE`

### Initialize the backend module

```bash
nx run bixarena-infra-terraform-backend:init
```

## Validate the Terraform syntax and config

```bash
nx run bixarena-infra-terraform-bootstrap:validate-backend:dev
```

### Review the deployment plan

```bash
nx run bixarena-infra-terraform-bootstrap:plan-backend:dev
```

### Deploy the backend resources

```bash
nx run bixarena-infra-terraform-bootstrap:apply-backend:dev
```

## Deploy the modules

```bash
nx run bixarena-infra-terraform-bootstrap:init
```

```bash
nx run bixarena-infra-terraform-bootstrap:plan
```
