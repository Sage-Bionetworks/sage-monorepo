locals {
  # Returns the absolute path to the root of the Git repository.
  workspace_root = get_repo_root()

  # Context variables
  organization = "sage"

  # Defines paths to projects within the monorepo to maintain consistency across
  # Terragrunt configurations.
  project_paths = {
    terraform = "${local.workspace_root}/libs/platform/infra/terraform"
  }
}

inputs = {
  organization  = local.organization
  project_paths = local.project_paths
}