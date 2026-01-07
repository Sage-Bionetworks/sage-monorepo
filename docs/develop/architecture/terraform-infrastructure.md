# Terraform Infrastructure Architecture

## Overview

The Sage Monorepo uses **OpenTofu** (with **Terragrunt**) to manage cloud infrastructure as code, with a focus on reusability, consistency, and best practices. This architecture enables teams to deploy cloud resources efficiently while maintaining security and compliance standards.

### Understanding the Tools

Before diving in, it's important to understand the relationship between these tools:

**Terraform** is the original open-source Infrastructure as Code (IaC) tool created by HashiCorp. It uses HCL (HashiCorp Configuration Language) to define and provision infrastructure.

**OpenTofu** is an open-source fork of Terraform, created after HashiCorp changed Terraform's license in 2023. OpenTofu is:

- Fully compatible with Terraform syntax and modules
- Binary-compatible (drop-in replacement for the `terraform` command)
- Maintained by the Linux Foundation
- What this monorepo actually uses (version 1.10.6)

**Terragrunt** is a thin wrapper around Terraform/OpenTofu that adds features:

- DRY (Don't Repeat Yourself) configuration
- Layered configuration inheritance
- Automatic backend initialization
- Dependency management between modules

**Why OpenTofu + Terragrunt?**

- **Infrastructure as Code (IaC)**: Version control your infrastructure alongside application code
- **Reusable Modules**: Create once, use everywhere approach for common infrastructure patterns
- **DRY Principle**: Terragrunt eliminates configuration duplication across environments
- **State Management**: Centralized, secure state storage with locking
- **Multi-Environment**: Deploy identical infrastructure to dev, staging, and production
- **Open Source**: OpenTofu ensures the tooling remains community-driven and free

### Key Technologies

| Technology   | Version | Purpose                                            |
| ------------ | ------- | -------------------------------------------------- |
| OpenTofu     | 1.10.6  | Infrastructure provisioning (Terraform-compatible) |
| Terragrunt   | 0.87.5  | DRY configuration wrapper for OpenTofu/Terraform   |
| AWS Provider | 5.88.0  | AWS resource management                            |

!!! note "Terminology in This Documentation"
While we use OpenTofu, the directory structure and common terminology still refer to "Terraform projects" (e.g., `apps/bixarena/infra/terraform/`). In this documentation:

    - **Terraform project** = An Nx project that manages infrastructure using OpenTofu/Terragrunt
    - **Module** = A logical infrastructure component (VPC, database, etc.)
    - **Terraform** and **OpenTofu** are used interchangeably (they're compatible)
    - Commands shown use `terragrunt` which wraps the `tofu` binary

## Architecture Principles

### 1. Layered Configuration Model

Infrastructure configuration follows a three-layer hierarchy to maximize reusability and minimize duplication:

```
workspace.hcl (root)              # Organization-wide defaults
    ↓
project.hcl (per Nx project)      # Project-specific settings
    ↓
terragrunt.hcl (per module)       # Module-specific configuration
```

#### Workspace Layer (`workspace.hcl`)

Located at the repository root, defines global constants:

```hcl
locals {
  workspace_root = get_repo_root()
  organization   = "sage"

  project_paths = {
    terraform = "${local.workspace_root}/libs/platform/infra/terraform"
  }
}

inputs = {
  organization  = local.organization
  project_paths = local.project_paths
}
```

**Responsibilities:**

- Organization name and branding
- Shared module registry paths
- Global tagging schema
- Default conventions

#### Project Layer (`project.hcl`)

Each Nx infrastructure project has a `project.hcl`:

```hcl
locals {
  workspace_vars = read_terragrunt_config(find_in_parent_folders("workspace.hcl"))
  _config_yaml   = yamldecode(file("config.yaml"))

  project_vars = {
    product     = get_env("PRODUCT", local._config_yaml.product)
    application = get_env("APPLICATION", local._config_yaml.application)
    environment = get_env("ENVIRONMENT", local._config_yaml.environment)
  }
}
```

**Responsibilities:**

- Project name and metadata
- Environment definitions
- Remote state backend configuration
- Provider settings
- Load project-specific `config.yaml`

#### Module Layer (`<module>/terragrunt.hcl`)

Each logical component (VPC, database, load balancer) has its own module:

```hcl
include "project" {
  path = find_in_parent_folders("project.hcl")
}

terraform {
  source = "${include.project.inputs.project_paths.terraform}//modules/terraform-s3-backend"
}

inputs = {
  region     = "us-east-1"
  component  = "backend"
  # Module-specific configuration
}
```

### 2. Configuration Precedence

Values cascade with clear precedence (highest to lowest):

1. **Environment Variables** - Runtime overrides (e.g., `TERRAFORM_BACKEND_BUCKET_NAME`)
2. **config.yaml** - Project-specific values
3. **Default Values** - Module variable defaults

This allows flexibility for CI/CD pipelines, local development, and different environments.

### 3. Reusable Module Pattern

Modules are organized into two categories:

#### Shared Modules (`libs/platform/infra/terraform/modules/`)

Generic, reusable infrastructure components:

```
libs/platform/infra/terraform/modules/
├── resource-label/          # Consistent naming and tagging
├── terraform-s3-backend/    # Terraform state backend
├── vpc/                     # Network infrastructure (planned)
├── ecs-cluster/             # Container orchestration (planned)
└── rds-postgres/            # Database instances (planned)
```

**Characteristics:**

- Technology/product agnostic
- Parameterized inputs with sensible defaults
- Comprehensive validation
- Well-documented outputs
- Security best practices built-in

#### Project Modules (`apps/<product>/infra/terraform/<project>/modules/`)

Product-specific infrastructure (planned):

```
apps/bixarena/infra/terraform/stack/modules/
├── api-service/             # BixArena-specific API configuration
├── worker-pool/             # Custom worker setup
└── cdn-distribution/        # Content delivery
```

**Characteristics:**

- Tailored to specific product requirements
- May combine multiple shared modules
- Business logic specific to the product

### 4. Resource Naming Convention

All resources follow a consistent naming pattern for discoverability and IAM scoping:

```
${organization}-${product}-${application}-${component}-${region}-${environment}
```

**Example:** `sage-bixarena-api-alb-use1-prod`

Components:

- **organization**: `sage` (from workspace.hcl)
- **product**: `bixarena` (from config.yaml or env var)
- **application**: `api` (from config.yaml)
- **component**: `alb` (from module input)
- **region**: `use1` (shortened from us-east-1)
- **environment**: `prod` (from config.yaml or env var)

The [resource-label](../../../../libs/platform/infra/terraform/modules/resource-label) module handles this naming automatically, including:

- Length truncation (S3 63-char limit, etc.)
- Region abbreviation
- Character filtering (alphanumeric + hyphens only)

### 5. Tagging Strategy

Every resource receives standard tags:

```hcl
tags = {
  Organization = "Sage Bionetworks"
  Product      = "BixArena"
  Application  = "API"
  Component    = "LoadBalancer"
  Environment  = "prod"
  ManagedBy    = "terraform"
  CostCenter   = "Research"
  Owner        = "platform-team@sagebase.org"
}
```

These tags enable:

- Cost allocation and tracking
- Resource filtering and queries
- Compliance auditing
- Ownership identification

### 6. Bootstrap Exception Pattern

The Terraform backend module has a special requirement: it **creates** the S3 bucket and DynamoDB table used for remote state, so it cannot use remote state itself.

**Solution:** Bootstrap modules use a local backend initially:

```hcl
# terraform-backend/terragrunt.hcl
remote_state {
  backend = "local"
  config = {
    path = "${get_terragrunt_dir()}/terraform.tfstate"
  }
}
```

After deployment, other modules reference the remote backend via `project.hcl`:

```hcl
# project.hcl (for all other modules)
remote_state {
  backend = "s3"
  config = {
    bucket         = "sage-bixarena-terraform-backend-use1-prod"
    region         = "us-east-1"
    dynamodb_table = "sage-bixarena-terraform-backend-lock-use1-prod"
  }
}
```

## Project Structure

### Typical Nx Infrastructure Project Layout

```
apps/bixarena/infra/terraform/terraform-backend/
├── project.json                      # Nx project configuration
├── project.hcl                       # Project-level Terragrunt configuration
├── config.yaml                       # Environment/module settings
├── README.md                         # Project documentation
├── .env.example                      # Environment variable template
└── terraform-backend/                # Module directory
    └── terragrunt.hcl                # Module configuration

libs/platform/infra/terraform/modules/
├── resource-label/                   # Shared: naming convention
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   ├── versions.tf
│   └── README.md
└── terraform-s3-backend/             # Shared: state backend
    ├── main.tf
    ├── variables.tf
    ├── outputs.tf
    ├── policies.tf
    ├── context.tf                    # Embeds resource-label
    ├── versions.tf
    └── README.md
```

### config.yaml Structure

```yaml
# Product/Environment identification
product: bixarena
application: infra
environment: prod

# Terraform backend (output from bootstrap)
terraform_backend:
  bucket_name: 'sage-bixarena-terraform-backend-use1-prod'
  bucket_region: 'us-east-1'
  dynamodb_table: 'sage-bixarena-terraform-backend-lock-use1-prod'

# Module-specific configuration
modules:
  terraform_backend:
    aws_provider:
      region: 'us-east-1'

  vpc:
    cidr_block: '10.0.0.0/16'
    availability_zones: ['us-east-1a', 'us-east-1b', 'us-east-1c']

  database:
    instance_class: 'db.t3.medium'
    allocated_storage: 100
```

## Module Dependencies

Use Terragrunt `dependency` blocks to reference outputs from other modules:

```hcl
# apps/bixarena/infra/stack/database/terragrunt.hcl
dependency "vpc" {
  config_path = "../network"
}

inputs = {
  vpc_id             = dependency.vpc.outputs.vpc_id
  subnet_ids         = dependency.vpc.outputs.private_subnet_ids
  security_group_ids = [dependency.vpc.outputs.database_security_group_id]
}
```

**Dependency Rules:**

- Only depend on foundational/lower-layer modules
- Avoid circular dependencies
- Keep dependency chains shallow (max 3 levels)
- Use explicit outputs, not entire objects

## State Management

### State File Organization

Each module has its own isolated state file:

```
s3://sage-bixarena-terraform-backend-use1-prod/
├── terraform-backend/terraform.tfstate         # Bootstrap (local)
├── network/terraform.tfstate                   # VPC, subnets, security groups
├── database/terraform.tfstate                  # RDS instances
├── compute/terraform.tfstate                   # ECS/EC2 resources
└── monitoring/terraform.tfstate                # CloudWatch, alarms
```

**Benefits:**

- Isolated blast radius (changes don't affect other modules)
- Parallel development (different teams work on different modules)
- Faster planning (smaller state graphs)
- Easier troubleshooting

### State Locking

DynamoDB provides distributed locking to prevent concurrent modifications:

```
+----------------+     Lock Request      +------------------+
| Developer A    | ------------------->  | DynamoDB Table   |
| (terraform)    | <------------------- |  (LockID: key)   |
+----------------+   Lock Acquired       +------------------+
                                                 |
                                                 | Lock Denied
                                                 v
                                         +----------------+
                                         | Developer B    |
                                         | (waiting...)   |
                                         +----------------+
```

## Security Best Practices

### 1. Encryption

- **S3 State Storage**: SSE-KMS encryption enforced
- **DynamoDB Tables**: Server-side encryption enabled
- **TLS**: All API calls require HTTPS

### 2. Access Control

- **S3 Bucket Policies**: Enforce encryption headers, deny unencrypted uploads
- **IAM Roles**: Least-privilege access for CI/CD pipelines
- **State File**: Contains sensitive data; never commit to version control

### 3. Version Pinning

```hcl
terraform {
  required_version = ">= 1.8.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "= 5.88.0"  # Exact version for reproducibility
    }
  }
}
```

### 4. Validation

```hcl
variable "s3_bucket_name" {
  type = string

  validation {
    condition     = length(var.s3_bucket_name) < 64
    error_message = "S3 bucket name must be fewer than 64 characters."
  }
}
```

## CI/CD Integration

### GitHub Actions Workflow

```yaml
- name: Terraform Plan
  run: |
    aws sso login --profile ${{ env.AWS_PROFILE }}
    nx run ${{ env.PROJECT }}:plan:${{ env.ENVIRONMENT }}

- name: Terraform Apply
  if: github.ref == 'refs/heads/main'
  run: |
    nx run ${{ env.PROJECT }}:deploy:${{ env.ENVIRONMENT }}
```

### Nx Project Configuration

```json
{
  "targets": {
    "init": {
      "executor": "nx:run-commands",
      "options": {
        "command": "terragrunt init --working-dir <module>",
        "cwd": "{projectRoot}"
      }
    },
    "plan": {
      "executor": "nx:run-commands",
      "options": {
        "command": "terragrunt plan --working-dir <module>",
        "cwd": "{projectRoot}"
      }
    },
    "deploy": {
      "executor": "nx:run-commands",
      "options": {
        "command": "terragrunt apply --working-dir <module>",
        "cwd": "{projectRoot}"
      }
    }
  }
}
```

## Migration from CDK

For teams migrating from AWS CDK, key differences:

| Aspect        | CDK               | OpenTofu + Terragrunt |
| ------------- | ----------------- | --------------------- |
| Language      | TypeScript/Python | HCL                   |
| State         | CloudFormation    | S3 + DynamoDB         |
| Modularity    | Constructs        | Modules               |
| Configuration | Code              | HCL + YAML            |
| Reusability   | Npm packages      | Local/remote modules  |

**Migration Strategy:**

1. Deploy Terraform backend (bootstrap)
2. Create shared modules for common patterns
3. Migrate one stack at a time
4. Import existing resources where possible
5. Run CDK and OpenTofu/Terragrunt in parallel during transition

## Examples

### Example 1: Terraform Backend Project

The [terraform-backend](../../../apps/bixarena/infra/terraform/terraform-backend) project demonstrates:

- Bootstrap exception pattern (local state)
- Reusable module usage
- Secure S3 and DynamoDB configuration
- Output values for downstream projects

### Example 2: Reusable Module (terraform-s3-backend)

The [terraform-s3-backend](../../../libs/platform/infra/terraform/modules/terraform-s3-backend) module shows:

- Context pattern (resource-label integration)
- Conditional resource creation
- Security policies (encryption, TLS)
- Comprehensive outputs

## Common Patterns

### Pattern 1: Data Sources for Existing Resources

```hcl
data "aws_vpc" "existing" {
  filter {
    name   = "tag:Name"
    values = ["sage-bixarena-vpc-prod"]
  }
}

inputs = {
  vpc_id = data.aws_vpc.existing.id
}
```

### Pattern 2: Conditional Resource Creation

```hcl
resource "aws_s3_bucket" "optional" {
  count = var.create_bucket ? 1 : 0

  bucket = var.bucket_name
}
```

### Pattern 3: Dynamic Blocks

```hcl
dynamic "cors_rule" {
  for_each = var.cors_rules

  content {
    allowed_headers = cors_rule.value.allowed_headers
    allowed_methods = cors_rule.value.allowed_methods
    allowed_origins = cors_rule.value.allowed_origins
  }
}
```

## Troubleshooting

### Common Issues

**Issue**: `Error: Backend configuration changed`
**Solution**: Run `terragrunt init -reconfigure`

**Issue**: `Error acquiring the state lock`
**Solution**: Another process is running. Wait or manually release lock in DynamoDB.

**Issue**: `Error: Invalid provider configuration`
**Solution**: Ensure AWS SSO session is active: `aws sso login --profile <profile>`

**Issue**: Module source not found
**Solution**: Verify `project_paths` in workspace.hcl points to correct location

## Related Documentation

- [OpenTofu Documentation](https://opentofu.org/docs/)
- [Terragrunt Documentation](https://terragrunt.gruntwork.io/)
- [AWS Provider Reference](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)

## Example Projects in the Monorepo

- **Terraform Backend**: `apps/bixarena/infra/terraform/terraform-backend/` - Example of bootstrap pattern
- **Copilot Instructions**: `.github/instructions/terraform.instructions.md` - Terraform conventions for AI assistants

## Next Steps

Ready to start working with infrastructure as code? Check out our tutorials:

- [Create a Terraform Backend](../tutorials/terraform/create-backend.md) - Bootstrap your first Terraform project
- [Create a Reusable Module](../tutorials/terraform/create-reusable-module.md) - Build shared infrastructure modules
- Deploy Infrastructure (coming soon) - Deploy complete stacks to AWS
