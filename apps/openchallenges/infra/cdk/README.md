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

## Testing

### Run All Tests

```bash
nx test openchallenges-infra-cdk
```

### Run Unit Tests Only

```bash
nx test openchallenges-infra-cdk --testPathPattern=unit
```

### Run Integration Tests Only

```bash
nx test openchallenges-infra-cdk --testPathPattern=integration
```

## Environment Variable Reference

### Required for All Environments

- `ENV`: Environment name (`dev`, `stage`, or `prod`)

### Required for Development

- `DEVELOPER_NAME`: Your identifier for resource naming (alphanumeric, hyphens, underscores only)

### Optional

- `AWS_REGION`: AWS region for deployment (defaults to CDK's default region)
- `AWS_ACCOUNT`: AWS account ID (defaults to CDK's default account)

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
