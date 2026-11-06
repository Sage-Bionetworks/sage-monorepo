data "aws_caller_identity" "current" {}

locals {
  secrets_connections_prefix = "airflow/connections"
  secrets_variables_prefix   = "airflow/variables"

  orca_recipes_url  = "https://raw.githubusercontent.com/Sage-Bionetworks-Workflows/orca-recipes/${var.orca_recipes_sha}"
  requirements_path = "requirements.txt"
  bucket_name       = trimprefix(module.mwaa.s3_bucket_arn, "arn:aws:s3:::")

  # CIDR of the Transit Gateway hub VPC (unionstationvpc, organizations-infra
  # org-formation/710-tgw). The Sage Client VPN source NATs client traffic to its network
  # interfaces in this VPC, so VPN traffic reaches spoke VPCs from here and not from the
  # VPN client CIDR (10.100.0.0/16)
  sage_vpn_cidr = "10.50.0.0/16"
}

# Download the MWAA Python requirements from the pinned orca-recipes commit.
# This ensures the dependencies deployed to MWAA come from the exact commit
# specified by var.orca_recipes_sha. Fail early if the file does not exist.
# MWAA installs them with the constraints file from the same commit, since the upstream
# Airflow constraints conflict with them and pin packages with vulnerability alerts
data "http" "requirements" {
  count = var.enabled ? 1 : 0

  url = "${local.orca_recipes_url}/requirements-airflow.txt"

  lifecycle {
    postcondition {
      condition     = self.status_code == 200
      error_message = "Could not download requirements-airflow.txt at orca-recipes commit ${var.orca_recipes_sha}"
    }
  }
}

# Dev may install requirements from a feature branch commit. Prod only installs commits on main
data "http" "orca_recipes_sha_on_main" {
  count = var.enabled && var.deployment_environment == "prod" ? 1 : 0

  url             = "https://api.github.com/repos/Sage-Bionetworks-Workflows/orca-recipes/compare/${var.orca_recipes_sha}...main?per_page=1"
  request_headers = { Accept = "application/vnd.github+json" }

  lifecycle {
    postcondition {
      condition     = contains(["ahead", "identical"], try(jsondecode(self.response_body).status, ""))
      error_message = "orca-recipes commit ${var.orca_recipes_sha} is not on main. Bump orca_recipes_sha to a main commit before deploying prod"
    }
  }
}

# Build and upload the requirements file that MWAA will install.
resource "aws_s3_object" "requirements" {
  count = var.enabled ? 1 : 0

  bucket       = local.bucket_name
  key          = local.requirements_path
  content_type = "text/plain"
  content      = <<-EOT
    --constraint "${local.orca_recipes_url}/mwaa/constraints.txt"
    ${data.http.requirements[0].response_body}
  EOT

  tags = var.tags
}

# Lets the execution role read Airflow connections and variables from Secrets Manager
data "aws_iam_policy_document" "secrets" {
  statement {
    sid     = "AirflowSecretsRead"
    effect  = "Allow"
    actions = ["secretsmanager:GetSecretValue"]
    resources = [
      "arn:aws:secretsmanager:${var.region}:${data.aws_caller_identity.current.account_id}:secret:${local.secrets_connections_prefix}/*",
      "arn:aws:secretsmanager:${var.region}:${data.aws_caller_identity.current.account_id}:secret:${local.secrets_variables_prefix}/*",
    ]
  }

  statement {
    sid       = "AirflowSecretsList"
    effect    = "Allow"
    actions   = ["secretsmanager:ListSecrets"]
    resources = ["*"]
  }
}

# Lets the execution role read and write the objects in var.dag_s3_buckets
data "aws_iam_policy_document" "dag_s3" {
  count = length(var.dag_s3_buckets) > 0 ? 1 : 0

  statement {
    sid       = "DagBucketObjects"
    effect    = "Allow"
    actions   = ["s3:PutObject", "s3:GetObject", "s3:DeleteObject"]
    resources = [for bucket in var.dag_s3_buckets : "arn:aws:s3:::${bucket}/*"]
  }

  statement {
    sid       = "DagBucketList"
    effect    = "Allow"
    actions   = ["s3:ListBucket"]
    resources = [for bucket in var.dag_s3_buckets : "arn:aws:s3:::${bucket}"]
  }
}

# The MWAA environment, plus the S3 bucket, execution role and security group the upstream
# module creates for it
module "mwaa" {
  source  = "cloudposse/mwaa/aws"
  version = "0.16.0"

  enabled = var.enabled

  name       = var.name
  region     = var.region
  vpc_id     = var.vpc_id
  subnet_ids = var.subnet_ids

  airflow_version = var.airflow_version

