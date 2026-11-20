# Runs the Terragrunt unit in project_root, from the tracked branch of this repo
resource "spacelift_stack" "dag-buckets" {
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
  description         = "Creates the S3 buckets that the orca-recipes DAGs write to"
  name                = "DPE DAG Buckets ${var.deployment_environment}"
  project_root        = "apps/dpe/infra/dag-buckets/${var.deployment_environment}"
  repository          = "sage-monorepo"
  space_id            = var.space_id
  # The files outside project_root that its plan reads
  additional_project_globs = [
    "apps/dpe/infra/project.hcl",
    "apps/dpe/infra/modules/dag-buckets-stack/*.tf",
    "apps/dpe/infra/modules/s3-bucket/*.tf",
  ]
}

# Gives the stack read and write credentials for the AWS account it deploys to
resource "spacelift_aws_integration_attachment" "dag-buckets" {
  integration_id = var.aws_integration_id
  stack_id       = spacelift_stack.dag-buckets.id
  read           = true
  write          = true
}

# The eks-stack stack that the stack above replaces. OpenTofu stops managing it without
# deleting it, so it keeps its state and its AWS integration until it is deleted by hand
removed {
  from = spacelift_stack.dag-buckets-stack

  lifecycle {
    destroy = false
  }
}

removed {
  from = spacelift_environment_variable.dag-buckets-stack-environment-variables

  lifecycle {
    destroy = false
  }
}

removed {
  from = spacelift_aws_integration_attachment.dag-buckets-aws-integration-attachment

  lifecycle {
    destroy = false
  }
}
