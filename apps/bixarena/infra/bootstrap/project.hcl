locals {
  workspace_vars = read_terragrunt_config(find_in_parent_folders("workspace.hcl"))

  # Default configuration.
  _default_config = {
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
      github_oidc_provider = {
        repository            = ""
        allowed_subs          = []
        existing_provider_arn = ""
        managed_policy_arns   = []
        deploy_role_name      = ""
        create_deploy_role    = true
      }
    }
  }

  _config_file   = find_in_parent_folders("config.yaml")
  # Assume config.yaml exists (may be empty); guard decode for robustness.
  _config_raw    = try(file(local._config_file), "")
  _merged_config = merge(local._default_config, try(yamldecode(local._config_raw), {}))

  # Static context
  product     = "bixarena"
  application = "bootstrap"
  environment = "prod"

  # Backend locals (env > file > base) used by remote_state and exposed in project_vars
  # Project vars exposed for module terragrunt files (direct inline env overrides, env > file > base)
  project_vars = {
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
            local._merged_config.modules.terraform_backend.aws_provider.region == null ? "" : local._merged_config.modules.terraform_backend.aws_provider.region
          )
        }
      }
      github_oidc_provider = {
        repository = get_env(
          "MODULES_GITHUB_OIDC_PROVIDER_REPOSITORY",
          local._merged_config.modules.github_oidc_provider.repository == null ? "" : local._merged_config.modules.github_oidc_provider.repository
        )
        existing_provider_arn = get_env(
          "MODULES_GITHUB_OIDC_PROVIDER_EXISTING_PROVIDER_ARN",
            local._merged_config.modules.github_oidc_provider.existing_provider_arn == null ? "" : local._merged_config.modules.github_oidc_provider.existing_provider_arn
        )
        allowed_subs = (
          length(trimspace(get_env("MODULES_GITHUB_OIDC_PROVIDER_ALLOWED_SUBS", ""))) > 0 ? [
            for s in split(",", get_env("MODULES_GITHUB_OIDC_PROVIDER_ALLOWED_SUBS", "")) : trimspace(s) if trimspace(s) != ""
          ] : (local._merged_config.modules.github_oidc_provider.allowed_subs == null ? [] : local._merged_config.modules.github_oidc_provider.allowed_subs)
        )
        managed_policy_arns = (
          length(trimspace(get_env("MODULES_GITHUB_OIDC_PROVIDER_MANAGED_POLICY_ARNS", ""))) > 0 ? [
            for s in split(",", get_env("MODULES_GITHUB_OIDC_PROVIDER_MANAGED_POLICY_ARNS", "")) : trimspace(s) if trimspace(s) != ""
          ] : (local._merged_config.modules.github_oidc_provider.managed_policy_arns == null ? [] : local._merged_config.modules.github_oidc_provider.managed_policy_arns)
        )
        deploy_role_name = get_env(
          "MODULES_GITHUB_OIDC_PROVIDER_DEPLOY_ROLE_NAME",
          local._merged_config.modules.github_oidc_provider.deploy_role_name == null ? "" : local._merged_config.modules.github_oidc_provider.deploy_role_name
        )
        create_deploy_role = (
          length(trimspace(get_env("MODULES_GITHUB_OIDC_PROVIDER_CREATE_DEPLOY_ROLE", ""))) > 0 ? try(
            tobool(lower(trimspace(get_env("MODULES_GITHUB_OIDC_PROVIDER_CREATE_DEPLOY_ROLE", "")))),
            local._merged_config.modules.github_oidc_provider.create_deploy_role
          ) : local._merged_config.modules.github_oidc_provider.create_deploy_role
        )
      }
    }
  }
}

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

inputs = merge(
  local.workspace_vars.inputs,
  {
    product     = local.product
    application = local.application
    environment = local.environment
  }
)