# Tutorial: Create a Reusable Terraform Module

## Overview

This tutorial guides you through creating a reusable Terraform module that can be shared across multiple Terraform projects in the monorepo. You'll learn the module pattern, best practices, and how to integrate with the resource-label system.

!!! info "What is a Terraform Module?"
In this context, a **Terraform module** is a reusable collection of Terraform/OpenTofu resources (`.tf` files) that can be used across multiple Terraform projects. Don't confuse this with Terragrunt modules (which are directories containing `terragrunt.hcl` files that reference Terraform modules).

**What you'll build:**

- A reusable VPC Terraform module with configurable parameters
- Proper input validation and outputs
- Integration with the resource-label naming convention
- Documentation for module consumers

**Time required:** 45-60 minutes

**Prerequisites:**

- Understanding of Infrastructure as Code concepts
- Completed the [Create a Terraform Backend](create-backend.md) tutorial
- Familiarity with AWS VPC concepts
- OpenTofu/Terraform module basics

## Step 1: Understand Reusable Module Principles

Reusable modules in the monorepo follow these principles:

1. **Generic and Parameterized** - Work for any product/environment
2. **Secure by Default** - Security best practices built-in
3. **Consistent Naming** - Use resource-label for all resources
4. **Well-Documented** - Clear inputs, outputs, and examples
5. **Validated** - Input validation prevents misconfigurations
6. **Testable** - Can be deployed independently for testing

## Step 2: Choose Module Scope

Decide if your module should be:

**Shared Module** (`libs/platform/infra/terraform/modules/`):

- Generic, reusable across products
- Examples: VPC, ECS cluster, RDS database, ALB
- Requires comprehensive documentation

**Project Module** (`apps/<product>/infra/terraform/<project>/modules/`):

- Product-specific configuration
- Examples: Custom API service, specific worker configuration
- Can be simpler, less documentation needed

For this tutorial, we'll create a **shared VPC module**.

## Step 3: Create Module Directory Structure

Create the module directory:

```bash
cd libs/platform/infra/terraform/modules/
mkdir vpc
cd vpc
```

Create the standard Terraform files:

```bash
touch main.tf
touch variables.tf
touch outputs.tf
touch versions.tf
touch context.tf
touch README.md
```

Your structure:

```
libs/platform/infra/terraform/modules/vpc/
├── main.tf         # Resource definitions
├── variables.tf    # Input variables
├── outputs.tf      # Output values
├── versions.tf     # Provider requirements
├── context.tf      # Resource label integration
└── README.md       # Documentation
```

## Step 4: Define Provider Requirements

Create `versions.tf`:

```hcl
terraform {
  required_version = ">= 1.8.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "= 5.88.0"
    }
  }
}
```

**Note:**

- Use exact version pinning (`= 5.88.0`) for reproducibility
- This works with both OpenTofu and Terraform (syntax compatible)
- The monorepo uses OpenTofu 1.10.6, which is compatible with Terraform 1.8.0+

## Step 5: Integrate Resource Label

Create `context.tf` to integrate the naming convention:

