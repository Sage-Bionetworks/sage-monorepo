resource "aws_kms_key" "rds_export_key" {
  description = "KMS key to encrypt RDS snapshot export objects"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowAccountAdministration"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${var.aws_account_id}:root"
        }
        Action   = "kms:*"
        Resource = "*"
      },
      {
        Sid    = "AllowSourceExportRole"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${var.source_account_id}:role/${var.source_iam_role}"
        }
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:ReEncrypt*",
          "kms:GenerateDataKey*",
          "kms:DescribeKey",
          "kms:CreateGrant"
        ]
        Resource = "*"
      },
      {
        Sid    = "AllowSnowflakeRole"
        Effect = "Allow"
        Principal = {
          AWS = aws_iam_role.snowflake_role.arn
        }
        Action = [
          "kms:Decrypt",
          "kms:DescribeKey"
        ]
        Resource = "*"
      },
      {
        Sid    = "AllowSourceAccountDescribeKey"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${var.source_account_id}:root"
        }
        Action   = "kms:DescribeKey"
        Resource = "*"
      }
    ]
  })

  tags = merge(
    var.tags,
    {
      Name    = "kms-${var.bucket_name}"
      Purpose = "KMS key to encrypt RDS snapshot export objects"
    }
  )
}

resource "aws_kms_alias" "rds_export_key" {
  name          = "alias/kms-${var.bucket_name}"
  target_key_id = aws_kms_key.rds_export_key.key_id
}

resource "aws_s3_bucket" "bucket" {
  bucket = var.bucket_name
  tags = merge(
    var.tags,
    {
      Name        = var.bucket_name
      Purpose     = "Snowflake Data Storage"
      Replication = "Destination"
    }
  )
}

resource "aws_s3_bucket_ownership_controls" "ownership" {
  count  = var.public_access ? 1 : 0
  bucket = aws_s3_bucket.bucket.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "access_block" {
  count  = var.public_access ? 1 : 0
  bucket = aws_s3_bucket.bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_acl" "bucket_acl" {
  count = var.public_access ? 1 : 0
  depends_on = [
    aws_s3_bucket_ownership_controls.ownership,
    aws_s3_bucket_public_access_block.access_block,
  ]

  bucket = aws_s3_bucket.bucket.id
  acl    = "public-read"
}

resource "aws_s3_bucket_versioning" "versioning" {
  bucket     = aws_s3_bucket.bucket.id
  depends_on = [aws_s3_bucket.bucket]
  versioning_configuration {
    status = var.enable_versioning ? "Enabled" : "Disabled"
  }
}

# Bucket policy to allow cross-account replication
resource "aws_s3_bucket_policy" "replication_destination_policy" {
  bucket = aws_s3_bucket.bucket.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowSpaceliftAdminRole"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${var.aws_account_id}:role/spacelift-admin-role"
        }
        Action = [
          "s3:GetBucketVersioning",
          "s3:GetEncryptionConfiguration",
          "s3:PutEncryptionConfiguration",
          "s3:PutBucketPolicy"
        ]
        Resource = aws_s3_bucket.bucket.arn
      },
      {
        Sid    = "AllowCrossAccountReplication"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${var.source_account_id}:root"
        }
        Action = [
          "s3:ReplicateObject",
          "s3:ReplicateDelete",
          "s3:ReplicateTags",
          "s3:ObjectOwnerOverrideToBucketOwner"
        ]
        Resource = "${aws_s3_bucket.bucket.arn}/*"
      },
      {
        Sid    = "AllowCrossAccountReplicationList"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${var.source_account_id}:root"
        }
        Action = [
          "s3:List*",
          "s3:GetBucketVersioning"
        ]
        Resource = aws_s3_bucket.bucket.arn
      },
      {
        Sid    = "AllowCrossAccountKMSAccess"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${var.source_account_id}:root"
        }
        Action = [
          "s3:PutObject"
        ]
        Resource = "${aws_s3_bucket.bucket.arn}/*"
        Condition = {
          StringEquals = {
            "s3:x-amz-server-side-encryption"                = "aws:kms"
            "s3:x-amz-server-side-encryption-aws-kms-key-id" = aws_kms_key.rds_export_key.arn
          }
        }
      }
    ]
  })
}

# Set up encryption using existing KMS key
resource "aws_s3_bucket_server_side_encryption_configuration" "encryption" {
  bucket = aws_s3_bucket.bucket.id
  depends_on = [
    aws_s3_bucket_policy.replication_destination_policy,
    aws_kms_key.rds_export_key
  ]

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = aws_kms_key.rds_export_key.arn
    }
    bucket_key_enabled = true
  }
}

resource "aws_iam_policy" "snowflake_s3_access_policy" {
  name        = "snowflake-s3-access-${var.bucket_name}"
  description = "IAM policy for Snowflake to access S3 bucket and KMS key"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "S3ExportWriteAndRead"
        Effect = "Allow"
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:ListBucket",
          "s3:GetBucketLocation",
          "s3:DeleteObject"
        ]
        Resource = [
          aws_s3_bucket.bucket.arn,
          "${aws_s3_bucket.bucket.arn}/*"
        ]
      },
      {
        Sid    = "KMSForSSEKMSObjects"
        Effect = "Allow"
        Action = [
          "kms:Decrypt",
          "kms:Encrypt",
          "kms:GenerateDataKey*",
          "kms:DescribeKey",
          "kms:CreateGrant"
        ]
        Resource = [
          aws_kms_key.rds_export_key.arn
        ]
      }
    ]
  })

  tags = merge(
    var.tags,
    {
      Name    = "snowflake-s3-access-${var.bucket_name}"
      Purpose = "Snowflake S3 and KMS access policy"
    }
  )
}

resource "aws_iam_role" "snowflake_role" {
  name        = "snowflake-role-${var.bucket_name}"
  description = "IAM role for Snowflake to access S3 bucket"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowSnowflake"
        Effect = "Allow"
        Principal = {
          AWS = var.snowflake_principal_arn
        }
        Action = "sts:AssumeRole"
        Condition = {
          StringEquals = {
            "sts:ExternalId" = var.snowflake_external_id
          }
        }
      }
    ]
  })

  tags = merge(
    var.tags,
    {
      Name    = "snowflake-role-${var.bucket_name}"
      Purpose = "Snowflake S3 access role"
    }
  )
}

resource "aws_iam_role_policy_attachment" "snowflake_policy_attachment" {
  role       = aws_iam_role.snowflake_role.name
  policy_arn = aws_iam_policy.snowflake_s3_access_policy.arn
}
