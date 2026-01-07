locals {
  enabled = module.this.enabled

  bucket_enabled   = local.enabled && var.bucket_enabled
  dynamodb_enabled = local.enabled && var.dynamodb_enabled

  bucket_label_enabled = local.enabled && (var.s3_bucket_name == "" || var.s3_bucket_name == null)

  bucket_name         = local.bucket_label_enabled ? module.bucket_label.id : var.s3_bucket_name
  dynamodb_table_name = local.dynamodb_enabled ? coalesce(var.dynamodb_table_name, module.dynamodb_table_label.id) : ""

  policy = one(data.aws_iam_policy_document.bucket_policy[*].json)
}

module "bucket_label" {
  source = "${var.project_paths.terraform}//modules/resource-label"

  enabled         = local.bucket_label_enabled
  region          = "us-east-1" # TODO: replace by var.region
  region_format   = "to_short"
  id_length_limit = 63

  context = module.this.context
}

module "dynamodb_table_label" {
  source = "${var.project_paths.terraform}//modules/resource-label"

  enabled       = local.dynamodb_enabled
  attributes    = ["lock"]
  region        = "us-east-1" # TODO: replace by var.region
  region_format = "to_short"

  context = module.this.context
}

data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

# The bucket that will store the Terraform states.
resource "aws_s3_bucket" "default" {
  count = local.bucket_enabled ? 1 : 0

  bucket        = substr(local.bucket_name, 0, 63)
  force_destroy = var.force_destroy

  tags = module.this.tags
}

# This resource attaches a bucket policy to an AWS S3 bucket, defining access
# permissions and security rules. It depends on the public access block
# settings, ensuring restrictions are applied before the policy takes effect.
resource "aws_s3_bucket_policy" "default" {
  count = local.bucket_enabled ? 1 : 0

  bucket     = one(aws_s3_bucket.default[*].id)
  policy     = local.policy
  depends_on = [aws_s3_bucket_public_access_block.default]
}

# This resource sets the ACL to private for an AWS S3 bucket, ensuring only the
# bucket owner has access. It depends on bucket ownership controls being set to
# BucketOwnerPreferred before applying the ACL.
resource "aws_s3_bucket_acl" "default" {
  count = local.bucket_enabled && !var.bucket_ownership_enforced_enabled ? 1 : 0

  bucket = one(aws_s3_bucket.default[*].id)
  acl    = "private"

  # Default "bucket ownership controls" for new S3 buckets is
  # "BucketOwnerEnforced", which disables ACLs. So, we need to wait until we
  # change bucket ownership to "BucketOwnerPreferred" before we can set ACLs.
  depends_on = [aws_s3_bucket_ownership_controls.default]
}

# This resource enables versioning for an AWS S3 bucket, allowing multiple
# versions of an object to be retained
resource "aws_s3_bucket_versioning" "default" {
  count = local.bucket_enabled ? 1 : 0

  bucket = one(aws_s3_bucket.default[*].id)

  versioning_configuration {
    status     = "Enabled"
    mfa_delete = "Disabled"
  }
}

# This resource configures server-side encryption (SSE) with AES-256 for an AWS
# S3 bucket, ensuring that all objects stored in the bucket are automatically
# encrypted at rest. It applies the encryption setting by default to enhance
# data security and compliance.
resource "aws_s3_bucket_server_side_encryption_configuration" "default" {
  count = local.bucket_enabled ? 1 : 0

  bucket = one(aws_s3_bucket.default[*].id)

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = var.sse_encryption
      kms_master_key_id = var.kms_master_key_id
    }
  }
}

# TODO
# resource "aws_s3_bucket_logging" "default" {
#   count = local.bucket_enabled && length(var.logging) > 0 ? 1 : 0

#   bucket = one(aws_s3_bucket.default[*].id)

#   target_bucket = var.logging[0].target_bucket
#   target_prefix = var.logging[0].target_prefix
# }

# This resource configures public access block settings for an AWS S3 bucket,
# restricting public access based on configurable variables. It helps enforce
# security best practices by blocking public ACLs, ignoring existing public
# ACLs, preventing public bucket policies, and restricting public access unless
# explicitly allowed.
resource "aws_s3_bucket_public_access_block" "default" {
  count = local.bucket_enabled && var.enable_public_access_block ? 1 : 0

  bucket                  = one(aws_s3_bucket.default[*].id)
  block_public_acls       = var.block_public_acls
  ignore_public_acls      = var.ignore_public_acls
  block_public_policy     = var.block_public_policy
  restrict_public_buckets = var.restrict_public_buckets
}

# After you apply the bucket owner enforced setting for Object Ownership, ACLs
# are disabled for the bucket. See
# https://docs.aws.amazon.com/AmazonS3/latest/userguide/about-object-ownership.html
resource "aws_s3_bucket_ownership_controls" "default" {
  count = local.bucket_enabled ? 1 : 0

  bucket = one(aws_s3_bucket.default[*].id)

  rule {
    object_ownership = var.bucket_ownership_enforced_enabled ? "BucketOwnerEnforced" : "BucketOwnerPreferred"
  }
  depends_on = [time_sleep.wait_for_aws_s3_bucket_settings]
}


# Workaround S3 eventual consistency for settings objects.
resource "time_sleep" "wait_for_aws_s3_bucket_settings" {
  count = local.enabled ? 1 : 0

  depends_on       = [aws_s3_bucket_public_access_block.default, aws_s3_bucket_policy.default]
  create_duration  = "30s"
  destroy_duration = "30s"
}

# This DynamoDB table will be used to lock the state to prevent concurrent
# modification.
resource "aws_dynamodb_table" "with_server_side_encryption" {
  count = local.dynamodb_enabled ? 1 : 0

  name                        = local.dynamodb_table_name
  billing_mode                = var.billing_mode
  read_capacity               = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
  write_capacity              = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  deletion_protection_enabled = var.deletion_protection_enabled

  # https://www.terraform.io/docs/backends/types/s3.html#dynamodb_table
  hash_key = "LockID"

  server_side_encryption {
    enabled = true
  }

  point_in_time_recovery {
    enabled = var.enable_point_in_time_recovery
  }

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = module.dynamodb_table_label.tags
}
