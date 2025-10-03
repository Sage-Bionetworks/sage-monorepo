output "s3_bucket_domain_name" {
  value       = one(aws_s3_bucket.default[*].bucket_domain_name)
  description = "S3 bucket domain name."
}

output "s3_bucket_id" {
  value       = one(aws_s3_bucket.default[*].id)
  description = "S3 bucket ID."
}

output "s3_bucket_arn" {
  value       = one(aws_s3_bucket.default[*].arn)
  description = "S3 bucket ARN."
}

output "s3_bucket_region" {
  value = var.region
}

output "dynamodb_table_name" {
  value       = one(aws_dynamodb_table.with_server_side_encryption[*].name)
  description = "DynamoDB table name."
}

output "dynamodb_table_id" {
  value       = one(aws_dynamodb_table.with_server_side_encryption[*].id)
  description = "DynamoDB table ID."
}

output "dynamodb_table_arn" {
  value       = one(aws_dynamodb_table.with_server_side_encryption[*].arn)
  description = "DynamoDB table ARN."
}
