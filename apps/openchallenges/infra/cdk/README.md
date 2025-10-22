# OpenChallenges Infra CDK

AWS CDK infrastructure as code for OpenChallenges. This project defines and manages the cloud resources required to deploy and operate the OpenChallenges application stack.

## Project Structure

TODO

## Quick Start

### Prerequisites

- AWS CLI configured with appropriate credentials
- Python 3.11 or later
- Node.js and pnpm (for Nx commands)

### Environment Configuration

The project uses environment variables for configuration. Nx automatically loads variables from `.env` files based on the target configuration.

#### Development Environment

For the development environment, you must set `DEVELOPER_NAME` to create unique resource names:

```bash
# .env.dev
DEVELOPER_NAME=jsmith
```

Resources will be named with the pattern: `openchallenges-dev-{developer-name}-{resource}`

#### Stage and Production Environments

No developer name is required for stage and production. Resources use the pattern: `openchallenges-{env}-{resource}`

### Synthesize CloudFormation Templates

Generate CloudFormation templates from the CDK code:

```bash
# Development (requires DEVELOPER_NAME in .env.dev)
nx synth openchallenges-infra-cdk:dev

# Staging
nx synth openchallenges-infra-cdk:stage

# Production
nx synth openchallenges-infra-cdk:prod
```

### List Stacks

View all available CDK stacks for an environment:

```bash
nx run openchallenges-infra-cdk:ls:dev
nx run openchallenges-infra-cdk:ls:stage
nx run openchallenges-infra-cdk:ls:prod
```

### Deploy Infrastructure

Deploy the CDK stacks to AWS:

```bash
# Development
nx deploy openchallenges-infra-cdk:dev

# Staging
nx deploy openchallenges-infra-cdk:stage

# Production
nx deploy openchallenges-infra-cdk:prod
```

### Testing the Application Load Balancer

After deploying, you can test the ALB health endpoint:

#### 1. Get the ALB DNS Name

After deployment, the ALB DNS name will be displayed in the CloudFormation outputs:

```bash
# Look for the "HealthCheckUrl" output
nx deploy openchallenges-infra-cdk:dev
```

Or retrieve it from the CloudFormation stack:

```bash
# Using AWS CLI
aws cloudformation describe-stacks \
  --stack-name openchallenges-dev-{your-name}-alb \
  --query 'Stacks[0].Outputs[?OutputKey==`LoadBalancerDnsName`].OutputValue' \
  --output text
```

#### 2. Test the `/health` Endpoint

**Using HTTP (no certificate configured):**

```bash
# From your terminal
curl http://<alb-dns-name>/health

# Or open in browser
# http://<alb-dns-name>/health
```

**Using HTTPS (with certificate configured):**

```bash
# From your terminal
curl https://<alb-dns-name>/health

# Or open in browser
# https://<alb-dns-name>/health
```

**Expected Response:**

```json
{
  "status": "healthy",
  "service": "openchallenges-alb"
}
```

#### 3. Test Other Paths

Since no backend services are configured yet, other paths will return 503:

```bash
curl http://<alb-dns-name>/api/v1/challenges
# Returns: {"error":"Service Unavailable","message":"No backend services configured"}
```

### Destroy Infrastructure

Remove all deployed resources:

```bash
# Development
nx destroy openchallenges-infra-cdk:dev

# Staging
nx destroy openchallenges-infra-cdk:stage

# Production
nx destroy openchallenges-infra-cdk:prod
```

## Developer Workflow

### Setting Up Your Development Stack

1. **Set your developer name** in `.env.dev`:

   ```bash
   echo "DEVELOPER_NAME=your-github-username" >> .env.dev
   ```

2. **Configure AWS credentials** for the development account:

   ```bash
   aws configure --profile openchallenges-dev
   ```

3. **Bootstrap CDK** (first-time only):

   ```bash
   ENV=dev cdk bootstrap --profile openchallenges-dev
   ```

4. **Synthesize and deploy**:
   ```bash
   nx synth openchallenges-infra-cdk:dev
   nx deploy openchallenges-infra-cdk:dev
   ```

### Making Changes

1. Make changes to constructs or stacks in the `shared/` directory
2. Test synthesis: `nx synth openchallenges-infra-cdk:dev`
3. Review diff: `nx diff openchallenges-infra-cdk:dev`
4. Deploy changes: `nx deploy openchallenges-infra-cdk:dev`

### Cleaning Up

When you're done with your development stack, destroy it to avoid charges:

```bash
nx destroy openchallenges-infra-cdk:dev
```

**Note on GuardDuty**: The CDK app now manages the GuardDuty VPC endpoint to ensure clean stack deletion. In earlier versions, GuardDuty created AWS-managed resources (VPC endpoints and security groups) outside of CloudFormation, which blocked VPC deletion. These are now explicitly managed by the CDK stack.

### Troubleshooting Stack Deletion

When you're done testing, destroy your development stack:

```bash
nx destroy openchallenges-infra-cdk:dev
```

