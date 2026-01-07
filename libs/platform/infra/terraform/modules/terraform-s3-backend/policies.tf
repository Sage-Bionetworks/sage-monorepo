# This data source defines an S3 bucket policy document enforcing security best
# practices.
data "aws_iam_policy_document" "bucket_policy" {
  count = local.enabled ? 1 : 0

  # Deny uploads that do not specify encryption
  statement {
    sid    = "DenyIncorrectEncryptionHeader"
    effect = "Deny"

    principals {
      identifiers = ["*"]
      type        = "AWS"
    }

    actions   = ["s3:PutObject"]
    resources = ["arn:aws:s3:::${local.bucket_name}/*"]

    condition {
      test     = "StringNotEquals"
      variable = "s3:x-amz-server-side-encryption"
      values   = ["AES256", "aws:kms"]
    }
  }

  # Deny uploads where the encryption header is missing
  statement {
    sid    = "DenyUnEncryptedObjectUploads"
    effect = "Deny"

    principals {
      identifiers = ["*"]
      type        = "AWS"
    }

    actions   = ["s3:PutObject"]
    resources = ["arn:aws:s3:::${local.bucket_name}/*"]

    condition {
      test     = "Null"
      variable = "s3:x-amz-server-side-encryption"
      values   = ["true"]
    }
  }

  # Enforce TLS.
  # The sid must not be changed because Terragrunt uses it to check the
  # existance of this statement.
  # See https://github.com/hyterion/hyterion-monorepo/issues/228
  statement {
    sid    = "EnforcedTLS"
    effect = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions = ["s3:*"]
    resources = [
      "arn:aws:s3:::${local.bucket_name}",
      "arn:aws:s3:::${local.bucket_name}/*",
    ]

    condition {
      test     = "Bool"
      variable = "aws:SecureTransport"
      values   = ["false"]
    }
  }

  # Enable root access. This is a no-op because the root account has already
  # access to this bucket.
  # The sid must not be changed because Terragrunt uses it to check the
  # existance of this statement.
  # See https://github.com/gruntwork-io/terragrunt/issues/2577
  statement {
    sid    = "RootAccess"
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"]
    }

    actions = ["s3:*"]
    resources = [
      "arn:aws:s3:::${local.bucket_name}",
      "arn:aws:s3:::${local.bucket_name}/*",
    ]
  }
}
