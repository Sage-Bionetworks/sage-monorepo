# BixArena Infra CDK

AWS CDK infrastructure as code for BixArena. This project defines and manages the cloud resources required to deploy and operate the BixArena application stack.

## Project Structure

TODO

## Quick Start

### Prerequisites

- AWS CLI installed
- Python 3.11 or later
- Node.js and pnpm (for Nx commands)

### Setup Instructions

#### 1. Configure AWS SSO

Create the AWS config file `~/.aws/config` (the `~/.aws` directory may not exist yet):

```bash
mkdir -p ~/.aws
cat > ~/.aws/config << 'EOF'
[sso-session org-sagebase]
sso_start_url = https://d-906769aa66.awsapps.com/start
sso_region = us-east-1
sso_registration_scopes = sso:account:access

[profile bixarena-dev-Developer]
sso_session = org-sagebase
sso_account_id = 864020296088
sso_role_name = Developer
region = us-east-1
output = json
cli_pager =
EOF
```

Then login with AWS SSO:

```bash
aws sso login --profile bixarena-dev-Developer
```

#### 2. Configure Environment Variables

Create the configuration files:

```bash
nx create-config bixarena-infra-cdk
```

This will generate `.env`, `.env.dev`, `.env.stage`, and `.env.prod` files with default values.

Then edit `.env.dev` and set your developer name:

```bash
# Open .env.dev in your editor and set:
DEVELOPER_NAME=your-github-username
AWS_PROFILE=bixarena-dev-Developer
```

Your resources will be named: `bixarena-dev-{your-name}-{resource}`

### Common Commands

**Synthesize CloudFormation templates:**

```bash
nx run bixarena-infra-cdk:synth:dev
```

**List stacks:**

```bash
nx run bixarena-infra-cdk:ls:dev
```

**Deploy infrastructure:**

```bash
nx run bixarena-infra-cdk:deploy:dev
```

**Destroy infrastructure:**

```bash
nx run bixarena-infra-cdk:destroy:dev
```

### Testing the Application Load Balancer

After deploying, test the ALB health endpoint:

```bash
# Get the ALB DNS name from deployment outputs (look for "HealthCheckUrl")
# Then test the health endpoint:
curl http://<alb-dns-name>/health
```

**Expected Response:**

```json
{
  "status": "healthy",
  "service": "bixarena-alb"
}
```

## Developer Workflow

### Making Changes

1. Make changes to constructs or stacks in the `shared/` directory
2. Test synthesis: `nx run bixarena-infra-cdk:synth:dev`
3. Review diff: `nx run bixarena-infra-cdk:diff:dev`
4. Deploy changes: `nx run bixarena-infra-cdk:deploy:dev`

### Cleaning Up

When you're done with your development stack, destroy it to avoid charges:

```bash
nx run bixarena-infra-cdk:destroy:dev
```

**Note on GuardDuty**: The CDK app manages the GuardDuty VPC endpoint to ensure clean stack deletion and prevent orphaned AWS-managed resources.

### Deploying to Stage/Prod

To deploy to staging or production environments, use the corresponding configuration:

```bash
# Staging
nx run bixarena-infra-cdk:synth:stage
nx run bixarena-infra-cdk:deploy:stage

# Production
nx run bixarena-infra-cdk:synth:prod
nx run bixarena-infra-cdk:deploy:prod
```

**Note**: Stage and production don't require a `DEVELOPER_NAME`. Resources use the pattern: `bixarena-{env}-{resource}`

## Phase 0: Minimal Infrastructure

This initial phase deploys a single S3 bucket to validate the multi-environment deployment workflow.

### Resources Deployed

- **Image Bucket**: S3 bucket for storing images (used by Thumbor service)
  - Encryption: S3-managed (AES256)
  - Public access: Blocked
  - Versioning: Disabled
  - Naming:
    - Dev: `bixarena-dev-{developer-name}-img`
    - Stage/Prod: `bixarena-{env}-img`

### CloudFormation Outputs

The bucket stack exports the following values:

- `ImageBucketName`: Name of the S3 bucket
- `ImageBucketArn`: ARN of the S3 bucket

These outputs can be referenced by other stacks or services.

## Phase 1: Networking and Load Balancer

This phase deploys the core networking infrastructure and application load balancer.

### Resources Deployed

#### VPC (Virtual Private Cloud)

- **Configuration**:

  - 2 Availability Zones for redundancy
  - CIDR block: Configurable via `VPC_CIDR` environment variable
    - Dev: `10.254.172.0/24`
    - Stage: `10.254.173.0/24`
    - Prod: `10.254.174.0/24`
  - Subnets: Auto-calculated to evenly divide the VPC CIDR
    - Public subnets: For ALB and NAT Gateway(s) (1 per AZ)
    - Private subnets: For ECS tasks and backend services (1 per AZ)
  - DNS support: Enabled

