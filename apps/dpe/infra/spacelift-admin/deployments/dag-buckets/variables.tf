variable "space_id" {
  description = "ID of the spacelift space to create the stack in"
  type        = string
}

variable "git_branch" {
  description = "Git branch to track for deployments"
  type        = string
}

variable "aws_integration_id" {
  description = "ID of the AWS integration in Spacelift"
  type        = string
}

variable "opentofu_version" {
  description = "Version of OpenTofu that Terragrunt runs"
  type        = string
  default     = "1.10.6"
}

variable "terragrunt_version" {
  description = "Version of Terragrunt the stack runs"
  type        = string
  default     = "0.87.5"
}

variable "deployment_environment" {
  description = "dev or prod. Selects the unit apps/dpe/infra/dag-buckets/<value>. Prod stacks do not allow run promotion"
  type        = string
}
