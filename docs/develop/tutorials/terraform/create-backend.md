# Tutorial: Create a Terraform Backend

## Overview

This tutorial guides you through creating a **Terraform backend infrastructure project** using the monorepo's reusable module pattern. You'll deploy an S3 bucket and DynamoDB table to AWS that will store Terraform state for future infrastructure projects.

!!! info "What is a Terraform Project?"
In the Sage Monorepo, a **Terraform project** is an Nx project (located in `apps/<product>/infra/terraform/` or `libs/platform/infra/terraform/`) that uses OpenTofu and Terragrunt to manage cloud infrastructure. Each project can contain multiple modules (logical infrastructure components).

**What you'll build:**

- S3 bucket with versioning and encryption for state storage
- DynamoDB table for state locking
- Secure bucket policies and access controls
- A Terraform project configured as an Nx project

**Time required:** 30-45 minutes

**Prerequisites:**

- AWS CLI installed and configured
- AWS SSO profile set up
- OpenTofu 1.10.6+ (installed in dev container)
- Terragrunt 0.87.5+ (installed in dev container)
- Basic understanding of Infrastructure as Code concepts

## Step 1: Understand the Architecture

Before we start, review the [Terraform Infrastructure Architecture](../../architecture/terraform-infrastructure.md) to understand:

- The layered configuration model (workspace → project → module)
- The bootstrap exception pattern (why this project uses local state)
- How reusable modules work

**Key Concept:** The backend creates the infrastructure that stores state for other projects, so it uses a **local backend** initially. This is intentional and correct.

## Step 2: Create the Nx Project Structure

Create the project directory structure:

```bash
# Navigate to your product's infrastructure directory
cd apps/<product>/infra/terraform/

# Create the project directory
mkdir -p terraform-backend/terraform-backend
cd terraform-backend
```

Your structure should look like:

```
apps/<product>/infra/terraform/terraform-backend/
├── project.json           # Nx project configuration (we'll create this)
├── project.hcl            # Project-level Terragrunt configuration
├── config.yaml            # Project settings
├── README.md              # Documentation
├── .env.example           # Environment variable template
└── terraform-backend/     # Module directory
    └── terragrunt.hcl     # Module configuration
```

## Step 3: Create the Nx Project Configuration

Create `project.json` to integrate with Nx:

```json
{
  "name": "<product>-infra-terraform-terraform-backend",
  "$schema": "../../../../node_modules/nx/schemas/project-schema.json",
  "projectType": "application",
  "tags": ["language:terraform", "product:<product>", "type:infra"],
  "targets": {
    "init": {
      "executor": "nx:run-commands",
      "options": {
        "command": "terragrunt init --working-dir terraform-backend",
        "cwd": "{projectRoot}"
      }
    },
    "validate": {
      "executor": "nx:run-commands",
      "options": {
        "command": "terragrunt validate --working-dir terraform-backend",
        "cwd": "{projectRoot}"
      }
    },
    "plan": {
      "executor": "nx:run-commands",
      "options": {
        "command": "terragrunt plan --working-dir terraform-backend",
        "cwd": "{projectRoot}"
      }
    },
    "deploy": {
      "executor": "nx:run-commands",
      "options": {
        "command": "terragrunt apply --working-dir terraform-backend",
        "cwd": "{projectRoot}"
      }
    },
    "destroy": {
      "executor": "nx:run-commands",
      "options": {
        "command": "terragrunt destroy --working-dir terraform-backend",
        "cwd": "{projectRoot}"
      }
    }
  }
}
```

**Replace** `<product>` with your product name (e.g., `bixarena`, `openchallenges`).

## Step 4: Create the Project Configuration

Create `project.hcl` to define project-level settings:

```hcl
locals {
  workspace_vars = read_terragrunt_config(find_in_parent_folders("workspace.hcl"))

  # Default configuration structure
  _default_config = {
    product     = ""
    application = ""
    environment = ""
    terraform_backend = {
      bucket_name    = ""
      bucket_region  = ""
      dynamodb_table = ""
    }
    modules = {
      terraform_backend = {
        aws_provider = {
          region = ""
        }
      }
    }
  }

  # Load config.yaml and merge with defaults
  _config_yaml = try(
    yamldecode(file(try(find_in_parent_folders("config.yaml"), ""))),
    {}
  )
  _merged_config = merge(local._default_config, local._config_yaml)

  # Project vars with environment variable overrides
  project_vars = {
    product     = get_env("PRODUCT", local._merged_config.product)
    application = get_env("APPLICATION", local._merged_config.application)
    environment = get_env("ENVIRONMENT", local._merged_config.environment)

    terraform_backend = {
      bucket_name    = get_env("TERRAFORM_BACKEND_BUCKET_NAME", local._merged_config.terraform_backend.bucket_name)
      bucket_region  = get_env("TERRAFORM_BACKEND_BUCKET_REGION", local._merged_config.terraform_backend.bucket_region)
      dynamodb_table = get_env("TERRAFORM_BACKEND_DYNAMODB_TABLE", local._merged_config.terraform_backend.dynamodb_table)
    }

    modules = {
      terraform_backend = {
        aws_provider = {
          region = get_env(
            "MODULES_TERRAFORM_BACKEND_AWS_PROVIDER_REGION",
            try(local._merged_config.modules.terraform_backend.aws_provider.region, "")
          )
        }
      }
    }
  }
}

# Remote state configuration for OTHER modules (not this bootstrap module)
remote_state {
  backend = "s3"
  config = {
    bucket         = local.project_vars.terraform_backend.bucket_name
    key            = "${path_relative_to_include()}/terraform.tfstate"
    region         = local.project_vars.terraform_backend.bucket_region
    encrypt        = true
    dynamodb_table = local.project_vars.terraform_backend.dynamodb_table
  }
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
}

# Inputs inherited by all modules
inputs = merge(
  local.workspace_vars.inputs,
  {
    product     = local.project_vars.product
    application = local.project_vars.application
    environment = local.project_vars.environment
  }
)
```

