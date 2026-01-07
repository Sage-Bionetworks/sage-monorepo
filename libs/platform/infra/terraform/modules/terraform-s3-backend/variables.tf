variable "billing_mode" {
  type        = string
  default     = "PAY_PER_REQUEST"
  description = "DynamoDB billing mode."
}

variable "block_public_acls" {
  type        = bool
  default     = true
  description = "Whether Amazon S3 should block public ACLs for this bucket."
}

variable "block_public_policy" {
  type        = bool
  default     = true
  description = "Whether Amazon S3 should block public bucket policies for this bucket."
}

variable "bucket_enabled" {
  type        = bool
  default     = true
  description = "Whether to create the S3 bucket."
}

variable "bucket_ownership_enforced_enabled" {
  type        = bool
  default     = true
  description = "Set bucket object ownership to \"BucketOwnerEnforced\". Disables ACLs."
}

variable "deletion_protection_enabled" {
  type        = bool
  default     = false
  description = "A boolean that enables deletion protection for DynamoDB table."
}

variable "dynamodb_enabled" {
  type        = bool
  default     = true
  description = "Whether to create the DynamoDB table."
}

variable "dynamodb_table_name" {
  type        = string
  default     = null
  description = "Override the name of the DynamoDB table which defaults to using `module.dynamodb_table_label.id`."
}

variable "enable_point_in_time_recovery" {
  type        = bool
  default     = true
  description = "Enable DynamoDB point-in-time recovery."
}

variable "enable_public_access_block" {
  type        = bool
  default     = true
  description = "Enable Bucket Public Access Block."
}

variable "force_destroy" {
  type        = bool
  default     = false
  description = "A boolean that indicates the S3 bucket can be destroyed even if it contains objects. These objects are not recoverable."
}

variable "ignore_public_acls" {
  type        = bool
  default     = true
  description = "Whether Amazon S3 should ignore public ACLs for this bucket."
}

variable "read_capacity" {
  type        = number
  default     = 5
  description = "DynamoDB read capacity units when using provisioned mode."
}

variable "restrict_public_buckets" {
  type        = bool
  default     = true
  description = "Whether Amazon S3 should restrict public bucket policies for this bucket."
}

variable "s3_bucket_name" {
  type        = string
  default     = ""
  description = "S3 bucket name. If not provided, the name will be generated from the context by the label module."

  validation {
    condition     = length(var.s3_bucket_name) < 64
    error_message = "A provided S3 bucket name must be fewer than 64 characters."
  }
}

variable "sse_encryption" {
  type        = string
  default     = "AES256"
  description = <<-EOT
    The server-side encryption algorithm to use.
    Valid values are `AES256`, `aws:kms`, and `aws:kms:dsse`.
    EOT
}

variable "kms_master_key_id" {
  type        = string
  default     = null
  description = <<-EOT
    AWS KMS master key ID used for the SSE-KMS encryption.
    This can only be used when you set the value of sse_algorithm as aws:kms.
    EOT
}

variable "write_capacity" {
  type        = number
  default     = 5
  description = "DynamoDB write capacity units when using provisioned mode."
}
