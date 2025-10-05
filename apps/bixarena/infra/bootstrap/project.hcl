locals {
  workspace_vars = read_terragrunt_config(find_in_parent_folders("workspace.hcl"))

  # Base shape guarantees consistent object structure to avoid conditional type issues.
  _base_config = {
    terraform_backend = {
      bucket_name    = null
      bucket_region  = null
      dynamodb_table = null
    }
    modules = {
      terraform_backend = {
        aws_provider = {
          region = null
        }
      }
      github_oidc_provider = {
        repository            = null
        allowed_subs          = []
        existing_provider_arn = null
        managed_policy_arns   = []
        deploy_role_name      = null
        create_deploy_role    = true
      }
    }
  }

  _config_file   = find_in_parent_folders("config.yaml")
  # Assume config.yaml exists (may be empty); guard decode for robustness.
  _config_raw    = try(file(local._config_file), "")
  _merged_config = merge(local._base_config, try(yamldecode(local._config_raw), {}))

  # Static context
  product     = "bixarena"
  application = "bootstrap"
  environment = "prod"

  # Backend env overrides (env > file > base)
  terraform_backend_bucket    = get_env("TERRAFORM_BACKEND_BUCKET", local._merged_config.terraform_backend.bucket_name)
  terraform_backend_region    = get_env("TERRAFORM_BACKEND_REGION", local._merged_config.terraform_backend.bucket_region)
  terraform_backend_ddb_table = get_env("TERRAFORM_BACKEND_DDB_TABLE", local._merged_config.terraform_backend.dynamodb_table)

  # Project vars exposed for module terragrunt files (github-oidc-provider expects this path)
  project_vars = {
    modules = {
      terraform_backend = {
        aws_provider = {
          region = coalesce(
            # Prefer explicit backend region, then terraform_backend bucket region, else module-provided region
            local.terraform_backend_region,
            local._merged_config.terraform_backend.bucket_region,
            local._merged_config.modules.terraform_backend.aws_provider.region
          )
        }
      }
      github_oidc_provider = {
        repository            = local._merged_config.modules.github_oidc_provider.repository
        allowed_subs          = local._merged_config.modules.github_oidc_provider.allowed_subs
        existing_provider_arn = get_env("GITHUB_OIDC_PROVIDER_ARN", local._merged_config.modules.github_oidc_provider.existing_provider_arn)
        managed_policy_arns   = local._merged_config.modules.github_oidc_provider.managed_policy_arns
        deploy_role_name      = local._merged_config.modules.github_oidc_provider.deploy_role_name
        create_deploy_role    = local._merged_config.modules.github_oidc_provider.create_deploy_role
      }
    }
  }
}

remote_state {
  backend = "s3"
  config = {
    bucket         = local.terraform_backend_bucket
    key            = "${path_relative_to_include()}/terraform.tfstate"
    region         = local.terraform_backend_region
    encrypt        = true
    dynamodb_table = local.terraform_backend_ddb_table
  }
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
}

inputs = merge(
  local.workspace_vars.inputs,
  {
    product     = local.product
    application = local.application
    environment = local.environment
  }
)