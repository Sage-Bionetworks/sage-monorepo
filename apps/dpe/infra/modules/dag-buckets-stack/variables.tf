variable "region" {
  description = "AWS region of the stack. Set by project.hcl"
  type        = string
}

variable "aws_account_id" {
  description = "AWS account the stack deploys to. The plan fails if the stack's AWS integration is for another account"
  type        = string
}

variable "buckets" {
  description = "Buckets to create, keyed by bucket name. public = true makes the objects readable by anyone and allows browser GETs (CORS)"
  type        = map(object({ public = bool }))
}
