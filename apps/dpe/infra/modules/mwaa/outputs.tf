output "arn" {
  description = "ARN of the MWAA environment"
  value       = module.mwaa.arn
}

output "webserver_url" {
  description = "Airflow webserver URL of the MWAA environment"
  value       = module.mwaa.webserver_url
}

output "s3_bucket_arn" {
  description = "ARN of the S3 bucket holding DAGs, requirements and plugins"
  value       = module.mwaa.s3_bucket_arn
}

output "s3_bucket_name" {
  description = "Name of the S3 bucket holding DAGs, requirements and plugins. Set as MWAA_BUCKET on the orca-recipes GitHub environment"
  value       = local.bucket_name
}

output "dag_deploy_role_arn" {
  description = "ARN of the role the orca-recipes DAG deploy job assumes. Set as MWAA_ROLE_ARN on the orca-recipes GitHub environment"
  value       = one(aws_iam_role.dag_deploy[*].arn)
}