```hcl
module "this" {
  source = "${var.project_paths.terraform}//modules/resource-label"

  enabled             = var.enabled
  organization        = var.organization
  product             = var.product
  application         = var.application
  component           = var.component
  attributes          = var.attributes
  region              = var.region
  environment         = var.environment
  delimiter           = var.delimiter
  tags                = var.tags
  label_order         = var.label_order
  regex_replace_chars = var.regex_replace_chars
  id_length_limit     = var.id_length_limit
  label_key_case      = var.label_key_case
  label_value_case    = var.label_value_case
  labels_as_tags      = var.labels_as_tags
  project_paths       = var.project_paths

  context = var.context
}

# Copy the full context variables from resource-label/variables.tf
# (All the variable definitions for organization, product, etc.)
# This pattern allows the module to be used with or without context

variable "context" {
  type = any
  default = {
    enabled             = true
    organization        = null
    product             = null
    application         = null
    component           = null
    attributes          = []
    region              = null
    environment         = null
    delimiter           = null
    tags                = {}
    regex_replace_chars = null
    label_order         = []
    id_length_limit     = null
    label_key_case      = null
    label_value_case    = null
    labels_as_tags      = ["unset"]
  }
  description = "Single object for setting entire context at once."
}

variable "enabled" {
  type        = bool
  default     = null
  description = "Set to false to prevent the module from creating any resources."
}

variable "organization" {
  type        = string
  default     = null
  description = "Organization name (e.g., 'sage')."
}

variable "product" {
  type        = string
  default     = null
  description = "Product name (e.g., 'bixarena')."
}

variable "application" {
  type        = string
  default     = null
  description = "Application name (e.g., 'api')."
}

variable "component" {
  type        = string
  default     = null
  description = "Component name (e.g., 'vpc', 'alb')."
}

variable "attributes" {
  type        = list(string)
  default     = []
  description = "Additional attributes to add to resource names."
}

variable "region" {
  type        = string
  default     = null
  description = "AWS region (e.g., 'us-east-1')."
}

variable "environment" {
  type        = string
  default     = null
  description = "Environment (e.g., 'dev', 'staging', 'prod')."
}

variable "delimiter" {
  type        = string
  default     = null
  description = "Delimiter between ID elements."
}

variable "tags" {
  type        = map(string)
  default     = {}
  description = "Additional tags for resources."
}

variable "label_order" {
  type        = list(string)
  default     = null
  description = "Order of labels in resource IDs."
}

variable "regex_replace_chars" {
  type        = string
  default     = null
  description = "Regex to remove characters from IDs."
}

variable "id_length_limit" {
  type        = number
  default     = null
  description = "Limit ID length (0 for unlimited)."
}

variable "label_key_case" {
  type        = string
  default     = null
  description = "Letter case for tag keys (lower/title/upper)."
}

variable "label_value_case" {
  type        = string
  default     = null
  description = "Letter case for label values (lower/title/upper/none)."
}

variable "labels_as_tags" {
  type        = set(string)
  default     = ["default"]
  description = "Labels to include as tags."
}

variable "project_paths" {
  type        = map(string)
  default     = null
  description = "Mapping of project paths."
}
```

## Step 6: Define Module Variables

Create `variables.tf` with module-specific inputs:

```hcl
# VPC Configuration
variable "cidr_block" {
  type        = string
  description = "CIDR block for the VPC (e.g., '10.0.0.0/16')."

  validation {
    condition     = can(cidrhost(var.cidr_block, 0))
    error_message = "Must be a valid CIDR block."
  }
}

variable "availability_zones" {
  type        = list(string)
  description = "List of availability zones for subnets (e.g., ['us-east-1a', 'us-east-1b'])."

  validation {
    condition     = length(var.availability_zones) >= 2
    error_message = "Must specify at least 2 availability zones for high availability."
  }
}

variable "public_subnet_cidrs" {
  type        = list(string)
  default     = []
  description = "CIDR blocks for public subnets (one per AZ)."
}

variable "private_subnet_cidrs" {
  type        = list(string)
  default     = []
  description = "CIDR blocks for private subnets (one per AZ)."
}

variable "enable_nat_gateway" {
  type        = bool
  default     = true
  description = "Enable NAT Gateway for private subnets."
}

variable "single_nat_gateway" {
  type        = bool
  default     = false
  description = "Use a single NAT Gateway instead of one per AZ (cost optimization)."
}

variable "enable_dns_hostnames" {
  type        = bool
  default     = true
  description = "Enable DNS hostnames in the VPC."
}

variable "enable_dns_support" {
  type        = bool
  default     = true
  description = "Enable DNS support in the VPC."
}

variable "enable_flow_logs" {
  type        = bool
  default     = true
  description = "Enable VPC Flow Logs for network traffic analysis."
}

variable "flow_logs_retention_days" {
  type        = number
  default     = 7
  description = "Number of days to retain flow logs."

  validation {
    condition     = contains([1, 3, 5, 7, 14, 30, 60, 90, 120, 150, 180, 365, 400, 545, 731, 1827, 3653], var.flow_logs_retention_days)
    error_message = "Must be a valid CloudWatch Logs retention period."
  }
}
```

## Step 7: Implement Main Resources

Create `main.tf`:

