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

The library determines which profile to load using the following priority:

1. **`ENVIRONMENT`** - Primary environment variable (e.g., `ENVIRONMENT=stage` loads `application-stage.yaml`)
2. **`NODE_ENV`** - Fallback variable, automatically set by build tools (maps `development` → `dev`, `production` → `prod`)
3. **`environment` property in `application.yaml`** - Default value defined in base configuration

This approach allows you to:

- Set a sensible default in `application.yaml` (e.g., `environment: dev`)
- Override for specific environments using `ENVIRONMENT` variable
- Leverage standard Node.js `NODE_ENV` conventions

### Examples

```bash
# Load production configuration
ENVIRONMENT=prod npm start

# Load staging configuration
ENVIRONMENT=stage npm start

# Use NODE_ENV (mapped to 'dev')
NODE_ENV=development npm start

# Override specific property
ENVIRONMENT=prod API_CSR_URL=https://custom-api.example.com npm start
```

## Usage

This is a generic library that can be extended for specific applications.

### Base Configuration Schema

The library provides a `BaseConfigSchema` that includes the standard `environment` property:

```typescript
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';
import { z } from 'zod';

// Extend the base schema with app-specific properties
export const AppConfigSchema = BaseConfigSchema.extend({
  app: z.object({
    version: z.string(),
  }),
  api: z.object({
    url: z.string().url(),
  }),
});

export type AppConfig = z.infer<typeof AppConfigSchema>;
```

This ensures all applications have a consistent `environment` property that's used for profile selection.

### Example Implementation

See `@sagebionetworks/openchallenges/web/angular/config` for a complete example implementation.

## Running unit tests

Run `nx test platform-config-angular` to execute the unit tests.