  # PRIVATE_ONLY is only reachable over the Sage VPN, which needs this VPC to be a Transit
  # Gateway spoke (organizations-infra 710-tgw) and authorized for VPN users (720-client-vpn).
  # Login is IAM-only either way
  webserver_access_mode = "PRIVATE_ONLY"

  # Allows Sage VPN traffic to the webserver's private VPC endpoint
  additional_security_group_rules = [
    {
      key         = "sage-vpn-webserver"
      type        = "ingress"
      from_port   = 443
      to_port     = 443
      protocol    = "tcp"
      cidr_blocks = [local.sage_vpn_cidr]
      description = "Allow HTTPS to the MWAA webserver from the Sage VPN"
    }
  ]

  # Pin MWAA to the S3 version of the generated requirements file.
  # When orca_recipes_sha changes, Terraform uploads new requirements content,
  # producing a new S3 version ID that causes MWAA to install the updated dependencies.
  requirements_s3_path           = local.requirements_path
  requirements_s3_object_version = one(aws_s3_object.requirements[*].version_id)

  # All log groups are disabled upstream by default. The requirements install output is
  # written to the scheduler and worker logs
  scheduler_logs_enabled      = true
  worker_logs_enabled         = true
  task_logs_enabled           = true
  dag_processing_logs_enabled = true
  webserver_logs_enabled      = true

  # Connections and Variables are looked up in Secrets Manager first, then the metadata database
  airflow_configuration_options = {
    "secrets.backend" = "airflow.providers.amazon.aws.secrets.secrets_manager.SecretsManagerBackend"
    "secrets.backend_kwargs" = jsonencode({
      connections_prefix = local.secrets_connections_prefix
      variables_prefix   = local.secrets_variables_prefix
    })
    # Read by DAGs as AIRFLOW__ORCA__ENVIRONMENT to choose dev or prod values
    "orca.environment" = var.deployment_environment
    # Lets tasks receive the dataclasses other tasks return. The default only allows
    # airflow.* classes
    "core.allowed_deserialization_classes_regexp" = ".*"
    # Largest list a task can return for a downstream expand. The default is 1024, and
    # synapse-dataset-to-croissant maps over more datasets than that
    "core.max_map_length" = "8192"
  }
  additionals_policy_documents = concat([data.aws_iam_policy_document.secrets.json], data.aws_iam_policy_document.dag_s3[*].json)

  # Workers finish running tasks (up to 12 hours) before being replaced on update.
  # Must be set: the upstream variable validation fails under OpenTofu when it is null.
  worker_replacement_strategy = "GRACEFUL"

  tags = var.tags
}

# Look up the AWS OIDC provider for GitHub Actions. organizations-infra creates it in every
# account
data "aws_iam_openid_connect_provider" "github" {
  count = var.enabled ? 1 : 0

  url = "https://token.actions.githubusercontent.com"
}

# Define who is allowed to assume the DAG deployment role.
# Only GitHub Actions jobs from orca-recipes running in the matching
# GitHub environment (mwaa-dev or mwaa-prod) can assume the role. Both environments only
# allow workflows from the main branch
data "aws_iam_policy_document" "dag_deploy_trust" {
  count = var.enabled ? 1 : 0

  statement {
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [data.aws_iam_openid_connect_provider.github[0].arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:sub"
      values   = ["repo:Sage-Bionetworks-Workflows/orca-recipes:environment:mwaa-${var.deployment_environment}"]
    }
  }
}

# Define what the GitHub Actions deployment role can do after assuming it.
# Limit access to the dags/ prefix so CI can deploy and remove DAGs without
# modifying requirements.txt or other objects
data "aws_iam_policy_document" "dag_deploy" {
  count = var.enabled ? 1 : 0

  statement {
    actions   = ["s3:ListBucket"]
    resources = [module.mwaa.s3_bucket_arn]

    condition {
      test     = "StringLike"
      variable = "s3:prefix"
      values   = ["dags/*"]
    }
  }

  statement {
    actions   = ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"]
    resources = ["${module.mwaa.s3_bucket_arn}/dags/*"]
  }
}

# Create the AWS role that the orca-recipes GitHub Actions deployment job
# assumes through OIDC
resource "aws_iam_role" "dag_deploy" {
  count = var.enabled ? 1 : 0

  name               = "${var.name}-dag-deploy"
  assume_role_policy = data.aws_iam_policy_document.dag_deploy_trust[0].json

  tags = var.tags
}

# Attach the S3 permissions above to the deployment role. This determines
# what the GitHub Actions job can do after it has successfully assumed the role.
resource "aws_iam_role_policy" "dag_deploy" {
  count = var.enabled ? 1 : 0

  name   = "dag-deploy"
  role   = aws_iam_role.dag_deploy[0].id
  policy = data.aws_iam_policy_document.dag_deploy[0].json
}