```hcl
locals {
  enabled = module.this.enabled

  # Calculate subnet CIDRs if not provided
  public_subnet_cidrs = length(var.public_subnet_cidrs) > 0 ? var.public_subnet_cidrs : [
    for i in range(length(var.availability_zones)) :
    cidrsubnet(var.cidr_block, 8, i)
  ]

  private_subnet_cidrs = length(var.private_subnet_cidrs) > 0 ? var.private_subnet_cidrs : [
    for i in range(length(var.availability_zones)) :
    cidrsubnet(var.cidr_block, 8, i + 100)
  ]

  nat_gateway_count = var.enable_nat_gateway ? (var.single_nat_gateway ? 1 : length(var.availability_zones)) : 0
}

# VPC Resource
resource "aws_vpc" "this" {
  count = local.enabled ? 1 : 0

  cidr_block           = var.cidr_block
  enable_dns_hostnames = var.enable_dns_hostnames
  enable_dns_support   = var.enable_dns_support

  tags = merge(
    module.this.tags,
    {
      Name = module.this.id
    }
  )
}

# Internet Gateway
resource "aws_internet_gateway" "this" {
  count = local.enabled ? 1 : 0

  vpc_id = one(aws_vpc.this[*].id)

  tags = merge(
    module.this.tags,
    {
      Name = "${module.this.id}-igw"
    }
  )
}

# Public Subnets
resource "aws_subnet" "public" {
  count = local.enabled ? length(var.availability_zones) : 0

  vpc_id                  = one(aws_vpc.this[*].id)
  cidr_block              = local.public_subnet_cidrs[count.index]
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = true

  tags = merge(
    module.this.tags,
    {
      Name = "${module.this.id}-public-${var.availability_zones[count.index]}"
      Type = "public"
    }
  )
}

# Private Subnets
resource "aws_subnet" "private" {
  count = local.enabled ? length(var.availability_zones) : 0

  vpc_id            = one(aws_vpc.this[*].id)
  cidr_block        = local.private_subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]

  tags = merge(
    module.this.tags,
    {
      Name = "${module.this.id}-private-${var.availability_zones[count.index]}"
      Type = "private"
    }
  )
}

# Elastic IPs for NAT Gateways
resource "aws_eip" "nat" {
  count = local.nat_gateway_count

  domain = "vpc"

  tags = merge(
    module.this.tags,
    {
      Name = "${module.this.id}-nat-${count.index + 1}"
    }
  )

  depends_on = [aws_internet_gateway.this]
}

# NAT Gateways
resource "aws_nat_gateway" "this" {
  count = local.nat_gateway_count

  allocation_id = aws_eip.nat[count.index].id
  subnet_id     = aws_subnet.public[count.index].id

  tags = merge(
    module.this.tags,
    {
      Name = "${module.this.id}-nat-${count.index + 1}"
    }
  )

  depends_on = [aws_internet_gateway.this]
}

# Public Route Table
resource "aws_route_table" "public" {
  count = local.enabled ? 1 : 0

  vpc_id = one(aws_vpc.this[*].id)

  tags = merge(
    module.this.tags,
    {
      Name = "${module.this.id}-public"
      Type = "public"
    }
  )
}

# Public Route to Internet Gateway
resource "aws_route" "public_internet_gateway" {
  count = local.enabled ? 1 : 0

  route_table_id         = one(aws_route_table.public[*].id)
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = one(aws_internet_gateway.this[*].id)
}

# Associate Public Subnets with Public Route Table
resource "aws_route_table_association" "public" {
  count = local.enabled ? length(var.availability_zones) : 0

  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = one(aws_route_table.public[*].id)
}

# Private Route Tables (one per NAT Gateway)
resource "aws_route_table" "private" {
  count = local.enabled ? local.nat_gateway_count : 0

  vpc_id = one(aws_vpc.this[*].id)

  tags = merge(
    module.this.tags,
    {
      Name = "${module.this.id}-private-${count.index + 1}"
      Type = "private"
    }
  )
}

# Private Routes to NAT Gateways
resource "aws_route" "private_nat_gateway" {
  count = local.enabled ? local.nat_gateway_count : 0

  route_table_id         = aws_route_table.private[count.index].id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.this[count.index].id
}

# Associate Private Subnets with Private Route Tables
resource "aws_route_table_association" "private" {
  count = local.enabled ? length(var.availability_zones) : 0

  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private[var.single_nat_gateway ? 0 : count.index].id
}

# VPC Flow Logs
resource "aws_flow_log" "this" {
  count = local.enabled && var.enable_flow_logs ? 1 : 0

  vpc_id          = one(aws_vpc.this[*].id)
  traffic_type    = "ALL"
  iam_role_arn    = one(aws_iam_role.flow_logs[*].arn)
  log_destination = one(aws_cloudwatch_log_group.flow_logs[*].arn)

  tags = merge(
    module.this.tags,
    {
      Name = "${module.this.id}-flow-logs"
    }
  )
}

# CloudWatch Log Group for Flow Logs
resource "aws_cloudwatch_log_group" "flow_logs" {
  count = local.enabled && var.enable_flow_logs ? 1 : 0

  name              = "/aws/vpc/${module.this.id}"
  retention_in_days = var.flow_logs_retention_days

  tags = module.this.tags
}

# IAM Role for Flow Logs
resource "aws_iam_role" "flow_logs" {
  count = local.enabled && var.enable_flow_logs ? 1 : 0

  name = "${module.this.id}-flow-logs"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "vpc-flow-logs.amazonaws.com"
      }
    }]
  })

  tags = module.this.tags
}

# IAM Policy for Flow Logs
resource "aws_iam_role_policy" "flow_logs" {
  count = local.enabled && var.enable_flow_logs ? 1 : 0

  name = "${module.this.id}-flow-logs"
  role = one(aws_iam_role.flow_logs[*].id)

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "logs:DescribeLogGroups",
        "logs:DescribeLogStreams"
      ]
      Effect = "Allow"
      Resource = "*"
    }]
  })
}
```

