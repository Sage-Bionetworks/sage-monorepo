locals {
  # orca-recipes commit whose requirements-airflow.txt and mwaa/constraints.txt are installed
  # on MWAA. Shared by dev and prod so they cannot diverge. To bump it, follow
  # https://github.com/Sage-Bionetworks-Workflows/orca-recipes/blob/main/mwaa/README.md#changing-dependencies
  # https://github.com/Sage-Bionetworks-Workflows/orca-recipes/commit/5173f7f54ff4b0e59817145cc6a2b19873c124f0
  orca_recipes_sha = "5173f7f54ff4b0e59817145cc6a2b19873c124f0"

  # Applied to every resource in this stack
  tags = {
    Purpose    = "Managed Airflow for DPE workflows"
    ManagedBy  = "Terraform"
    CostCenter = "No Program / 000000"
  }
}

# Dedicated VPC for the MWAA environment
module "vpc" {
  count = var.enabled ? 1 : 0

  source  = "terraform-aws-modules/vpc/aws"
  version = "6.7.3"

  name = var.vpc_name
  cidr = var.vpc_cidr_block

  azs             = var.azs
  private_subnets = var.private_subnet_cidrs
  public_subnets  = var.public_subnet_cidrs

  # MWAA runs in the private subnets and needs egress to install requirements
  # and reach the AWS APIs it depends on
  create_igw         = true
  enable_nat_gateway = true
  single_nat_gateway = true

  # Leave the VPC's default security group as AWS creates it. MWAA uses the security group
  # created by the mwaa module
  manage_default_security_group = false

  tags = local.tags
}

# Exports the VPC IDs as <region>-<vpc_name>-<key>, the names organizations-infra
# (org-formation/710-tgw) looks up with !CopyValue to attach this VPC to the Transit Gateway.
# Renaming vpc_name or replacing the VPC needs a matching organizations-infra deploy
resource "aws_cloudformation_stack" "vpc_exports" {
  count = var.enabled ? 1 : 0

  name = "${var.vpc_name}-vpc-exports"
  template_body = jsonencode({
    # A stack needs at least one resource
    Resources = { Placeholder = { Type = "AWS::CloudFormation::WaitConditionHandle" } }
    Outputs = {
      for key, value in {
        VPCId             = module.vpc[0].vpc_id
        PrivateRouteTable = module.vpc[0].private_route_table_ids[0]
        PrivateSubnet     = module.vpc[0].private_subnets[0]
        PrivateSubnet1    = module.vpc[0].private_subnets[1]
      } : key => { Value = value, Export = { Name = "${var.region}-${var.vpc_name}-${key}" } }
    }
  })

  tags = local.tags
}

# The MWAA environment, running in the private subnets of the VPC above
module "mwaa" {
  source     = "../mwaa"
  enabled    = var.enabled
  name       = var.name
  region     = var.region
  vpc_id     = one(module.vpc[*].vpc_id)
  subnet_ids = flatten(module.vpc[*].private_subnets)

  airflow_version        = var.airflow_version
  orca_recipes_sha       = local.orca_recipes_sha
  deployment_environment = var.deployment_environment
  dag_s3_buckets         = var.dag_s3_buckets

  tags = local.tags
}
