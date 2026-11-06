# Spacelift space holding the MWAA stack
resource "spacelift_space" "mwaa-space" {
  name             = var.space_name
  parent_space_id  = var.parent_space_id
  description      = "Contains the managed Airflow resources for the DPE team."
  inherit_entities = true
}

# Runs the Terragrunt unit in project_root, from the tracked branch of this repo
resource "spacelift_stack" "mwaa" {
  github_enterprise {
    namespace = "Sage-Bionetworks"
    id        = "sage-bionetworks-workflows-gh"
  }

  terragrunt {
    terraform_version    = var.opentofu_version
    terragrunt_version   = var.terragrunt_version
    tool                 = "OPEN_TOFU"
    use_run_all          = false
    use_state_management = true
  }

  autodeploy            = false
  protect_from_deletion = true
  # A pull request's proposed run can be promoted to apply the branch to dev. Prod only
  # applies commits on the tracked branch
  allow_run_promotion = var.deployment_environment != "prod"
  branch              = var.git_branch
  description         = "Creates an Amazon MWAA (Managed Airflow) environment"
  name                = "DPE Managed Airflow ${var.deployment_environment}"
  project_root        = "apps/dpe/infra/mwaa/${var.deployment_environment}"
  repository          = "sage-monorepo"
  space_id            = spacelift_space.mwaa-space.id
  # The files outside project_root that its plan reads
  additional_project_globs = [
    "apps/dpe/infra/project.hcl",
    "apps/dpe/infra/modules/mwaa-stack/*.tf",
    "apps/dpe/infra/modules/mwaa/*.tf",
  ]
}

# Gives the stack read and write credentials for the AWS account it deploys to
resource "spacelift_aws_integration_attachment" "mwaa" {
  integration_id = var.aws_integration_id
  stack_id       = spacelift_stack.mwaa.id
  read           = true
  write          = true
}

# The eks-stack stack that the stack above replaces. OpenTofu stops managing it without
# deleting it, so it keeps its state and its AWS integration until it is deleted by hand
removed {
  from = spacelift_stack.mwaa-stack

  lifecycle {
    destroy = false
  }
}

removed {
  from = spacelift_environment_variable.mwaa-stack-environment-variables

  lifecycle {
    destroy = false
  }
}

removed {
  from = spacelift_aws_integration_attachment.mwaa-aws-integration-attachment

  lifecycle {
    destroy = false
  }
}
