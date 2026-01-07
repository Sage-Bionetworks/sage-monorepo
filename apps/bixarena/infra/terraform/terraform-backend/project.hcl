locals {
  workspace_vars = read_terragrunt_config(find_in_parent_folders("workspace.hcl"))

  # Default configuration.
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

  # Load optional config.yaml. If not found or invalid YAML, fall back to empty object.
  _config_yaml = try(
    yamldecode(
      file(try(find_in_parent_folders("config.yaml"), ""))
    ),
    {}
  )
  _merged_config = merge(local._default_config, local._config_yaml)

  # Backend locals (env > file > base) used by remote_state and exposed in project_vars
  # Project vars exposed for module terragrunt files (direct inline env overrides, env > file > base)
  # -----------------------------------------------------------------------------
  # project_vars precedence model
  # For every value we expose to module terragrunt files we follow:
  #   1. Environment variable (if set)            <-- highest precedence
  #   2. config.yaml value (if present / non-null)
  #   3. _default_config fallback (stable shape: empty string / [] / true)
  # Notes:
  # - We avoid nulls in defaults so get_env() always has a non-null fallback.
  # - YAML may still use explicit null (e.g., deploy_role_name: null); we guard only
  #   where Terragrunt requires a non-null default (deploy_role_name).
  # - Lists: a comma-separated env var overrides YAML list completely; empty / unset
  #   env var leaves YAML list (or default empty list) intact.
  # - Module terragrunt.hcl files consume these values via include.project.locals.project_vars
  #   AND also inherit the merged 'inputs' block defined below (workspace + product/application/environment).
  #   Use project_vars for structured, non-variable configuration; use inputs for Terraform variable inputs.
  # -----------------------------------------------------------------------------
  project_vars = {
    product = get_env("PRODUCT", local._merged_config.product)
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
    product     = local.project_vars.product
    application = local.project_vars.application
    environment = local.project_vars.environment
  }
)