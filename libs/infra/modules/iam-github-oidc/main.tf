locals {
  enabled = var.enabled

  repo_parts = split("/", var.repository)
  repo_owner = local.repo_parts[0]
  repo_name  = local.repo_parts[1]

  # Build list of full allowed sub claims like: repo:owner/name:ref:refs/heads/main
  full_subs = [for suffix in var.allowed_subs : "repo:${var.repository}:${suffix}"]

  use_existing_provider = var.existing_provider_arn != null && var.existing_provider_arn != ""
}

resource "aws_iam_openid_connect_provider" "github" {
  count           = local.enabled && !local.use_existing_provider ? 1 : 0
  url             = "https://token.actions.githubusercontent.com"
  client_id_list  = var.audiences
  thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1"]
  tags            = var.tags
}

data "aws_iam_openid_connect_provider" "existing" {
  count = local.enabled && local.use_existing_provider ? 1 : 0
  arn   = var.existing_provider_arn
}

# Trust policy for the deploy role (if created)
data "aws_iam_policy_document" "deploy_assume_role" {
  count = local.enabled && var.create_deploy_role ? 1 : 0

  statement {
    effect = "Allow"
    principals {
      type = "Federated"
      identifiers = [
        local.use_existing_provider ? data.aws_iam_openid_connect_provider.existing[0].arn : aws_iam_openid_connect_provider.github[0].arn
      ]
    }
    actions = ["sts:AssumeRoleWithWebIdentity"]

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = var.audiences
    }

    condition {
      test     = "StringLike"
      variable = "token.actions.githubusercontent.com:sub"
      values   = local.full_subs
    }
  }
}

locals {
  deploy_role_name = coalesce(var.deploy_role_name, "github-${local.repo_owner}-${local.repo_name}-deploy")
}

resource "aws_iam_role" "deploy" {
  count              = local.enabled && var.create_deploy_role ? 1 : 0
  name               = local.deploy_role_name
  assume_role_policy = data.aws_iam_policy_document.deploy_assume_role[0].json
  tags               = var.tags
  force_detach_policies = true
  max_session_duration  = 3600
}

# Attach managed policies if any
resource "aws_iam_role_policy_attachment" "managed" {
  for_each = { for arn in var.managed_policy_arns : arn => arn if local.enabled && var.create_deploy_role }
  role     = aws_iam_role.deploy[0].name
  policy_arn = each.value
}

# Inline policies
locals {
  inline_policies = var.inline_policies
}

resource "aws_iam_role_policy" "inline" {
  for_each = local.inline_policies
  role     = aws_iam_role.deploy[0].name
  name     = each.key

  policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [for s in each.value.statements : {
      Sid      = try(s.sid, null)
      Effect   = s.effect
      Action   = s.actions
      Resource = s.resources
      Condition = try(s.condition, null)
    }]
  })
}
