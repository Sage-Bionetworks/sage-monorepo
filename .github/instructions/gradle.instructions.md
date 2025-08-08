---
applyTo: '**/build.gradle.kts'
---

# Copilot Instructions: Gradle Build Scripts

This file provides coding standards and guidelines for GitHub Copilot when editing Gradle build scripts in the Sage monorepo.

## Code Style

### Indentation

- **ALWAYS use 2 spaces for indentation** in build.gradle.kts files
- Do not use tabs or other indentation sizes
- Ensure consistent indentation throughout the file

### Example of correct indentation:

```gradle-kotlin-dsl
configure(listOf(project(":openchallenges-mcp-server"))) {
  apply(plugin = "org.springframework.boot")
  apply(plugin = "org.graalvm.buildtools.native")
  apply(plugin = "dev.nx.gradle.project-graph")

  configure<SomeExtension> {
    property = "value"
    nested {
      anotherProperty = "anotherValue"
    }
  }
}
```

### Dependencies

- Use the shared version catalog (libs.versions.toml) for all dependencies
- Reference dependencies using `libs.` prefix
- Keep dependencies organized and grouped logically

### Plugin Configuration

- Apply plugins using the `alias(libs.plugins.pluginName)` syntax when available
- Use `apply false` in the root build.gradle.kts for plugins that will be applied conditionally to subprojects
- Apply plugins to specific subprojects using the `configure(listOf(project(...)))` pattern

## Multi-Module Structure

This is a multi-module Gradle workspace where:

- Root project manages plugin versions and common configuration
- Subprojects are configured individually using `configure(listOf(project(...)))`
- Version catalog centralizes all dependency versions in `gradle/libs.versions.toml`

When editing build files, ensure compatibility with the Nx Gradle plugin and maintain the established patterns.
