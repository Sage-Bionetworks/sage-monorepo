# OpenChallenges Infra CDK

AWS CDK infrastructure as code for OpenChallenges. This project defines and manages the cloud resources required to deploy and operate the OpenChallenges application stack.

## Project Structure

```
openchallenges_infra_cdk/
├── dev/                    # Development environment app
│   └── app.py             # Main CDK app for dev
├── shared/                # Shared infrastructure components
│   ├── constructs/        # Reusable CDK constructs
│   │   └── fargate_service_construct.py  # ECS Fargate service construct
│   ├── stacks/            # CloudFormation stacks
│   │   ├── alb_stack.py              # Application Load Balancer
│   │   ├── api_gateway_stack.py      # Spring Cloud Gateway
│   │   ├── bucket_stack.py           # S3 buckets
│   │   ├── image_service_stack.py    # Image service (generates Thumbor URLs)
│   │   ├── thumbor_stack.py          # Thumbor image processing
│   │   ├── vpc_stack.py              # VPC and networking
│   │   └── web_stack.py              # Angular web application
│   └── utils.py           # Utility functions
└── tests/                 # Unit tests
```

## Quick Start

### Prerequisites

- AWS CLI installed and configured
- Python 3.11 or later
- Node.js and pnpm (for Nx commands)

### AWS Account Access

OpenChallenges uses AWS accounts with two IAM roles:

- **Developer**: Standard role for deploying and managing your development resources
- **Administrator**: Elevated role for administrative tasks (typically used by IT team)

Most developers will use the **Developer** role for day-to-day work. The Administrator role is only needed for initial account setup tasks like CDK bootstrapping (usually done by IT).

**AWS Profile Naming Convention:**

Use this naming pattern for your AWS CLI profiles:

```
openchallenges-{account}-{role}
```

Examples:

- `openchallenges-dev-Developer` (development account)
- `openchallenges-dev-Administrator` (development account, admin role)
- `openchallenges-prod-Developer` (production account, used for both stage and prod environments)
- `openchallenges-prod-Administrator` (production account, admin role)

### Configuring AWS CLI with SSO

OpenChallenges uses AWS SSO for authentication. Add your profiles to `~/.aws/config`:

```bash
# Edit ~/.aws/config and add:
[sso-session org-sagebase]
sso_start_url = https://d-906769aa66.awsapps.com/start
sso_region = us-east-1
sso_registration_scopes = sso:account:access

[profile openchallenges-dev-Developer]
sso_session = org-sagebase
sso_account_id = 221082174873
sso_role_name = Developer
region = us-east-1
output = json
cli_pager =

[profile openchallenges-dev-Administrator]
sso_session = org-sagebase
sso_account_id = 221082174873
sso_role_name = Administrator
region = us-east-1
output = json
cli_pager =

[profile openchallenges-prod-Developer]
sso_session = org-sagebase
sso_account_id = 528757786185
sso_role_name = Developer
region = us-east-1
output = json
cli_pager =

[profile openchallenges-prod-Administrator]
sso_session = org-sagebase
sso_account_id = 528757786185
sso_role_name = Administrator
region = us-east-1
output = json
cli_pager =
```

**Note**: Replace `YOUR_PROD_ACCOUNT_ID` with your production AWS account ID.

Then login with SSO:

```bash
# Login to start your SSO session
aws sso login --profile openchallenges-dev-Developer

# This will open a browser for authentication
# You only need to login once - the session works for all profiles using the same sso-session
```

### Environment Configuration

The project uses environment variables for configuration. Nx automatically loads variables from `.env` and `.env.{environment}` files based on the target configuration, which correspond to different environments (dev, stage, and prod).

Create the configuration files from their templates:

```bash
nx create-config openchallenges-infra-cdk
```

#### Development Environment

For the development environment, you must set `DEVELOPER_NAME` in `.env.dev`:

```bash
# .env.dev
DEVELOPER_NAME=jsmith
```

Resources will be named with the pattern: `openchallenges-dev-{developer-name}-{resource}`

#### Stage and Production Environments

Stage and production deployments both use the **production AWS account**. No developer name is required. Resources use the pattern: `openchallenges-{environment}-{resource}`

Set the AWS profile to use the production account:

```bash
# .env.stage (uses production account)
AWS_PROFILE=openchallenges-prod-Developer

# .env.prod (uses production account)
AWS_PROFILE=openchallenges-prod-Developer
```

**Note**: Stage and prod deployments share the same AWS account but are isolated by resource naming and by VPC configuration.

### Using Local Docker Images (Development Only)

