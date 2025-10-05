include "project" {
  path   = find_in_parent_folders("project.hcl")
  expose = true
}

locals {
  component      = "github-oidc-provider"
  module_vars    = include.project.locals.project_vars.modules.github_oidc_provider
  repo           = local.module_vars.repository
  allowed_subs   = local.module_vars.allowed_subs
  managed_policies = try(local.module_vars.managed_policy_arns, [])
  deploy_role_name  = try(local.module_vars.deploy_role_name, null)
  create_deploy_role = try(local.module_vars.create_deploy_role, true)

  # Allow an alternate ad-hoc env var override for existing provider ARN while still honoring project_vars
  existing_provider_arn_env = trimspace(get_env("GITHUB_OIDC_PROVIDER_ARN", ""))
  existing_provider_arn_base = try(local.module_vars.existing_provider_arn, "")
  existing_provider_arn = length(local.existing_provider_arn_env) > 0 ? local.existing_provider_arn_env : (length(trimspace(local.existing_provider_arn_base)) > 0 ? trimspace(local.existing_provider_arn_base) : null)
}

terraform {
  source = "${include.project.inputs.project_paths.infra}//modules/iam-github-oidc"
}

inputs = {
  enabled            = true
  repository         = local.repo
  allowed_subs       = local.allowed_subs
  managed_policy_arns = local.managed_policies
  deploy_role_name    = local.deploy_role_name
  create_deploy_role  = local.create_deploy_role
  existing_provider_arn = local.existing_provider_arn
}
