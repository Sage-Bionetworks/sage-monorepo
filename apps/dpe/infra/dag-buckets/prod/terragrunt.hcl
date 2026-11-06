include "project" {
  path   = find_in_parent_folders("project.hcl")
  expose = true
}

terraform {
  source = "../../modules//dag-buckets-stack"
}

# DPE prod account
inputs = {
  aws_account_id = include.project.locals.aws_account_ids.dpe_prod
  buckets        = include.project.locals.dag_buckets.prod
}
