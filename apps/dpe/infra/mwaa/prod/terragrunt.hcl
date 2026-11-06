include "project" {
  path   = find_in_parent_folders("project.hcl")
  expose = true
}

terraform {
  source = "../../modules//mwaa-stack"
}

# DPE prod account
inputs = {
  aws_account_id         = include.project.locals.aws_account_ids.dpe_prod
  deployment_environment = "prod"
  dag_s3_buckets         = keys(include.project.locals.dag_buckets.prod)

  vpc_name             = "mwaa-prod"
  vpc_cidr_block       = "10.52.52.0/22"
  azs                  = ["us-east-1a", "us-east-1b"]
  private_subnet_cidrs = ["10.52.52.0/24", "10.52.53.0/24"]
  public_subnet_cidrs  = ["10.52.54.0/24", "10.52.55.0/24"]

  enabled         = true
  name            = "synapse-mwaa-prod"
  airflow_version = "3.3.1"
}
