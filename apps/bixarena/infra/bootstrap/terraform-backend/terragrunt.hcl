include "project" {
  path   = find_in_parent_folders("project.hcl")
  expose = true
}

locals {
  component_vars = include.project.locals.project_vars.modules.terraform_backend
}

terraform {
  source = "${include.project.inputs.project_paths.shared_terraform}//modules/terraform-s3-backend"
}

# The inputs passed to the Terraform module include the inputs defined in the
# project Terragrunt file.
inputs = {
  component = "terraform-backend"
  region    = local.component_vars.aws_provider.region

  enabled                           = true
  bucket_ownership_enforced_enabled = false
  force_destroy                     = false
  sse_encryption                    = "aws:kms"
}

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

generate "provider" {
  path      = "provider.tf"
  if_exists = "overwrite"
  contents  = <<EOF
provider "aws" {
  assume_role {
    role_arn = "${local.component_vars.aws_provider.role_arn}"
  }

  region = "${local.component_vars.aws_provider.region}"
}
EOF
}