## Phase 0: Minimal Infrastructure

This initial phase deploys a single S3 bucket to validate the multi-environment deployment workflow.

### Resources Deployed

- **Image Bucket**: S3 bucket for storing images (used by Thumbor service)
  - Encryption: S3-managed (AES256)
  - Public access: Blocked
  - Versioning: Disabled
  - Naming:
    - Dev: `openchallenges-dev-{developer-name}-img`
    - Stage/Prod: `openchallenges-{env}-img`

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
  - CIDR block: 10.0.0.0/16 (configurable via `VPC_CIDR` environment variable)
  - Public subnets (/24): For ALB and NAT Gateway(s)
  - Private subnets (/24): For ECS tasks and backend services
  - DNS support: Enabled

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

### Environment Configuration

Add these optional variables to your `.env` files:

```bash
# Optional: Customize VPC CIDR block (default: 10.0.0.0/16)
VPC_CIDR=10.0.0.0/16

# Optional: Number of Availability Zones (default: 2)
# Stage/prod environments use one NAT Gateway per AZ for high availability
# Dev always uses 1 NAT Gateway regardless of this setting
MAX_AZS=2

# Optional: Enable HTTPS with ACM certificate
# When set, ALB will listen on port 443 and redirect HTTP to HTTPS
CERTIFICATE_ARN=arn:aws:acm:region:account:certificate/xxxxxxxxx
```

**Development**: HTTPS is optional. Without a certificate, the ALB will only listen on port 80 (HTTP).

**Production**: HTTPS should be enabled for security. Set `CERTIFICATE_ARN` to your ACM certificate.

### Cost Estimates

Monthly costs for the networking and ALB infrastructure by environment:

#### Development Environment

| Resource                  | Cost              | Details                                           |
| ------------------------- | ----------------- | ------------------------------------------------- |
| VPC                       | $0                | No charge for VPC itself                          |
| NAT Gateway (1)           | ~$32.50           | $0.045/hour (~$32.40) + data transfer (~$0.10/GB) |
| Application Load Balancer | ~$18              | $0.0225/hour (~$16.20) + LCU charges (~$1.80)     |
| S3 Bucket (from Phase 0)  | <$1               | Minimal storage and requests                      |
| **Total**                 | **~$51.50/month** | Estimated for dev environment with low traffic    |

#### Stage/Production Environments

| Resource                  | Cost                | Details                                                  |
| ------------------------- | ------------------- | -------------------------------------------------------- |
| VPC                       | $0                  | No charge for VPC itself                                 |
| NAT Gateways              | ~$32.50 × N AZs     | Default: 2 AZs = ~$65/month; scales with MAX_AZS setting |
| Application Load Balancer | ~$25-50             | Higher traffic = higher LCU charges                      |
| S3 Bucket (from Phase 0)  | $1-5                | More storage and requests                                |
| **Total (2 AZs)**         | **~$91-120/month**  | Estimated for stage/prod with moderate traffic           |
| **Total (3 AZs)**         | **~$124-153/month** | Add ~$32.50/month for each additional AZ                 |

**Notes**:

- NAT Gateway data transfer charges vary based on usage (estimated at ~2GB/month for dev, higher for stage/prod)
- ALB LCU (Load Balancer Capacity Unit) charges scale with traffic, connections, and rule evaluations
- Production costs can be higher depending on actual traffic patterns
- Stage/prod environments use one NAT Gateway per Availability Zone (configurable via `MAX_AZS`)
- Each additional AZ adds approximately $32.50/month in NAT Gateway costs
- These estimates do NOT include future ECS services, databases, or other resources

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
nx test openchallenges-infra-cdk
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

Pattern: `openchallenges-dev-{developer-name}-{resource-type}`

Example: `openchallenges-dev-jsmith-img`

### Stage and Production

Pattern: `openchallenges-{env}-{resource-type}`

Examples:

- `openchallenges-stage-img`
- `openchallenges-prod-img`

## Troubleshooting

### "ENV environment variable is required"

Make sure you're running commands with a configuration:

```bash
nx synth openchallenges-infra-cdk:dev  # Not just 'openchallenges-infra-cdk'
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

### Environment-Specific Commands

You can target specific environments by providing a configuration:

```bash
# Development (default)
nx synth openchallenges-infra-cdk
nx ls openchallenges-infra-cdk
nx deploy openchallenges-infra-cdk

# Staging
nx synth openchallenges-infra-cdk:stage
nx ls openchallenges-infra-cdk:stage
nx deploy openchallenges-infra-cdk:stage

# Production
nx synth openchallenges-infra-cdk:prod
nx ls openchallenges-infra-cdk:prod
nx deploy openchallenges-infra-cdk:prod
```

### Environment Variable Precedence

Environment variables are loaded by Nx with the following precedence (highest to lowest):

1. Environment-specific file (`.env.dev`, `.env.stage`, or `.env.prod`)
2. Base environment file (`.env`)

Variables defined in environment-specific files will override any matching variables in the base `.env` file.
