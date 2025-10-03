locals {
  workspace_vars = read_terragrunt_config(find_in_parent_folders("workspace.hcl"))
  project_vars   = yamldecode(file(find_in_parent_folders("config.yaml")))

  # Context variables
  product     = "bixarena"
  application = "bootstrap"
  environment = "prod"
}

remote_state {
  backend = "s3"
  config = {
    bucket         = local.project_vars.terraform_backend.bucket_name
    key            = "${path_relative_to_include()}/terraform.tfstate"
    region         = local.project_vars.terraform_backend.bucket_region
    role_arn       = local.project_vars.terraform_backend.role_arn
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