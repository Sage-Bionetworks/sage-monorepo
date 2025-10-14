# BixArena Infra CDK

AWS CDK infrastructure as code for BixArena. This project defines and manages the cloud resources required to deploy and operate the BixArena application stack.

## Quick Start

### Synthesize CloudFormation Templates

Generate CloudFormation templates from the CDK code:

```bash
nx synth bixarena-infra-cdk
```

### List Stacks

View all available CDK stacks:

```bash
nx ls bixarena-infra-cdk
```

### Deploy Infrastructure

Deploy the CDK stacks to AWS:

```bash
nx deploy bixarena-infra-cdk
```

## Environment Configuration

### Creating Configuration Files

Initialize environment configuration files:

```bash
nx create-config bixarena-infra-cdk
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
nx synth bixarena-infra-cdk
nx ls bixarena-infra-cdk
nx deploy bixarena-infra-cdk

# Staging
nx synth bixarena-infra-cdk:stage
nx ls bixarena-infra-cdk:stage
nx deploy bixarena-infra-cdk:stage

# Production
nx synth bixarena-infra-cdk:prod
nx ls bixarena-infra-cdk:prod
nx deploy bixarena-infra-cdk:prod
```

### Environment Variable Precedence

Environment variables are loaded by Nx with the following precedence (highest to lowest):

1. Environment-specific file (`.env.dev`, `.env.stage`, or `.env.prod`)
2. Base environment file (`.env`)

Variables defined in environment-specific files will override any matching variables in the base `.env` file.