## Step 8: Define Outputs

Create `outputs.tf`:

```hcl
output "vpc_id" {
  value       = one(aws_vpc.this[*].id)
  description = "VPC ID."
}

output "vpc_arn" {
  value       = one(aws_vpc.this[*].arn)
  description = "VPC ARN."
}

output "vpc_cidr_block" {
  value       = one(aws_vpc.this[*].cidr_block)
  description = "VPC CIDR block."
}

output "public_subnet_ids" {
  value       = aws_subnet.public[*].id
  description = "List of public subnet IDs."
}

output "private_subnet_ids" {
  value       = aws_subnet.private[*].id
  description = "List of private subnet IDs."
}

output "public_subnet_cidrs" {
  value       = aws_subnet.public[*].cidr_block
  description = "List of public subnet CIDR blocks."
}

output "private_subnet_cidrs" {
  value       = aws_subnet.private[*].cidr_block
  description = "List of private subnet CIDR blocks."
}

output "nat_gateway_ids" {
  value       = aws_nat_gateway.this[*].id
  description = "List of NAT Gateway IDs."
}

output "internet_gateway_id" {
  value       = one(aws_internet_gateway.this[*].id)
  description = "Internet Gateway ID."
}

output "public_route_table_id" {
  value       = one(aws_route_table.public[*].id)
  description = "Public route table ID."
}

output "private_route_table_ids" {
  value       = aws_route_table.private[*].id
  description = "List of private route table IDs."
}
```

## Step 9: Document the Module

Create `README.md`:

````markdown
# VPC Terraform Module

## Overview

This module creates a production-ready AWS VPC with public and private subnets across multiple availability zones, NAT gateways, and optional VPC Flow Logs.

## Features

- Multi-AZ architecture for high availability
- Public and private subnets
- NAT Gateways for private subnet internet access
- Internet Gateway for public subnets
- VPC Flow Logs for network monitoring
- Consistent naming via resource-label
- Fully configurable and validated inputs

## Usage

```hcl
module "vpc" {
  source = "${path_to_terraform}//modules/vpc"

  # Context (inherited from project)
  organization = "sage"
  product      = "bixarena"
  application  = "api"
  component    = "network"
  region       = "us-east-1"
  environment  = "prod"

  # VPC Configuration
  cidr_block         = "10.0.0.0/16"
  availability_zones = ["us-east-1a", "us-east-1b", "us-east-1c"]

  # Optional: Override calculated subnet CIDRs
  # public_subnet_cidrs  = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  # private_subnet_cidrs = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]

  # NAT Gateway Configuration
  enable_nat_gateway = true
  single_nat_gateway = false  # Use one NAT per AZ for HA

  # Flow Logs
  enable_flow_logs           = true
  flow_logs_retention_days   = 7
}
```
````