## Step 5: Create the Configuration File

Create `config.yaml` with your project-specific settings:

```yaml
# Product identification
product: <product>
application: infra
environment: prod

# Terraform backend configuration
# These values will be populated after the first deployment
terraform_backend:
  bucket_name: 'sage-<product>-terraform-backend-use1-prod'
  bucket_region: 'us-east-1'
  dynamodb_table: 'sage-<product>-terraform-backend-lock-use1-prod'

# Module-specific configuration
modules:
  terraform_backend:
    aws_provider:
      region: 'us-east-1'
```

**Replace** `<product>` with your product name.

## Step 6: Create the Module Configuration

Create `terraform-backend/terragrunt.hcl`:

```hcl
include "project" {
  path   = find_in_parent_folders("project.hcl")
  expose = true
}

locals {
  module_vars = include.project.locals.project_vars.modules.terraform_backend
}

terraform {
  # Reference the shared reusable module
  source = "${include.project.inputs.project_paths.terraform}//modules/terraform-s3-backend"
}

inputs = {
  component = ""  # Empty for backend (not a sub-component)
  region    = local.module_vars.aws_provider.region

  # Module configuration
  enabled                           = true
  bucket_ownership_enforced_enabled = false
  force_destroy                     = false  # Set to true only for testing
  sse_encryption                    = "aws:kms"

  # Optional: Override auto-generated names
  # s3_bucket_name     = "my-custom-bucket-name"
  # dynamodb_table_name = "my-custom-table-name"
}

# IMPORTANT: Bootstrap exception - use local backend
remote_state {
  backend = "local"
  config = {
    path = "${get_terragrunt_dir()}/terraform.tfstate"
  }
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite"
  }
}

# Generate AWS provider configuration
generate "provider" {
  path      = "provider.tf"
  if_exists = "overwrite"
  contents  = <<EOF
provider "aws" {
  region = "${local.module_vars.aws_provider.region}"
}
EOF
}
```

## Step 7: Create Documentation

Create `README.md` documenting your project. You can reference the BixArena example at `apps/bixarena/infra/terraform/terraform-backend/README.md` in the monorepo for a complete template.

Key sections to include:

- Overview of what the project deploys
- Prerequisites
- Available Nx commands
- State file management notes
- Troubleshooting tips

## Step 8: Initialize the Project

Authenticate with AWS and initialize the Terraform project:

```bash
# Login to AWS SSO
aws sso login --profile <product>-<env>-Developer

# Initialize Terraform
nx run <product>-infra-terraform-terraform-backend:init
```

This will:

- Download the AWS provider
- Initialize the local backend
- Prepare the working directory

Terragrunt wraps OpenTofu (the `tofu` binary), so you'll see OpenTofu output.

**Expected output:**

```
OpenTofu has been successfully initialized!
```

## Step 9: Validate Configuration

Check your configuration for syntax errors:

```bash
nx run <product>-infra-terraform-terraform-backend:validate
```

**Expected output:**

```
Success! The configuration is valid.
```

## Step 10: Review the Plan

Generate an execution plan to see what will be created:

```bash
nx run <product>-infra-terraform-terraform-backend:plan
```

**Review the plan carefully.** You should see:

- 1 S3 bucket with versioning and encryption
- Multiple S3 bucket configuration resources (ACL, policy, etc.)
- 1 DynamoDB table with point-in-time recovery

Example output:

```
Plan: 8 to add, 0 to change, 0 to destroy.

Changes to Outputs:
  + dynamodb_table_arn  = (known after apply)
  + dynamodb_table_name = (known after apply)
  + s3_bucket_arn      = (known after apply)
  + s3_bucket_id       = (known after apply)
```

## Step 11: Deploy the Backend

Apply the changes to create resources in AWS:

```bash
nx run <product>-infra-terraform-terraform-backend:deploy
```