When developing microservices or containerized components, you can test local Docker images without publishing them to the container registry:

**1. Enable local images in `.env.dev`:**

```bash
# .env.dev
USE_LOCAL_IMAGES=true
```

**2. Export your Docker image as a tarball:**

```bash
# After making changes to a service, export the image
nx run openchallenges-api-gateway:export-image-tarball
nx run openchallenges-challenge-service:export-image-tarball
nx run openchallenges-organization-service:export-image-tarball
# ... etc for any service you've modified
```

**3. Deploy with CDK:**

```bash
# The CDK app will automatically upload the tarball and deploy it
nx run openchallenges-infra-cdk:deploy:dev
```

**Important Notes:**

- **Development only**: Use this for dev environment testing. Stage and prod should use published images from the Sage monorepo CI/CD pipeline.
- **Large assets**: Image tarballs can be hundreds of MB, increasing deployment time
- **Stale images**: Remember to re-export tarballs after making code changes
- **Default behavior**: When `USE_LOCAL_IMAGES=false` (default), images are pulled from `ghcr.io/sage-bionetworks`

### Image Service Placeholder Mode

The image service can operate in placeholder mode during development or initial deployment when images haven't been uploaded to S3 yet.

**Configure placeholder mode in `.env.dev`, `.env.stage`, or `.env.prod`:**

```bash
# .env.dev (or .env.stage, .env.prod)

# Enable placeholder mode to return URLs for placeholder images
# Set to "false" once real images have been uploaded to S3
IMAGE_SERVICE_PLACEHOLDER_ENABLED=true
```

**Behavior:**

- When `IMAGE_SERVICE_PLACEHOLDER_ENABLED=true`: Image service returns URLs pointing to placeholder images (e.g., via placeholder.com). No images need to exist in the S3 bucket.
- When `IMAGE_SERVICE_PLACEHOLDER_ENABLED=false`: Image service returns URLs pointing to real images stored in S3 via Thumbor.
- **Default**: `true` (placeholder mode enabled)

**Migrating Images to Your Environment:**

Once you're ready to use real images, you can copy them from a reference bucket or restore from a backup:

```bash
# Option 1: Copy from a reference S3 bucket (e.g., production environment)
# ============================================================================

# 1. Download images from reference bucket to local directory
AWS_PROFILE=openchallenges-prod-Administrator \
  aws s3 sync s3://openchallenges-prod-img/ /tmp/openchallenges-img-backup/ \
  --no-progress

# 2. Find your target bucket name
AWS_PROFILE=openchallenges-dev-Developer aws s3 ls | grep imagebucket
# Example output: openchallenges-dev-jsmith-imagebucketc726ca21-abc123xyz

# 3. Upload images to your environment's S3 bucket
AWS_PROFILE=openchallenges-dev-Administrator \
  aws s3 sync /tmp/openchallenges-img-backup/ \
  s3://openchallenges-dev-jsmith-imagebucketc726ca21-abc123xyz/ \
  --no-progress


# Option 2: Restore from a Synapse backup
# ============================================================================

# 1. Download backup from Synapse (requires Synapse credentials)
synapse get syn12345678 --downloadLocation /tmp/openchallenges-img-backup/

# 2. Find your target bucket name
AWS_PROFILE=openchallenges-dev-Developer aws s3 ls | grep imagebucket

# 3. Upload to your environment's S3 bucket
AWS_PROFILE=openchallenges-dev-Administrator \
  aws s3 sync /tmp/openchallenges-img-backup/ \
  s3://openchallenges-dev-jsmith-imagebucketc726ca21-abc123xyz/ \
  --no-progress


# After uploading images, disable placeholder mode
# ============================================================================

# 1. Update environment configuration
# Edit .env.dev (or .env.stage, .env.prod) and set:
IMAGE_SERVICE_PLACEHOLDER_ENABLED=false

# 2. Redeploy the image service to pick up the new configuration
nx run openchallenges-infra-cdk:deploy:dev
```

**Note:** The S3 bucket name includes a random hash suffix generated by CloudFormation. Use the `aws s3 ls` command shown above to find the exact bucket name for your environment.

### Synthesize CloudFormation Templates

Generate CloudFormation templates from the CDK code:

```bash
# Development (requires DEVELOPER_NAME in .env.dev)
nx run openchallenges-infra-cdk:synth:dev

# Staging
nx run openchallenges-infra-cdk:synth:stage

# Production
nx run openchallenges-infra-cdk:synth:prod
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
nx run openchallenges-infra-cdk:deploy:dev
nx run openchallenges-infra-cdk:deploy:stage
nx run openchallenges-infra-cdk:deploy:prod
```

