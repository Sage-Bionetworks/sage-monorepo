output "id" {
  value       = local.enabled ? local.id : ""
  description = "Generated ID."
}

output "id_full" {
  value       = local.enabled ? local.id_full : ""
  description = "Generated ID not restricted in length."
}

output "enabled" {
  value       = local.enabled
  description = "True if the module is enabled, false otherwise."
}

output "organization" {
  value       = local.enabled ? local.organization : ""
  description = "Normalized organization."
}

output "product" {
  value       = local.enabled ? local.product : ""
  description = "Normalized product."
}

output "application" {
  value       = local.enabled ? local.application : ""
  description = "Normalized application."
}

output "component" {
  value       = local.enabled ? local.component : ""
  description = "Normalized component."
}

output "region" {
  value       = local.enabled ? local.region : ""
  description = "Normalized region."
}

output "environment" {
  value       = local.enabled ? local.environment : ""
  description = "Normalized environment."
}

output "delimiter" {
  value       = local.enabled ? local.delimiter : ""
  description = "Delimiter used to assemble the ID from the labels."
}

output "tags" {
  value       = local.enabled ? local.tags : {}
  description = "Normalized tags."
}

output "label_order" {
  value       = local.label_order
  description = "The naming order actually used to create the ID."
}

output "regex_replace_chars" {
  value       = local.regex_replace_chars
  description = "The regex_replace_chars actually used to sanitize the ID."
}

output "id_length_limit" {
  value       = local.id_length_limit
  description = "The id_length_limit used to create the ID, with `0` meaning unlimited."
}

output "normalized_context" {
  value       = local.output_context
  description = "Normalized context of this module."
}

output "context" {
  value       = local.input
  description = <<-EOT
    Merged but otherwise unmodified input to this module, to be used as context
    input to other modules. Note: this version will have null values as
    defaults, not the values actually used as defaults.
  EOT
}

# output "region_code" {
#   value       = local.region_code
#   description = ""
# }