Type `yes` when prompted to confirm.

**Deployment time:** ~30-60 seconds

## Step 12: Capture Outputs

After successful deployment, OpenTofu displays output values:

```
Outputs:

dynamodb_table_arn = "arn:aws:dynamodb:us-east-1:123456789012:table/sage-product-terraform-backend-lock-use1-prod"
dynamodb_table_name = "sage-product-terraform-backend-lock-use1-prod"
s3_bucket_arn = "arn:aws:s3:::sage-product-terraform-backend-use1-prod"
s3_bucket_id = "sage-product-terraform-backend-use1-prod"
s3_bucket_region = "us-east-1"
```

**Important:** Update your `config.yaml` with these values so other projects can reference this backend.

## Step 13: Verify in AWS Console

1. Navigate to AWS Console → S3
2. Find your bucket: `sage-<product>-terraform-backend-use1-prod`
3. Verify:
   - ✅ Versioning is enabled
   - ✅ Default encryption is enabled (SSE-KMS)
   - ✅ Public access is blocked
4. Navigate to DynamoDB
5. Find your table: `sage-<product>-terraform-backend-lock-use1-prod`
6. Verify:
   - ✅ Table exists with `LockID` hash key
   - ✅ Point-in-time recovery is enabled

## Step 14: Secure the State File

The local state file is located at:

```
apps/<product>/infra/terraform/terraform-backend/terraform-backend/terraform.tfstate
```

**Critical Security Steps:**

1. ✅ Verify it's excluded from Git (already in `.gitignore`)
2. 📦 Back it up to a secure location:
   - 1Password vault
   - AWS Secrets Manager
   - Secure cloud storage
3. 🔒 Restrict access (only infrastructure team)

**Why it matters:** If this file is lost, you'll need to manually import resources or redeploy.

## Step 15: Test with a Dependent Module (Optional)

Create a test module to verify the backend works:

```bash
# Create a test module
mkdir -p ../test-module
cd ../test-module

# Create terragrunt.hcl that uses the remote backend
cat > terragrunt.hcl <<'EOF'
include "project" {
  path = find_in_parent_folders("project.hcl")
}

terraform {
  source = "tfr:///terraform-aws-modules/s3-bucket/aws?version=3.15.0"
}

inputs = {
  bucket = "test-remote-state-${get_env("USER", "demo")}"
  tags = {
    Test = "true"
  }
}
EOF

# Initialize - should use the remote S3 backend
terragrunt init

# Check the backend configuration
cat .terragrunt-cache/.../backend.tf
```

You should see S3 backend configuration pointing to your new bucket.

## Troubleshooting

### Error: "backend configuration has changed"

**Cause:** Backend settings were modified.

**Solution:**

```bash
nx run <product>-infra-terraform-terraform-backend:init
# Answer 'yes' to reconfigure
```

### Error: "Failed to get existing workspaces"

**Cause:** AWS credentials expired or invalid.

**Solution:**

```bash
aws sso login --profile <product>-<env>-Developer
```

### Error: "AccessDenied: Access Denied"

**Cause:** IAM permissions insufficient.

**Solution:** Ensure your AWS profile has permissions for:

- `s3:CreateBucket`, `s3:PutBucketPolicy`, `s3:PutEncryptionConfiguration`
- `dynamodb:CreateTable`, `dynamodb:UpdateTable`

### Resources Already Exist

**Cause:** Resources were created outside of this Terraform project or in a previous run.

**Solution:** Import existing resources:

```bash
cd terraform-backend
terragrunt import aws_s3_bucket.default <bucket-name>
terragrunt import aws_dynamodb_table.with_server_side_encryption <table-name>
```

## Next Steps

Now that you have a Terraform backend:

1. **Create additional infrastructure modules** that use this remote backend
2. **Learn to create reusable modules** - See [Create a Reusable Module](create-reusable-module.md)
3. **Set up CI/CD** for automated deployments
4. **Add monitoring** with CloudWatch alarms for state access

## Best Practices

- ✅ **One backend per product** - Don't share backends across products
- ✅ **Separate environments** - Create separate backends for dev/staging/prod
- ✅ **Version control** - Always commit your Terraform project code, never the state files
- ✅ **Review plans** - Never run `apply` without reviewing the plan first
- ✅ **Use workspaces carefully** - For multi-environment, prefer separate backends

## Related Resources

- [Terraform Infrastructure Architecture](../../architecture/terraform-infrastructure.md)
- [OpenTofu S3 Backend Docs](https://opentofu.org/docs/language/settings/backends/s3/)
- [Terraform S3 Backend Docs](https://www.terraform.io/docs/language/settings/backends/s3.html) (compatible)

## Example Code in the Monorepo

- **Reusable Module**: `libs/platform/infra/terraform/modules/terraform-s3-backend/`
- **Example Project**: `apps/bixarena/infra/terraform/terraform-backend/`
