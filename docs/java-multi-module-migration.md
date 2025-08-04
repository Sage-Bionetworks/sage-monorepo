# OpenChallenges Java Multi-Module Migration

## Overview
Successfully migrated two Java projects from individual Gradle wrappers to a unified multi-module Gradle setup using Nx's @nx/gradle plugin:

- `libs/openchallenges/api-client-java` (library)
- `apps/openchallenges/organization-service` (Spring Boot application)

## Architecture Changes

### Before
- Each project had its own `gradlew` wrapper and individual `settings.gradle.kts`
- Projects were managed independently
- Dependency management was duplicated across projects

### After
- Single root `gradlew` wrapper at the monorepo level
- Unified `settings.gradle.kts` and `build.gradle.kts` at the root
- Shared dependency management through version catalogs
- Project modules defined as `:openchallenges-api-client-java` and `:openchallenges-organization-service`

## Key Files Created/Modified

### Root Level
- **`settings.gradle.kts`**: Defines the multi-module structure
- **`build.gradle.kts`**: Common configuration and module-specific settings
- **`gradle.properties`**: Shared Gradle properties
- **`gradle/libs.versions.toml`**: Centralized dependency version management

### Module Level
- Updated `build.gradle.kts` files to use shared version catalog
- Updated `project.json` files for Nx integration
- Removed individual `settings.gradle.kts` files

## Dependency Management

The organization-service now properly depends on the api-client-java module:

```kotlin
dependencies {
    // Other dependencies...
    implementation(project(":openchallenges-api-client-java"))
}
```

This is verified in the dependency tree:
```
\--- project :openchallenges-api-client-java
```

## Nx Integration

The @nx/gradle plugin automatically detects and manages Gradle tasks:
- Builds are coordinated through Nx
- Dependency graph is properly maintained
- Caching and task distribution work correctly

## Build Commands

### Multi-Module Commands
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :openchallenges-api-client-java:build
./gradlew :openchallenges-organization-service:build

# Run tests for specific module
./gradlew :openchallenges-organization-service:test

# View dependencies
./gradlew :openchallenges-organization-service:dependencies
```

### Nx Commands
```bash
# Nx automatically detects Gradle projects
npx nx show projects --affected

# Custom tasks still work through project.json
npx nx serve openchallenges-organization-service
npx nx generate openchallenges-api-client-java
```

## Benefits

1. **Reduced Duplication**: Single dependency management system
2. **Improved Build Performance**: Gradle's incremental builds across modules
3. **Better Dependency Resolution**: Organization service automatically includes API client changes
4. **Nx Integration**: Leverages Nx's task orchestration and caching
5. **Simplified Maintenance**: Single Gradle configuration to maintain

## Testing

The migration has been verified by:
- ✅ Building both modules independently
- ✅ Building all modules together
- ✅ Verifying dependency resolution
- ✅ Running tests successfully
- ✅ Confirming Nx detects the projects

## Next Steps

1. Consider migrating other OpenChallenges Java projects to this multi-module setup
2. Explore Nx's caching and distributed task execution for CI/CD optimization
3. Add shared Java utilities as additional modules if needed
4. Update CI/CD pipelines to use the new build commands

## References

- [Spring Boot Multi-Module Guide](https://spring.io/guides/gs/multi-module)
- [Nx Gradle Tutorial](https://nx.dev/getting-started/tutorials/gradle-tutorial)
- [Gradle Multi-Project Builds](https://docs.gradle.org/current/userguide/multi_project_builds.html)
