resource "spacelift_space" "development" {
  name             = "development"
  parent_space_id  = var.parent_space_id
  description      = "Contains all the resources to deploy out to the dev enviornment."
  inherit_entities = true
}

resource "spacelift_space" "production" {
  name             = "production"
  parent_space_id  = var.parent_space_id
  description      = "Contains all the resources to deploy out to the production enviornment."
  inherit_entities = true
}

# Managed Airflow (MWAA) environment for DPE workflows. Runs the units in
# apps/dpe/infra/mwaa/<env>
module "mwaa-spacelift-development" {
  source = "./mwaa"

  aws_integration_id     = var.org_sagebase_dnt_dev_aws_integration_id
  git_branch             = var.git_branch
  parent_space_id        = spacelift_space.development.id
  space_name             = "mwaa-dev"
  deployment_environment = "dev"
}

module "mwaa-spacelift-production" {
  source = "./mwaa"

  aws_integration_id     = var.org_sagebase_dpe_prod_aws_integration_id
  git_branch             = var.git_branch
  parent_space_id        = spacelift_space.production.id
  space_name             = "mwaa-prod"
  deployment_environment = "prod"
}

# S3 buckets the orca-recipes DAGs write to. Runs the units in apps/dpe/infra/dag-buckets/<env>
module "dag-buckets-spacelift-development" {
  source = "./dag-buckets"

  aws_integration_id     = var.org_sagebase_dnt_dev_aws_integration_id
  git_branch             = var.git_branch
  space_id               = spacelift_space.development.id
  deployment_environment = "dev"
}

module "dag-buckets-spacelift-production" {
  source = "./dag-buckets"

  aws_integration_id     = var.org_sagebase_dpe_prod_aws_integration_id
  git_branch             = var.git_branch
  space_id               = spacelift_space.production.id
  deployment_environment = "prod"
}

# S3 buckets in the DPE prod account that the Synapse RDS snapshots are copied into for
# Snowflake. Runs the units in apps/dpe/infra/snowflake/<env>
module "snowflake-spacelift-development" {
  source = "./snowflake"

  aws_integration_id     = var.org_sagebase_dpe_prod_aws_integration_id
  git_branch             = var.git_branch
  parent_space_id        = spacelift_space.production.id
  space_name             = "synapse-dev-to-snowflake"
  deployment_environment = "dev"
}

module "snowflake-spacelift-production" {
  source = "./snowflake"

  aws_integration_id     = var.org_sagebase_dpe_prod_aws_integration_id
  git_branch             = var.git_branch
  parent_space_id        = spacelift_space.production.id
  space_name             = "synapse-prod-to-snowflake"
  deployment_environment = "prod"
}
