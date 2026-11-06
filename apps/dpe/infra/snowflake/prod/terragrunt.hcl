include "project" {
  path   = find_in_parent_folders("project.hcl")
  expose = true
}

terraform {
  source = "../../modules//snowflake-stack"
}

# Lands the Synapse prod RDS snapshots in the DPE prod account
inputs = {
  aws_account_id = include.project.locals.aws_account_ids.dpe_prod
  bucket_name    = "synapse-snowflake-rds-snapshots-prod"

  source_account_id = "325565585839"
  source_bucket_arn = "arn:aws:s3:::prod.dpe.rds.backups.sagebase.org"
  source_iam_role   = "prod-rds-repl-role"

  snowflake_principal_arn = "arn:aws:iam::365909334157:user/m2nb0000-s"
  snowflake_external_id   = "UO70315_SFCRole=2_MrT3ubydhclba/9w8Kq1btbWsBc="
}