## Inputs

| Name                 | Type         | Default    | Description                   |
| -------------------- | ------------ | ---------- | ----------------------------- |
| cidr_block           | string       | -          | CIDR block for the VPC        |
| availability_zones   | list(string) | -          | List of AZs (minimum 2)       |
| public_subnet_cidrs  | list(string) | calculated | Public subnet CIDRs           |
| private_subnet_cidrs | list(string) | calculated | Private subnet CIDRs          |
| enable_nat_gateway   | bool         | true       | Enable NAT Gateway            |
| single_nat_gateway   | bool         | false      | Use single NAT (cost savings) |
| enable_flow_logs     | bool         | true       | Enable VPC Flow Logs          |

## Outputs

| Name               | Description        |
| ------------------ | ------------------ |
| vpc_id             | VPC ID             |
| public_subnet_ids  | Public subnet IDs  |
| private_subnet_ids | Private subnet IDs |
| nat_gateway_ids    | NAT Gateway IDs    |

## Examples

See [examples](../../../../apps/bixarena/infra/terraform/network) for usage.

````

## Step 10: Test the Module

Create a test Terragrunt module to verify:

```bash
cd apps/bixarena/infra/terraform/
mkdir -p test-vpc/network
cd test-vpc
````

Create `test-vpc/network/terragrunt.hcl`:

```hcl
include "project" {
  path = "../../../terraform-backend/project.hcl"  # Reuse existing project
}

terraform {
  source = "${include.project.inputs.project_paths.terraform}//modules/vpc"
}

inputs = {
  component          = "test-network"
  cidr_block         = "10.100.0.0/16"
  availability_zones = ["us-east-1a", "us-east-1b"]
  single_nat_gateway = true  # Cost optimization for testing
}
```

Test the module:

```bash
cd network
terragrunt init
terragrunt plan
# terragrunt apply  # Only if you want to create real resources
```

## Step 11: Add to Version Control

```bash
cd /workspaces/sage-monorepo
git add libs/platform/infra/terraform/modules/vpc/
git commit -m "feat(terraform): add reusable VPC module"
```

## Best Practices Checklist

- ✅ Input validation for all critical parameters
- ✅ Sensible defaults for optional parameters
- ✅ Integration with resource-label for naming
- ✅ Security best practices (private subnets, flow logs)
- ✅ Cost optimization options (single NAT gateway)
- ✅ Comprehensive outputs for downstream modules
- ✅ Clear documentation with examples
- ✅ Conditional resource creation (`count` with `enabled`)

## Common Patterns

### Pattern 1: Conditional Resources

```hcl
resource "aws_nat_gateway" "this" {
  count = var.enable_nat_gateway ? length(var.availability_zones) : 0
  # ...
}
```

### Pattern 2: Calculated Values

```hcl
locals {
  public_subnet_cidrs = length(var.public_subnet_cidrs) > 0 ? var.public_subnet_cidrs : [
    for i in range(length(var.availability_zones)) :
    cidrsubnet(var.cidr_block, 8, i)
  ]
}
```

### Pattern 3: Dynamic Tagging

```hcl
tags = merge(
  module.this.tags,  # Standard tags from resource-label
  {
    Name = "${module.this.id}-custom"  # Resource-specific name
  }
)
```

## Next Steps

- Create additional shared modules (ALB, ECS, RDS)
- Add automated testing (Terratest)
- Document module in team wiki
- Create example implementations

## Related Resources

- [Terraform Infrastructure Architecture](../../architecture/terraform-infrastructure.md)
- [AWS VPC Documentation](https://docs.aws.amazon.com/vpc/)

## Example Code in the Monorepo

- **Resource Label Module**: `libs/platform/infra/terraform/modules/resource-label/`
- **Terraform Backend Module**: `libs/platform/infra/terraform/modules/terraform-s3-backend/` - Another reusable module example
