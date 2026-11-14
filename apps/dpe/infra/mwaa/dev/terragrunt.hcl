include "project" {
  path   = find_in_parent_folders("project.hcl")
  expose = true
}

terraform {
  source = "../../modules//mwaa-stack"
}

# DNT dev account
inputs = {
  aws_account_id         = include.project.locals.aws_account_ids.dnt_dev
  deployment_environment = "dev"
  dag_s3_buckets         = keys(include.project.locals.dag_buckets.dev)

  vpc_name             = "mwaa-dev"
  vpc_cidr_block       = "10.52.48.0/22"
  azs                  = ["us-east-1a", "us-east-1b"]
  private_subnet_cidrs = ["10.52.48.0/24", "10.52.49.0/24"]
  public_subnet_cidrs  = ["10.52.50.0/24", "10.52.51.0/24"]

  enabled         = true
  name            = "synapse-mwaa-dev"
  airflow_version = "3.3.1"
}
