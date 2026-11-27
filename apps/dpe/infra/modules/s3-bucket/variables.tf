variable "bucket_name" {
  description = "Name of the S3 bucket to create"
  type        = string
}

variable "tags" {
  description = "Tags to apply to the S3 bucket"
  type        = map(string)
  default = {
    "CostCenter" = "No Program / 000000"
  }
}

variable "enable_versioning" {
  description = "Enable versioning on the bucket"
  type        = bool
  default     = true
}

variable "public_access" {
  description = "Enable/block public access to the bucket"
  type        = bool
  default     = false
}

variable "enable_cors" {
  description = "Enable CORS on the bucket"
  type        = bool
  default     = false
}
