variable "region" {
  description = "AWS region of the stack. Set by project.hcl"
  type        = string
}

variable "aws_account_id" {
  description = "AWS account the stack deploys to. The plan fails if the stack's AWS integration is for another account"
  type        = string
}

variable "deployment_environment" {
  description = "dev or prod. Exposed to DAGs as AIRFLOW__ORCA__ENVIRONMENT, and selects the orca-recipes GitHub environment (mwaa-<value>) allowed to deploy DAGs"
  type        = string
}

variable "dag_s3_buckets" {
  description = "Names of the S3 buckets the MWAA execution role can read and write. The dag-buckets unit of the same environment creates them"
  type        = list(string)
}

variable "vpc_name" {
  description = "Prefix of the VPC's CloudFormation exports, which organizations-infra 710-tgw looks up by name"
  type        = string
}

variable "vpc_cidr_block" {
  description = "CIDR of the VPC. Must be unique across the Sage AWS organization so the Transit Gateway can route to it. Picked with IPAM as described in https://sagebionetworks.jira.com/wiki/spaces/IT/pages/2850586648/Setup+AWS+VPC. Changing it replaces the VPC and the MWAA environment"
  type        = string
}

variable "azs" {
  description = "Availability zones of the subnets"
  type        = list(string)
}

variable "private_subnet_cidrs" {
  description = "CIDRs of the private subnets, one per availability zone. MWAA requires exactly two"
  type        = list(string)
}

variable "public_subnet_cidrs" {
  description = "CIDRs of the public subnets, which carry the NAT gateway"
  type        = list(string)
}

variable "enabled" {
  description = "No AWS resources are created until this is true"
  type        = bool
}

variable "name" {
  description = "Name of the MWAA environment. The globally unique S3 bucket name is derived from it: <name>-s3"
  type        = string
}

variable "airflow_version" {
  description = "MWAA-supported Airflow version"
  type        = string
}
