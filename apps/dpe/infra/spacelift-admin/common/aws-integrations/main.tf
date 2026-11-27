# Resources derived from: https://registry.terraform.io/providers/spacelift-io/spacelift/latest/docs/resources/aws_integration
resource "spacelift_aws_integration" "org-sagebase-dnt-dev-aws-integration" {
  name                           = "org-sagebase-dnt-dev-aws-integration"
  role_arn                       = "arn:aws:iam::631692904429:role/spacelift-admin-role"
  generate_credentials_in_worker = false
  duration_seconds               = 3600
  space_id                       = "root"
}

resource "spacelift_aws_integration" "org-sagebase-dpe-prod-aws-integration" {
  name                           = "org-sagebase-dpe-prod-aws-integration"
  role_arn                       = "arn:aws:iam::766808016710:role/spacelift-admin-role"
  generate_credentials_in_worker = false
  duration_seconds               = 3600
  space_id                       = "root"
}
