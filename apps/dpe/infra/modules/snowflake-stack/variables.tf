variable "aws_account_id" {
  description = "AWS account ID where S3 resources will be created"
  type        = string
}

variable "bucket_name" {
  description = "Name of the S3 bucket for Snowflake data storage"
  type        = string
}

variable "region" {
  description = "AWS region for S3 bucket"
  type        = string
  default     = "us-east-1"
}

variable "source_account_id" {
  description = "AWS account ID that will replicate TO this bucket (for cross-account replication)"
  type        = string
}

variable "source_bucket_arn" {
  description = "ARN of the source bucket that will replicate to this bucket"
  type        = string
}

variable "snowflake_principal_arn" {
  description = "ARN of the Snowflake principal (user or role) that will assume the role"
  type        = string
}

variable "snowflake_external_id" {
  description = "External ID for Snowflake role assumption"
  type        = string
  sensitive   = true
}

variable "source_iam_role" {
  description = "Name of the IAM role in the source account that will have KMS permissions"
  type        = string
}
