output "aws_account_id" {
  description = "AWS account ID where S3 resources will be created"
  value       = var.aws_account_id
}

output "bucket_name" {
  description = "Name of the S3 bucket for Snowflake data storage"
  value       = var.bucket_name
}

output "region" {
  description = "AWS region for S3 bucket"
  value       = var.region
}

output "source_account_id" {
  description = "AWS account ID that will replicate TO this bucket (for cross-account replication)"
  value       = var.source_account_id
}

output "source_bucket_arn" {
  description = "ARN of the source bucket that will replicate to this bucket"
  value       = var.source_bucket_arn
}
