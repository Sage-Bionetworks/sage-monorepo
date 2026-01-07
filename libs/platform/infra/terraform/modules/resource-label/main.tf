locals {
  defaults = {
    label_order         = ["organization", "product", "application", "component", "attributes", "region", "environment"]
    regex_replace_chars = "/[^-a-zA-Z0-9]/"
    delimiter           = "-"
    replacement         = ""
    id_length_limit     = 0
    id_hash_length      = 5
    label_key_case      = "title"
    label_value_case    = "lower"
    project_paths       = {}

    # The default value of labels_as_tags cannot be included in this defaults'
    # map because it creates a circular dependency.
  }

  default_labels_as_tags = keys(local.tags_context)
  # Unlike other inputs, the first setting of `labels_as_tags` cannot be later overridden. However,
  # we still have to pass the `input` map as the context to the next module. So we need to distinguish
  # between the first setting of var.labels_as_tags == null as meaning set the default and do not change
  # it later, versus later settings of var.labels_as_tags that should be ignored. So, we make the
  # default value in context be "unset", meaning it can be changed, but when it is unset and
  # var.labels_as_tags is null, we change it to "default". Once it is set to "default" we will
  # not allow it to be changed again, but of course we have to detect "default" and replace it
  # with local.default_labels_as_tags when we go to use it.
  #
  # We do not want to use null as default or unset, because Terraform has issues with
  # the value of an object field being null in some places and [] in others.
  # We do not want to use [] as default or unset because that is actually a valid setting
  # that we want to have override the default.
  #
  # To determine whether that context.labels_as_tags is not set,
  # we have to cover 2 cases: 1) context does not have a labels_as_tags key, 2) it is present and set to ["unset"]
  # TODO: Try to simplify. Is it actually OK to labels_as_tags = null in defaults?
  context_labels_as_tags_is_unset = try(contains(var.context.labels_as_tags, "unset"), true)

  # Parameters that the user cannot override.
  replacement    = local.defaults.replacement
  id_hash_length = local.defaults.id_hash_length

  # The values provided by variables supersede the values inherited from the
  # context object, except for tags and attributes which are merged.
  input = {
    enabled      = var.enabled == null ? var.context.enabled : var.enabled
    organization = var.organization == null ? var.context.organization : var.organization
    product      = var.product == null ? var.context.product : var.product
    application  = var.application == null ? var.context.application : var.application
    component    = var.component == null ? var.context.component : var.component
    # Modules add attributes (passed via var) to those from context, ensuring uniqueness and removing empty values.
    attributes  = compact(distinct(concat(coalesce(var.context.attributes, []), coalesce(var.attributes, []))))
    region      = var.region == null ? var.context.region : var.region
    environment = var.environment == null ? var.context.environment : var.environment
    delimiter   = var.delimiter == null ? var.context.delimiter : var.delimiter
    tags        = merge(var.context.tags, var.tags)

    label_order         = var.label_order == null ? var.context.label_order : var.label_order
    regex_replace_chars = var.regex_replace_chars == null ? var.context.regex_replace_chars : var.regex_replace_chars
    id_length_limit     = var.id_length_limit == null ? var.context.id_length_limit : var.id_length_limit
    label_key_case      = var.label_key_case == null ? var.context.label_key_case : var.label_key_case
    label_value_case    = var.label_value_case == null ? var.context.label_value_case : var.label_value_case

    labels_as_tags = local.context_labels_as_tags_is_unset ? var.labels_as_tags : var.context.labels_as_tags
    project_paths  = var.project_paths == null ? var.context.project_paths : var.project_paths
  }

  enabled             = local.input.enabled
  regex_replace_chars = coalesce(local.input.regex_replace_chars, local.defaults.regex_replace_chars)

  # string_label_names are names of inputs that are strings (not list of
  # strings) used as labels.
  string_label_names = ["organization", "product", "application", "component", "region", "environment"]

  # Sanitize the value of the labels specified (replace characters).
  sanitized_label_values = { for k in local.string_label_names : k =>
    local.input[k] == null ? "" : replace(local.input[k], local.regex_replace_chars, local.replacement)
  }
  sanitized_attributes = compact(distinct([for v in local.input.attributes : replace(v, local.regex_replace_chars, local.replacement)]))

  # Set the casing of the label values.
  formatted_label_values = { for k in local.string_label_names : k => local.label_value_case == "none" ? local.sanitized_label_values[k] :
    local.label_value_case == "title" ? title(lower(local.sanitized_label_values[k])) :
    local.label_value_case == "upper" ? upper(local.sanitized_label_values[k]) : lower(local.sanitized_label_values[k])
  }

  attributes = compact(distinct([
    for v in local.sanitized_attributes : (local.label_value_case == "none" ? v :
      local.label_value_case == "title" ? title(lower(v)) :
    local.label_value_case == "upper" ? upper(v) : lower(v))
  ]))

  # Formatted labels.
  organization = local.formatted_label_values["organization"]
  product      = local.formatted_label_values["product"]
  application  = local.formatted_label_values["application"]
  component    = local.formatted_label_values["component"]
  region       = local.formatted_label_values["region"]
  environment  = local.formatted_label_values["environment"]

  # labels_as_tags is an exception to the rule that input vars override context values (see above)
  labels_as_tags = contains(local.input.labels_as_tags, "default") ? local.default_labels_as_tags : local.input.labels_as_tags

  # Set variables from input if set, otherwise from defaults.
  delimiter        = local.input.delimiter == null ? local.defaults.delimiter : local.input.delimiter
  label_order      = local.input.label_order == null ? local.defaults.label_order : coalescelist(local.input.label_order, local.defaults.label_order)
  id_length_limit  = local.input.id_length_limit == null ? local.defaults.id_length_limit : local.input.id_length_limit
  label_key_case   = local.input.label_key_case == null ? local.defaults.label_key_case : local.input.label_key_case
  label_value_case = local.input.label_value_case == null ? local.defaults.label_value_case : local.input.label_value_case

  project_paths = local.input.project_paths == null ? local.defaults.project_paths : local.input.project_paths

  # GENERATE TAGS

  # The labels available as tags. The labels effectively used as tags are
  # defined by labels_as_tags.
  tags_context = {
    organization = local.organization,
    product      = local.product,
    application  = local.application,
    component    = local.component,
    attributes   = local.id_context.attributes
    region       = local.region,
    environment  = local.environment
  }

  # Tags generated from labels as defined by labels_as_tags.
  generated_tags = {
    for l in setintersection(keys(local.tags_context), local.labels_as_tags) :
    local.label_key_case == "upper" ? upper(l) : (
      local.label_key_case == "lower" ? lower(l) : title(lower(l))
    ) => local.tags_context[l] if length(local.tags_context[l]) > 0
  }

  # The final list of tags.
  tags = merge(local.generated_tags, local.input.tags)

  # GENERATE ID
  az_map = module.aws_utils.region_az_alt_code_maps[var.region_format]

  # The labels available as ID elements.
  id_context = {
    organization = local.organization,
    product      = local.product,
    application  = local.application,
    component    = local.component,
    attributes   = join(local.delimiter, local.attributes)
    region       = length(local.region) > 0 ? local.az_map[local.region] : local.region,
    environment  = local.environment
  }

  # Find the labels specified in label_order whose value is not blank.
  labels = [for l in local.label_order : local.id_context[l] if length(local.id_context[l]) > 0]

  id_full = join(local.delimiter, local.labels)
  # Create a truncated ID if needed
  delimiter_length = length(local.delimiter)
  # Calculate length of normal part of ID, leaving room for delimiter and hash
  id_truncated_length_limit = local.id_length_limit - (local.id_hash_length + local.delimiter_length)
  # Truncate the ID and ensure a single (not double) trailing delimiter
  id_truncated = local.id_truncated_length_limit <= 0 ? "" : "${trimsuffix(substr(local.id_full, 0, local.id_truncated_length_limit), local.delimiter)}${local.delimiter}"
  # Support usages that disallow numeric characters. Would prefer tr 0-9 q-z but Terraform does not support it.
  # Probably would have been better to take the hash of only the characters being removed,
  # so identical removed strings would produce identical hashes, but it is not worth breaking existing IDs for.
  id_hash_plus = "${md5(local.id_full)}qrstuvwxyz"
  id_hash_case = local.label_value_case == "title" ? title(local.id_hash_plus) : local.label_value_case == "upper" ? upper(local.id_hash_plus) : local.label_value_case == "lower" ? lower(local.id_hash_plus) : local.id_hash_plus
  id_hash      = replace(local.id_hash_case, local.regex_replace_chars, local.replacement)
  # Create the short ID by adding a hash to the end of the truncated ID
  id_short = substr("${local.id_truncated}${local.id_hash}", 0, local.id_length_limit)
  id       = local.id_length_limit != 0 && length(local.id_full) > local.id_length_limit ? local.id_short : local.id_full

  # Context of this label to pass to other label modules.
  output_context = {
    enabled             = local.enabled
    organization        = local.organization
    product             = local.product,
    application         = local.application,
    component           = local.component,
    attributes          = local.attributes
    region              = local.region
    environment         = local.environment
    delimiter           = local.delimiter
    tags                = local.tags
    label_order         = local.label_order
    regex_replace_chars = local.regex_replace_chars
    id_length_limit     = local.id_length_limit
    label_key_case      = local.label_key_case
    label_value_case    = local.label_value_case
    labels_as_tags      = local.labels_as_tags
    project_paths       = local.project_paths
  }
}

module "aws_utils" {
  source = "../aws-utils"
}
