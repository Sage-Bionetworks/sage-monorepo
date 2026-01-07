variable "enabled" {
  type        = bool
  default     = true
  description = "Whether to create resources."
}

variable "repository" {
  type        = string
  description = "GitHub repository in the form 'owner/repo'."
}

variable "existing_provider_arn" {
  type        = string
  default     = null
  description = "If provided, reuse this existing GitHub OIDC provider ARN instead of creating a new one. The URL must match https://token.actions.githubusercontent.com."
}

variable "allowed_subs" {
  type        = list(string)
  description = "List of GitHub OIDC sub suffixes (after 'repo:owner/repo:') allowed to assume roles."
  default     = ["ref:refs/heads/main"]
}

variable "audiences" {
  type        = list(string)
  default     = ["sts.amazonaws.com"]
  description = "OIDC audiences to trust."
}

variable "create_deploy_role" {
  type        = bool
  default     = true
  description = "Whether to create a basic deploy role trust-bound to the OIDC provider."
}

variable "deploy_role_name" {
  type        = string
  default     = null
  description = "Optional explicit name for the deploy role."
}

variable "managed_policy_arns" {
  type        = list(string)
  default     = []
  description = "Managed policy ARNs to attach to the deploy role (if created)."
}

variable "inline_policies" {
  type = map(object({
    statements = list(object({
      sid       = optional(string)
      effect    = string
      actions   = list(string)
      resources = list(string)
      condition = optional(any)
    }))
  }))
  default     = {}
  description = "Inline policy documents to attach to the deploy role. Map key becomes the policy name."
}

variable "tags" {
  type        = map(string)
  default     = {}
  description = "Tags to apply to created resources."
}
