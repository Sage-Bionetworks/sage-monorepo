# Platform Config Angular

A reusable Angular configuration library that provides Spring Boot-inspired configuration management.

## Features

- **YAML-based configuration** with profile support (dev, test, stage, prod)
- **Environment variable overrides** with automatic type conversion
- **Server-side rendering (SSR) support** with TransferState for config sharing
- **Server/client config separation** - Optionally transform config before sending to browser (e.g., filter server-only URLs)
- **12-factor app compliant** - Configuration via environment variables, no rebuild needed for config changes
- **Type-safe configuration** using generics and Zod validation
- **Spring Boot-style hierarchy** (priority from highest to lowest):
  1. **Environment variables** (highest priority) - Individual property overrides
  2. **Profile-specific configuration** (`application-{profile}.yaml`) - Environment-specific overrides
  3. **Base configuration** (`application.yaml`) - Default values for all environments

## SSR and 12-Factor Compliance

When running with server-side rendering (SSR), the library follows the [12-factor app](https://12factor.net/config) principle: **configuration is stored in environment variables**, allowing you to change configuration without rebuilding the application.

1. **Server (Node.js):**

   - Loads YAML configuration files from filesystem
   - Applies environment variable overrides (e.g., `ENVIRONMENT=prod`, `API_CSR_URL=...`)
   - Optionally transforms config for client (e.g., filtering server-only URLs)
   - Stores client configuration in Angular's `TransferState` (embedded in the HTML response)
   - Keeps full server configuration for its own use

2. **Browser (Client):**

   - Retrieves configuration from `TransferState` (injected during SSR)
   - The browser config reflects the server's environment variable overrides (either directly or after transformation)
   - No separate HTTP requests needed for config files

This approach ensures:

- ✅ **12-factor compliance** - Configuration via environment variables, no rebuild required for config changes
- ✅ **Consistency** - Client configuration is derived from the same source as server (YAML + env vars)
- ✅ **Security** - Sensitive server-only data can be filtered out before sending to browser
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

## Server/Client Config Separation

The library supports optional transformation of configuration before sending it to the browser. This is useful when:

- The server needs different URLs than the client (e.g., internal service URLs vs public URLs)
- You want to prevent exposing server-only configuration to the browser
- Client needs a simplified version of the server configuration

### How It Works

The `ConfigLoaderService` is generic with two type parameters:

```typescript
ConfigLoaderService<T, S = T>
```

- **T**: Target config type (what the service returns - `ClientConfig` on client, `ServerConfig` on server)
- **S**: Source config type (what's loaded from YAML - typically `ServerConfig`)

When implementing a custom config service, you can provide an optional `transformForClient()` method:

```typescript
export class MyConfigService extends ConfigLoaderService<
  RuntimeClientConfig | RuntimeServerConfig,
  ServerConfig
> {
  // Validate the server config (loaded from YAML)
  override validateConfig(config: unknown): ServerConfig {
    return ServerConfigSchema.parse(config);
  }

  // Transform server config to client config before sending to browser
  override transformForClient(serverConfig: ServerConfig): ClientConfig {
    return {
      // Copy most properties as-is
      environment: serverConfig.environment,
      analytics: serverConfig.analytics,
      features: serverConfig.features,

      // Transform API URLs - client gets only the CSR URL
      api: {
        baseUrl: serverConfig.api.baseUrls.csr, // Client uses CSR URL
        docsUrl: serverConfig.api.docsUrl,
      },

      // Server-only properties are omitted (api.baseUrls.ssr)
    };
  }
}
```

### Configuration Flow with Transformation

**Server Side:**

1. Load and validate `ServerConfig` from YAML (with `api.baseUrls.{csr, ssr}`)
2. Apply environment variable overrides
3. **Transform** to `ClientConfig` (filters to `api.baseUrl` only)
4. Store transformed `ClientConfig` in TransferState
5. **Keep full `ServerConfig` for server's own use** (can access `api.baseUrls.ssr`)

**Client Side:**

1. Retrieve `ClientConfig` from TransferState
2. Use simplified config (only has `api.baseUrl`, not `api.baseUrls`)

### Example: Different API URLs

**Server Config (application.yaml):**

```yaml
api:
  baseUrls:
    csr: https://api.example.com # Public API URL for browser
    ssr: http://api-internal:8080 # Internal API URL for server
  docsUrl: https://docs.example.com
```

**Client Config (after transformation):**

```typescript
{
  api: {
    baseUrl: "https://api.example.com",  // Only the public URL
    docsUrl: "https://docs.example.com"
  }
  // api.baseUrls.ssr is NOT exposed to browser
}
```

This allows the server to communicate with internal services while the browser uses public URLs.

## Usage

This is a generic library that can be extended for specific applications.

### Basic Usage (Same Config for Server and Client)

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
export type RuntimeAppConfig = AppConfig & { isPlatformServer: boolean };

// Create a config service
@Injectable({ providedIn: 'root' })
export class AppConfigService extends ConfigLoaderService<RuntimeAppConfig> {
  override validateConfig(config: unknown): AppConfig {
    return AppConfigSchema.parse(config);
  }
}
```

This ensures all applications have a consistent `environment` property that's used for profile selection.

### Advanced Usage (Separate Server and Client Config)

For applications that need different configuration on server vs client:

```typescript
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';
import { z } from 'zod';

// Server config schema (full configuration)
export const ServerConfigSchema = BaseConfigSchema.extend({
  api: z.object({
    baseUrls: z.object({
      csr: z.string().url(), // Client-side URL
      ssr: z.string().url(), // Server-side URL (internal)
    }),
    docsUrl: z.string().url(),
  }),
  // ... other properties
});

// Client config schema (filtered/simplified)
export const ClientConfigSchema = BaseConfigSchema.extend({
  api: z.object({
    baseUrl: z.string().url(), // Single URL for client
    docsUrl: z.string().url(),
  }),
  // ... same properties as server, minus server-only ones
});

export type ServerConfig = z.infer<typeof ServerConfigSchema>;
export type ClientConfig = z.infer<typeof ClientConfigSchema>;
export type RuntimeServerConfig = ServerConfig & { isPlatformServer: true };
export type RuntimeClientConfig = ClientConfig & { isPlatformServer: false };

// Transformer function
export function transformServerToClientConfig(serverConfig: ServerConfig): ClientConfig {
  return {
    environment: serverConfig.environment,
    api: {
      baseUrl: serverConfig.api.baseUrls.csr, // Map CSR URL to single baseUrl
      docsUrl: serverConfig.api.docsUrl,
    },
    // ... other properties
  };
}

// Config service with transformation
@Injectable({ providedIn: 'root' })
export class AppConfigService extends ConfigLoaderService<
  RuntimeClientConfig | RuntimeServerConfig,
  ServerConfig
> {
  override validateConfig(config: unknown): ServerConfig {
    return ServerConfigSchema.parse(config);
  }

  override transformForClient(serverConfig: ServerConfig): ClientConfig {
    return transformServerToClientConfig(serverConfig);
  }
}
```

**Usage in Application:**

```typescript
// In a component or service
export class MyComponent {
  constructor(private configService: AppConfigService) {
    const config = this.configService.config;

    if (config.isPlatformServer) {
      // Server: access full server config
      const serverUrl = (config as RuntimeServerConfig).api.baseUrls.ssr;
    } else {
      // Client: access client config
      const clientUrl = (config as RuntimeClientConfig).api.baseUrl;
    }
  }
}
```

### Example Implementation

See `@sagebionetworks/openchallenges/web/angular/config` for a complete example implementation.

## Running unit tests

Run `nx test platform-config-angular` to execute the unit tests.
