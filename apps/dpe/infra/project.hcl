# Included by every unit under apps/dpe/infra. Each unit is one Spacelift stack, defined in
# spacelift-admin/deployments. Spacelift manages each stack's state, so there is no
# remote_state block
locals {
  region = "us-east-1"

  aws_account_ids = {
    dnt_dev  = "631692904429" # org-sagebase-dnt-dev
    dpe_prod = "766808016710" # org-sagebase-dpe-prod
  }

  # S3 buckets the orca-recipes DAGs write to, by environment. The dag-buckets units create
  # them, and the mwaa units give the MWAA execution role of the same environment access to
  # them. public = true makes the objects readable by anyone and allows browser GETs (CORS)
  dag_buckets = {
    # Dev buckets are private
    dev = {
      "synapse-croissant-metadata-dev"         = { public = false }
      "synapse-croissant-metadata-minimal-dev" = { public = false }
    }
    # Public: Google and the Synapse web client read the croissant files
    prod = {
      "synapse-croissant-metadata"         = { public = true }
      "synapse-croissant-metadata-minimal" = { public = true }
    }
  }
}

# Every module declares region and aws_account_id. The plan fails if the stack's Spacelift
# AWS integration is for another account
generate "provider" {
  path      = "provider.tf"
  if_exists = "overwrite_terragrunt"
  contents  = <<-EOF
    provider "aws" {
      region              = var.region
      allowed_account_ids = [var.aws_account_id]
    }
  EOF
}

inputs = {
  region = local.region
}
