# Centralized Docker Base Images

This system provides centralized management of Docker container images for postgres and caddy project types in the openchallenges and amp-als applications.

## How It Works

The Nx plugin automatically detects project types and generates appropriate Dockerfiles with centralized container image references.

### Supported Project Types

- **postgres**: For PostgreSQL database projects
- **caddy**: For Caddy reverse proxy projects
- **custom**: For all other projects (uses original Dockerfile)

### Automatic Detection

Projects are automatically classified based on:

1. Project path (must contain `openchallenges` or `amp-als`)
2. Project name containing `postgres` or `apex` (apex projects use caddy container image)

### Generated Targets

For postgres and caddy projects, the plugin automatically creates:

- `generate-dockerfile`: Creates `Dockerfile.generated` with the correct container image
- `build-image`: Builds the Docker image (depends on `generate-dockerfile`)

## Base Image Configuration

Base images are centrally configured in:

```
libs/sage-monorepo/nx-plugin/src/config/base-images.ts
```

Current configuration:

- **postgres**: `mirror.gcr.io/postgres:16.9-bullseye`
- **caddy**: `mirror.gcr.io/caddy:2.9.1`

## Usage

### Building Images

For postgres/caddy projects:

```bash
# This will automatically generate Dockerfile.generated and build
nx run openchallenges-postgres:build-image

# Or just generate the Dockerfile
nx run openchallenges-postgres:generate-dockerfile
```

For custom projects:

```bash
# Uses the original Dockerfile
nx run bixarena-postgres:build-image
```

### Updating Base Images

To update a container image version:

1. Edit `libs/sage-monorepo/nx-plugin/src/config/base-images.ts`
2. Update the version for the desired image type
3. Rebuild the plugin: `nx build sage-monorepo-nx-plugin`
4. Reset Nx cache: `nx reset`
5. Rebuild affected images

### Project Classification

View project classification:

```bash
# Check project tags to see container image type
nx show project openchallenges-postgres --json | jq '.tags'
# Output: ["base-image:postgres", "type:db", "scope:backend"]

# Check available targets
nx show project openchallenges-postgres --json | jq '.targets | keys'
# Output: ["build-image", "create-config", "generate-dockerfile", "serve-detach"]
```

## Templates

Dockerfile templates are stored in:

- `libs/sage-monorepo/nx-plugin/src/templates/postgres.Dockerfile.template`
- `libs/sage-monorepo/nx-plugin/src/templates/caddy.Dockerfile.template`

These templates support variable substitution:

- `{{baseImage}}`: Replaced with the centralized container image

## Benefits

1. **Centralized Version Management**: Update container image versions in one place
2. **Automatic Generation**: No manual Dockerfile maintenance for standard projects
3. **Type Safety**: TypeScript ensures container image configurations are valid
4. **Backward Compatibility**: Custom projects continue to use original Dockerfiles
5. **Project-Specific Customization**: Templates can be customized per project type
