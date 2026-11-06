module "create_bucket_snowflake_rds_landing" {
  source                  = "../snowflake-s3-bucket"
  bucket_name             = var.bucket_name
  aws_account_id          = var.aws_account_id
  region                  = var.region
  enable_versioning       = true
  source_account_id       = var.source_account_id
  source_bucket_arn       = var.source_bucket_arn
  snowflake_principal_arn = var.snowflake_principal_arn
  snowflake_external_id   = var.snowflake_external_id
  source_iam_role         = var.source_iam_role

  tags = {
    Purpose    = "Synapse RDS snapshots to ingest into snowflake"
    ManagedBy  = "Terraform"
    CostCenter = "No Program / 000000"
  }
}
