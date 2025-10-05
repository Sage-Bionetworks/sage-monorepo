output "oidc_provider_arn" {
  value       = local.use_existing_provider ? var.existing_provider_arn : try(aws_iam_openid_connect_provider.github[0].arn, null)
  description = "ARN of the GitHub OIDC provider (existing or newly created)."
}

output "deploy_role_arn" {
  value       = try(aws_iam_role.deploy[0].arn, null)
  description = "ARN of the created deploy role (if any)."
}

output "full_subs" {
  value       = local.full_subs
  description = "Fully qualified sub claim patterns allowed."
}