> [!NOTE]
> It is important that every VPC CIDR be a unique value within our AWS organization. That will allow
> us to easily setup private access via VPN and cross account VPC routing if needed. Check our
> [wiki](https://sagebionetworks.jira.com/wiki/spaces/IT/pages/2850586648/Setup+AWS+VPC) for
> information on how to obtain a unique CIDR.

- **NAT Gateway Configuration** (Environment-Specific):

  - **Development**: 1 NAT Gateway

    - Cost-optimized: ~$32.50/month
    - Trade-off: Single point of failure for internet access from private subnets
    - Suitable for dev/test environments where cost is prioritized over uptime

  - **Stage/Production**: NAT Gateways = Number of Availability Zones (default: 2)
    - High availability: ~$32.50/month per NAT Gateway
    - One NAT Gateway per AZ ensures redundancy
    - If one NAT or AZ fails, the other continues to provide internet access
    - Recommended for production workloads requiring high uptime
    - Configurable via `MAX_AZS` environment variable (scales automatically)

#### Application Load Balancer (ALB)

- **Configuration**:

  - Internet-facing: Accessible from the public internet
  - HTTP support: Always enabled on port 80
  - HTTPS support: Optional, enabled when `CERTIFICATE_ARN` is provided
  - Security groups: Allow HTTP (80) and HTTPS (443) from anywhere (0.0.0.0/0)
  - Health check endpoint: `/health` returns 200 OK with JSON response
  - Default action: Returns 503 "Service Unavailable" for unconfigured routes
  - HTTP to HTTPS redirect: Automatically enabled when certificate is configured

- **Listener Rules**:
  - Priority 1: `/health` → Fixed response (200 OK, JSON)
  - Default: All other paths → 503 Service Unavailable (until backends are configured)

### CloudFormation Outputs

#### VPC Stack

- `VpcId`: ID of the VPC
- `PublicSubnetIds`: Comma-separated list of public subnet IDs
- `PrivateSubnetIds`: Comma-separated list of private subnet IDs

#### ALB Stack

- `LoadBalancerArn`: ARN of the Application Load Balancer
- `LoadBalancerDnsName`: DNS name to access the ALB
- `HealthCheckUrl`: Full URL to the health check endpoint

### Architecture Notes

This infrastructure replaces the Caddy reverse proxy used in the original deployment:

- **Original**: ALB → Caddy container → Backend services
- **New**: ALB → Backend services (direct)

The ALB provides the same capabilities as Caddy:

- Path-based routing (via listener rules)
- HTTPS termination
- Health checks
- HTTP to HTTPS redirection

Benefits of ALB-only approach:

- One less service to manage and monitor
- Better AWS integration (CloudWatch metrics, access logs, WAF)
- Higher availability (AWS-managed service)
- Cost-effective (eliminates ECS task for Caddy)

## Testing

### Run All Tests

```bash
nx test bixarena-infra-cdk
```

## Environment Variable Reference

### Required for All Environments

- `AWS_PROFILE`: Name of the AWS profile
- `ENV`: Environment name (`dev`, `stage`, or `prod`)

### Required for Development

- `DEVELOPER_NAME`: Your identifier for resource naming (alphanumeric, hyphens, underscores only)

## Environment Variable Precedence

Environment variables are loaded by Nx with the following precedence (highest to lowest):

1. Environment-specific file (`.env.dev`, `.env.stage`, or `.env.prod`)
2. Base environment file (`.env`)

Variables defined in environment-specific files will override any matching variables in the base `.env` file.

## Resource Naming Convention

Resources follow a consistent naming pattern:

### Development

Pattern: `bixarena-dev-{developer-name}-{resource-type}`

Example: `bixarena-dev-jsmith-img`

### Stage and Production

Pattern: `bixarena-{env}-{resource-type}`

Examples:

- `bixarena-stage-img`
- `bixarena-prod-img`

## Troubleshooting

### "ENV environment variable is required"

Make sure you're running commands with a configuration:

```bash
nx run bixarena-infra-cdk:synth:dev  # Not just 'bixarena-infra-cdk'
```

### "DEVELOPER_NAME environment variable is required for dev"

Add your developer name to `.env.dev`:

```bash
echo "DEVELOPER_NAME=your-name" >> .env.dev
```

### "Invalid DEVELOPER_NAME"

Developer names must contain only alphanumeric characters, hyphens, and underscores.

### Stack name too long

If your developer name is very long, CDK may reject the stack name. Try using a shorter name or abbreviation.
