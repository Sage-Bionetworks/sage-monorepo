locals {
  workspace_vars = read_terragrunt_config(find_in_parent_folders("workspace.hcl"))

  # Always attempt to read config.yaml; if absent or partially defined we normalize to a stable shape so
  # downstream references never cause type inconsistencies. This eliminates the earlier conditional that
  # produced "Inconsistent conditional result types" when optional keys (e.g. allowed_subs) existed only
  # in one branch.
  _project_config_file = find_in_parent_folders("config.yaml")
  _raw_config          = fileexists(local._project_config_file) ? try(yamldecode(file(local._project_config_file)), {}) : {
    terraform_backend = {}
    modules = {
      terraform_backend    = {}
      github_oidc_provider = {
        aws_provider         = {}
        repository           = null
        existing_provider_arn = null
        allowed_subs         = []
        managed_policy_arns  = []
        deploy_role_name     = null
        create_deploy_role   = null
      }
    }
  }

  # Normalize raw config into a predictable object shape (add missing maps/keys as empty objects)
  project_vars_raw = {
    terraform_backend = try(local._raw_config.terraform_backend, {})
    modules = {
      terraform_backend    = try(local._raw_config.modules.terraform_backend, {})
      github_oidc_provider = try(local._raw_config.modules.github_oidc_provider, {})
    }
  }

  # Static context
  product     = "bixarena"
  application = "bootstrap"
  environment = "prod"

  project_vars_defaults = {
    terraform_backend = {
      bucket_name    = null
      bucket_region  = null
      dynamodb_table = null
    }
    modules = {
      terraform_backend    = {}
      github_oidc_provider = {}
    }
  }

  project_vars = merge(local.project_vars_defaults, local.project_vars_raw)

  # Environment variable overrides (prefer env; fallback to config.yaml -> defaults)
  # Updated variable names (removed BIXARENA_ prefix) to use neutral names defined in .env.example
  backend_bucket    = get_env("TERRAFORM_BACKEND_BUCKET", local.project_vars.terraform_backend.bucket_name)
  backend_region    = get_env("TERRAFORM_BACKEND_REGION", local.project_vars.terraform_backend.bucket_region)
  backend_ddb_table = get_env("TERRAFORM_BACKEND_DDB_TABLE", local.project_vars.terraform_backend.dynamodb_table)
}

remote_state {
  backend = "s3"
  config = {
    bucket         = local.backend_bucket
    key            = "${path_relative_to_include()}/terraform.tfstate"
    region         = local.backend_region
    encrypt        = true
    dynamodb_table = local.backend_ddb_table
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