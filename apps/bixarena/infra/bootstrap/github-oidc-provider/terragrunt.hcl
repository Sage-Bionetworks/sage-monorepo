include "project" {
  path   = find_in_parent_folders("project.hcl")
  expose = true
}

locals {
  component        = "github-oidc-provider"
  component_vars   = include.project.locals.project_vars.modules.github_oidc_provider
  repo             = local.component_vars.repository
  allowed_subs     = local.component_vars.allowed_subs
  managed_policies = try(local.component_vars.managed_policy_arns, [])
  deploy_role_name = try(local.component_vars.deploy_role_name, null)
  create_deploy_role = try(local.component_vars.create_deploy_role, true)

  existing_provider_arn_raw = coalesce(
    get_env("GITHUB_OIDC_PROVIDER_ARN", ""),
    try(local.component_vars.existing_provider_arn, "")
  )
  existing_provider_arn = length(trimspace(local.existing_provider_arn_raw)) == 0 ? null : trimspace(local.existing_provider_arn_raw)
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
