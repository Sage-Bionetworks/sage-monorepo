---
paths:
  - '**/build.gradle.kts'
---

# Gradle Build Scripts

This rule provides coding standards and guidelines for editing Gradle build scripts in the Sage monorepo.

## Code Style

### Indentation

Indent `build.gradle.kts` files with 2 spaces.

### Dependencies

- Use the shared version catalog (libs.versions.toml) for all dependencies
- Reference dependencies using `libs.` prefix
- Keep dependencies organized and grouped logically

### Plugin Configuration

- Apply plugins using the `alias(libs.plugins.pluginName)` syntax when available
- Use `apply false` in the root build.gradle.kts for plugins that will be applied conditionally to subprojects
- Put shared build logic in a convention plugin under `buildSrc/src/main/kotlin/` (`sage.java-library`, `sage.spring-boot-library`, `sage.spring-boot-application`), not in individual build files. The root `subprojects { when { ... } }` block applies one to each project based on its location.

## Multi-Module Structure

This is a multi-module Gradle workspace where:

- Root project manages plugin versions and common configuration
- Subprojects receive their convention plugin from the root build script by path (`libs/` → library, `apps/` → Spring Boot application)
- Version catalog centralizes all dependency versions in `gradle/libs.versions.toml`

When editing build files, ensure compatibility with the Nx Gradle plugin and maintain the established patterns.
