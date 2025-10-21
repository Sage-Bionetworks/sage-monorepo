# Platform Config Angular

A reusable Angular configuration library that provides Spring Boot-inspired configuration management.

## Features

- **YAML-based configuration** with profile support (dev, test, stage, prod)
- **Environment variable overrides** with automatic type conversion
- **Server-side rendering (SSR) support** with TransferState for config sharing
- **12-factor app compliant** - Browser uses the same config as server (including env var overrides)
- **Type-safe configuration** using generics and Zod validation
- **Spring Boot-style hierarchy** (priority from highest to lowest):
  1. **Environment variables** (highest priority) - Individual property overrides
  2. **Profile-specific configuration** (`application-{profile}.yaml`) - Environment-specific overrides
  3. **Base configuration** (`application.yaml`) - Default values for all environments

## SSR and 12-Factor Compliance

When running with server-side rendering (SSR), the library ensures **both server and browser use identical configuration**:

1. **Server (Node.js):**

   - Loads YAML configuration files from filesystem
   - Applies environment variable overrides (e.g., `ENVIRONMENT=prod`, `API_CSR_URL=...`)
   - Stores final configuration in Angular's `TransferState`

2. **Browser (Client):**
   - Retrieves configuration from `TransferState` (injected during SSR)
   - Uses the **exact same configuration** as the server, including all environment variable overrides
   - No separate HTTP requests needed for config files

This approach ensures:

- ✅ **12-factor compliance** - Configuration comes from environment variables
- ✅ **Consistency** - No drift between server and client configuration
- ✅ **Security** - Sensitive environment variables processed server-side only
- ✅ **Performance** - No additional HTTP requests for config files

## Configuration Loading

The library determines which profile to load using the following priority:

1. **`ENVIRONMENT`** - Primary environment variable (e.g., `ENVIRONMENT=stage` loads `application-stage.yaml`)
2. **`NODE_ENV`** - Fallback variable, automatically set by build tools (maps `development` → `dev`, `production` → `prod`)
3. **`environment` property in `application.yaml`** - Default value defined in base configuration

This approach allows you to:

- Set a sensible default in `application.yaml` (e.g., `environment: dev`)
- Override for specific environments using `ENVIRONMENT` variable
- Leverage standard Node.js `NODE_ENV` conventions

### Configuration Flow

**With SSR (Server-Side Rendering):**

```
Server Start
    ↓
1. Load application.yaml (from filesystem)
2. Load application-{ENVIRONMENT}.yaml (from filesystem)
3. Apply environment variable overrides (`API_CSR_URL`, etc.)
4. Validate and store in TransferState
    ↓
Browser Hydration
    ↓
5. Check TransferState for config
6. If found: Use server's configuration (with env var overrides)
7. Config loaded successfully ✓
```

**Without SSR (Client-Side Only Rendering):**

```
Browser Start
    ↓
1. Check TransferState for config
2. If not found: Load via HTTP
    ↓
3. HTTP GET /config/application.yaml
4. Read 'environment' property from application.yaml
5. HTTP GET /config/application-{environment}.yaml
6. Merge configurations
7. Validate config
8. Config loaded successfully ✓
```

**Key Differences:**

| Aspect                    | With SSR                                        | Without SSR                              |
| ------------------------- | ----------------------------------------------- | ---------------------------------------- |
| **Config Source**         | TransferState (from server)                     | HTTP requests to `/config/` endpoint     |
| **Environment Variables** | ✅ Applied (server-side)                        | ❌ Not available (browser has no access) |
| **Profile Selection**     | `ENVIRONMENT` → `NODE_ENV` → `application.yaml` | Only from `application.yaml`             |
| **12-Factor Compliant**   | ✅ Yes                                          | ⚠️ Limited (no env var overrides)        |
| **HTTP Requests**         | 0 (config embedded in HTML)                     | 2 (application.yaml + profile yaml)      |

**Note:** For production deployments, SSR is recommended to ensure 12-factor compliance and allow environment variable overrides.

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
    url: z.url(),
  }),
});

export type AppConfig = z.infer<typeof AppConfigSchema>;
```

This ensures all applications have a consistent `environment` property that's used for profile selection.

### Example Implementation

See `@sagebionetworks/openchallenges/web/angular/config` for a complete example implementation.

## Running unit tests

Run `nx test platform-config-angular` to execute the unit tests.
