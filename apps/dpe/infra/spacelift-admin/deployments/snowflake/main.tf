# TODO: This resource is being created for each Spacelift config. Abstraction warranted?
resource "spacelift_space" "dpe-space" {
  name             = var.space_name
  parent_space_id  = var.parent_space_id
  description      = "Contains resources for the DPE team."
  inherit_entities = true
}

# Runs the Terragrunt unit in project_root, from the tracked branch of this repo
resource "spacelift_stack" "snowflake" {
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
  branch                = var.git_branch
  description           = "Creates an S3 bucket for Synapse RDS data to be copied into"
  name                  = "Synapse RDS Snapshots ${var.deployment_environment}"
  project_root          = "apps/dpe/infra/snowflake/${var.deployment_environment}"
  repository            = "sage-monorepo"
  space_id              = spacelift_space.dpe-space.id
  # The files outside project_root that its plan reads
  additional_project_globs = [
    "apps/dpe/infra/project.hcl",
    "apps/dpe/infra/modules/snowflake-stack/*.tf",
    "apps/dpe/infra/modules/snowflake-s3-bucket/*.tf",
  ]
}

# Gives the stack read and write credentials for the AWS account it deploys to
resource "spacelift_aws_integration_attachment" "snowflake" {
  integration_id = var.aws_integration_id
  stack_id       = spacelift_stack.snowflake.id
  read           = true
  write          = true
}

# The eks-stack stack that the stack above replaces. OpenTofu stops managing it without
# deleting it, so it keeps its state and its AWS integration until it is deleted by hand
removed {
  from = spacelift_stack.snowflake-stack

  lifecycle {
    destroy = false
  }
}

removed {
  from = spacelift_environment_variable.snowflake-stack-environment-variables

  lifecycle {
    destroy = false
  }
}

removed {
  from = spacelift_aws_integration_attachment.snowflake-aws-integration-attachment

  lifecycle {
    destroy = false
  }
}
