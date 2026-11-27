# After infra is imported it can be commented out or removed. Keeping it here for reference.

# import {
#   # The initial administrative stack is created manually in the Spacelift UI, and imported
#   # See https://docs.spacelift.io/vendors/terraform/terraform-provider.html#proposed-workflow
#   # "We suggest to first manually create a single administrative stack, and then use it
#   # to programmatically define other stacks as necessary."
#   to = spacelift_stack.root_administrative_stack
#   id = "root-spacelift-administrative-stack"
# }

locals {
  git_branch = "main"
}

# Every file this stack reads is under project_root, so any change to them triggers it
resource "spacelift_stack" "root_administrative_stack" {
  github_enterprise {
    namespace = "Sage-Bionetworks"
    id        = "sage-bionetworks-workflows-gh"
  }

  autodeploy              = false
  protect_from_deletion   = true
  branch                  = local.git_branch
  description             = "Manages other spacelift resources"
  name                    = "Root Spacelift Administrative Stack"
  project_root            = "apps/dpe/infra/spacelift-admin"
  terraform_version       = "1.10.6"
  terraform_workflow_tool = "OPEN_TOFU"
  repository              = "sage-monorepo"
  space_id                = "root"
}


resource "spacelift_role" "stack_admin" {
  name    = "Stack admin"
  actions = ["SPACE_ADMIN"]
}

resource "spacelift_role_attachment" "spacelift-admin-operator" {
  stack_id = spacelift_stack.root_administrative_stack.id
  role_id  = spacelift_role.stack_admin.id
  space_id = "root"
}

resource "spacelift_space" "environment" {
  name             = "environment"
  parent_space_id  = "root"
  description      = "Contains all the resources to deploy out to each enviornment."
  inherit_entities = true
}

module "common" {
  source     = "./common"
  depends_on = [spacelift_stack.root_administrative_stack]
}

module "deployments" {
  source = "./deployments"
  depends_on = [
    spacelift_stack.root_administrative_stack,
    module.common,
  ]
  parent_space_id                          = spacelift_space.environment.id
  org_sagebase_dnt_dev_aws_integration_id  = module.common.org_sagebase_dnt_dev_aws_integration_id
  org_sagebase_dpe_prod_aws_integration_id = module.common.org_sagebase_dpe_prod_aws_integration_id
  git_branch                               = local.git_branch
}
