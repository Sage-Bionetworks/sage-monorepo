variable "parent_space_id" {
  description = "ID of the parent spacelift space"
  type        = string
}

variable "org_sagebase_dnt_dev_aws_integration_id" {
  description = "ID of the AWS integration for the org-sagebase-dnt-dev account"
  type        = string
}

variable "org_sagebase_dpe_prod_aws_integration_id" {
  description = "ID of the AWS integration for the org-sagebase-dpe-prod account"
  type        = string
}

variable "git_branch" {
  description = "The branch to deploy"
  type        = string
}
