# Platform Config Angular

A reusable Angular configuration library that provides Spring Boot-inspired configuration management.

## Features

- **YAML-based configuration** with profile support (dev, test, stage, prod)
- **Environment variable overrides** with automatic type conversion
- **Server-side rendering (SSR) support** with separate browser/Node.js code paths
- **Type-safe configuration** using generics and Zod validation
- **Spring Boot-style hierarchy** (priority from highest to lowest):
  1. **Environment variables** (highest priority) - Individual property overrides
  2. **Profile-specific configuration** (`application-{profile}.yaml`) - Environment-specific overrides
  3. **Base configuration** (`application.yaml`) - Default values for all environments

## Configuration Loading

The library determines which profile to load using environment variables:

- **`ENVIRONMENT`** - Primary variable for setting the active profile (e.g., `ENVIRONMENT=stage` loads `application-stage.yaml`)
- **`NODE_ENV`** - Fallback variable, automatically set by build tools (maps `development` → `dev`, `production` → `prod`)
- Default: `development` (loads `application-dev.yaml`)

### Examples

```bash
# Load production configuration
ENVIRONMENT=prod npm start

# Load staging configuration
ENVIRONMENT=stage npm start

# Use NODE_ENV (mapped to 'dev')
NODE_ENV=development npm start

# Override specific property
ENVIRONMENT=prod API__CSR__URL=https://custom-api.example.com npm start
```

## Usage

This is a generic library that can be extended for specific applications. See `@sagebionetworks/openchallenges/web/angular/config` for an example implementation.

## Running unit tests

Run `nx test platform-config-angular` to execute the unit tests.
