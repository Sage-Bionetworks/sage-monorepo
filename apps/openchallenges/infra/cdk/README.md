# OpenChallenges Infra CDK

AWS CDK infrastructure as code for OpenChallenges. This project defines and manages the cloud resources required to deploy and operate the OpenChallenges application stack.

## Quick Start

### Synthesize CloudFormation Templates

Generate CloudFormation templates from the CDK code:

```bash
nx synth openchallenges-infra-cdk
```

### List Stacks

View all available CDK stacks:

```bash
nx ls openchallenges-infra-cdk
```

### Deploy Infrastructure

Deploy the CDK stacks to AWS:

```bash
nx deploy openchallenges-infra-cdk
```

## Environment Configuration

### Creating Configuration Files

Initialize environment configuration files:

```bash
nx create-config openchallenges-infra-cdk
```

This command creates the necessary `.env` files for different deployment environments.

### Available Environments

The project supports three deployment environments:

- **Development**: Uses `.env` and `.env.dev`
- **Staging**: Uses `.env` and `.env.stage`
- **Production**: Uses `.env` and `.env.prod`

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
