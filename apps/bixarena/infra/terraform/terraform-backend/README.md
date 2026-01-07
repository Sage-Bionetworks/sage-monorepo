# BixArena Terraform Backend

## Overview

This Nx Terraform project provisions the foundational AWS infrastructure required for Terraform remote state management. It deploys:

- **S3 Bucket**: Stores Terraform state files with versioning, encryption (SSE-KMS), and secure access controls
- **DynamoDB Table**: Provides state locking to prevent concurrent modifications and ensure consistency
- **Security Controls**: Enforces TLS, blocks public access, and includes point-in-time recovery

This backend will be used by all other BixArena Terraform projects to store their state remotely.

**Important**: This project uses a **local backend** (`terraform.tfstate` stored locally) because it creates the remote backend infrastructure itself. After deployment, other Terraform projects will reference this backend via their `project.hcl` configuration.

> **Note on OpenTofu**: This monorepo uses OpenTofu 1.10.6, an open-source fork of Terraform that is fully compatible with Terraform syntax and modules. Commands run via `terragrunt` which wraps the `tofu` binary.

## Prerequisites

- AWS CLI configured with SSO profile
- OpenTofu 1.10.6+ and Terragrunt 0.87.5+ (installed in dev container)
- Appropriate AWS permissions to create S3 buckets, DynamoDB tables, and IAM policies

## Configuration

Backend settings are defined in [config.yaml](config.yaml):

```yaml
modules:
  terraform_backend:
    aws_provider:
      region: 'us-east-1'
```

The following environment variables can override configuration values:

- `MODULES_TERRAFORM_BACKEND_AWS_PROVIDER_REGION` - AWS region for resources

After deployment, the outputs (bucket name, DynamoDB table name, etc.) should be added to `config.yaml` under the `terraform_backend` section for use by other projects.

## Authentication

Login to AWS SSO before running any commands:

```bash
aws sso login --profile bixarena-dev-Developer
```

## Available Commands

All commands use the Nx project name: `bixarena-infra-terraform-terraform-backend`

### Initialize Terraform

Prepares the Terragrunt working directory and downloads required providers:

```bash
nx run bixarena-infra-terraform-terraform-backend:init
```

Or with environment configuration:

```bash
nx run bixarena-infra-terraform-terraform-backend:init:dev
```

### Validate Configuration

Checks Terraform syntax and configuration validity:

```bash
nx run bixarena-infra-terraform-terraform-backend:validate
```

Or for a specific environment:

```bash
nx run bixarena-infra-terraform-terraform-backend:validate:dev
```

### Review Deployment Plan

Generates an execution plan showing what resources will be created:

```bash
nx run bixarena-infra-terraform-terraform-backend:plan
```

Or for a specific environment:

```bash
nx run bixarena-infra-terraform-terraform-backend:plan:dev
```

### Deploy Backend Resources

Creates the S3 bucket and DynamoDB table in AWS:

```bash
nx run bixarena-infra-terraform-terraform-backend:deploy
```

Or for a specific environment:

```bash
nx run bixarena-infra-terraform-terraform-backend:deploy:dev
```

**Post-deployment**: After successful deployment, OpenTofu will output the backend configuration values (S3 bucket name, DynamoDB table name, region). Copy these values to the `terraform_backend` section in [config.yaml](config.yaml) so other Terraform projects can reference this backend.

### Destroy Backend Resources

**WARNING**: Only run this if you want to completely remove the backend infrastructure. Ensure no other projects are using this backend before destroying it.

```bash
nx run bixarena-infra-terraform-terraform-backend:destroy
```

Or for a specific environment:

```bash
nx run bixarena-infra-terraform-terraform-backend:destroy:dev
```

## State File Management

This project uses a **local backend**, so `terraform.tfstate` is stored at:

```
apps/bixarena/infra/terraform/terraform-backend/terraform-backend/terraform.tfstate
```

**Important**:

- This file is already excluded from version control via `.gitignore`
- Back up this file to a secure location (e.g., 1Password, AWS Secrets Manager)
- If lost, you'll need to manually import existing resources or redeploy

## Module Structure

This project demonstrates the reusable module pattern:

- **Terragrunt Module**: [terraform-backend/terragrunt.hcl](terraform-backend/terragrunt.hcl) - Project-specific configuration
- **Reusable Terraform Module**: [libs/platform/infra/terraform/modules/terraform-s3-backend](../../../../libs/platform/infra/terraform/modules/terraform-s3-backend) - Shared module
- **Resource Label Module**: [libs/platform/infra/terraform/modules/resource-label](../../../../libs/platform/infra/terraform/modules/resource-label) - Naming convention helper

## Outputs

After deployment, the module outputs:

- `s3_bucket_id` - S3 bucket name
- `s3_bucket_arn` - S3 bucket ARN
- `s3_bucket_region` - S3 bucket region
- `dynamodb_table_name` - DynamoDB lock table name
- `dynamodb_table_arn` - DynamoDB table ARN

## Troubleshooting

### Error: "backend configuration changed"

If you see this error, run `init` again to reconfigure the backend.

### Error: "Error acquiring the state lock"

Another process is running. Wait for it to complete or manually release the lock from DynamoDB if it's stuck.

## Related Documentation

- [Terraform Infrastructure Architecture](../../../../docs/develop/architecture/terraform-infrastructure.md)
- [Terraform & Terragrunt Monorepo Conventions](../../../../.github/instructions/terraform.instructions.md)
- [OpenTofu Documentation](https://opentofu.org/docs/)
- [Terragrunt Documentation](https://terragrunt.gruntwork.io/)
- [AWS S3 Backend](https://www.terraform.io/docs/language/settings/backends/s3.html)