### Destroy Infrastructure

Remove all deployed resources:

```bash
nx run openchallenges-infra-cdk:destroy:dev
nx run openchallenges-infra-cdk:destroy:stage
nx run openchallenges-infra-cdk:destroy:prod
```

## Accessing Private Resources

The PostgreSQL database and OpenSearch are deployed in private subnets. Access them via port forwarding through the bastion ECS service using the provided scripts.

**Note:** AWS Session Manager plugin is required and is already installed in the dev container.

### PostgreSQL Database

```bash
# Start database tunnel
cd apps/openchallenges/infra/cdk
./tools/start-db-tunnel.sh dev {developer-name}

# The script will display the connection string:
# postgresql://postgres:password@localhost:5432/openchallenges
```

Use the displayed connection string with:

- **VS Code**: [PostgreSQL extension](https://marketplace.visualstudio.com/items?itemName=ckolkman.vscode-postgres)
- **psql**: `psql -h localhost -p 5432 -U postgres -d openchallenges`

### OpenSearch Dashboards

```bash
# Start OpenSearch tunnel
cd apps/openchallenges/infra/cdk
./tools/start-opensearch-tunnel.sh dev {developer-name}

# The script will display:
# - Dashboard URL: https://localhost:9200/_dashboards
# - Username and password
```

Open `https://localhost:9200/_dashboards` in your browser and login with the displayed credentials.

**Note:** Keep the tunnel scripts running while accessing these resources. Press Ctrl+C to stop.

## Deployed Infrastructure

The OpenChallenges CDK app deploys a complete microservices architecture on AWS.

### Architecture Overview

```
Internet
   │
   ├─ HTTPS (443) / HTTP (80)
   │
   ▼
Application Load Balancer
   │
   ├─ /health           → Fixed response (health check)
   ├─ /api/*            → API Gateway (Spring Cloud Gateway)
   ├─ /oauth2/*         → API Gateway (Keycloak authentication)
   ├─ /.well-known/*    → API Gateway (OIDC discovery)
   ├─ /img/*            → Thumbor (with URL rewrite: /img/* → /*)
   └─ /*                → Web App (Angular frontend)
```

### Stacks Deployed

1. **VPC Stack**: Networking infrastructure
2. **Image Bucket Stack**: S3 bucket for image storage
3. **ALB Stack**: Application Load Balancer with routing rules
4. **ECS Cluster Stack**: Fargate cluster for containerized services
5. **Thumbor Stack**: Image processing service
6. **Image Service Stack**: Generates Thumbor URLs for images
7. **API Gateway Stack**: Spring Cloud Gateway for backend APIs
8. **Web Stack**: Angular frontend application

### Architecture Decisions

**1. ALB-First Routing (No Reverse Proxy)**

The infrastructure uses ALB listener rules for all routing, eliminating the openchallenges-apex Caddy reverse proxy:

- **Original**: ALB → Caddy container → Backend services
- **Current**: ALB → Backend services (direct)

Benefits:

- One less service to manage and monitor
- Better AWS integration (CloudWatch metrics, access logs, WAF)
- Higher availability (AWS-managed service)
- Native URL rewriting via ALB transforms (October 2025 feature)

**2. Thumbor URL Rewriting**

Images are served through the `/img/*` path on the ALB. To strip the prefix before forwarding to Thumbor, we use ALB's native URL rewrite transforms:

```python
transforms=[
    elbv2.CfnListenerRule.TransformProperty(
        type="url-rewrite",
        url_rewrite_config=elbv2.CfnListenerRule.RewriteConfigObjectProperty(
            rewrites=[
                elbv2.CfnListenerRule.RewriteConfigProperty(
                    regex="^/img/(.*)$",
                    replace="/$1",
                )
            ]
        ),
    )
]
```

This transforms requests like:

- Request: `https://alb-dns/img/abc123=/500x500/image.jpg`
- Forwarded to Thumbor: `/abc123=/500x500/image.jpg`

**3. Service Discovery**

All backend services use AWS Cloud Map for internal service-to-service communication via DNS:

- Pattern: `{service-name}.{namespace}.local`
- Example: `openchallenges-thumbor.openchallenges-dev-tschaffter.local:8889`

**4. Environment Variable Injection**

The web app (Angular) requires environment-specific configuration. Variables are injected at container startup by generating a `config.json` file that the app fetches at runtime.

## Testing

### Run All Tests

```bash
nx test openchallenges-infra-cdk
```

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
