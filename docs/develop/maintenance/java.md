# Java Build & Dependency Maintenance

This guide covers governance of Java dependencies and maintenance of the Java build toolchain (Gradle wrapper and JDK) for the monorepo. It emphasizes a centralized version catalog for consistency plus clearly isolated procedures for upgrading Gradle and the JDK.

The dependency model is described below; build tool & JDK upgrade procedures live at the end under "Build Tool & JDK Upgrades".

## Dependency Model

Java dependencies are managed primarily via the Gradle Version Catalog located at `gradle/libs.versions.toml`. Individual `build.gradle.kts` files reference aliases defined there. Centralizing versions:

- Ensures alignment across services and libraries
- Simplifies upgrades and security remediation
- Reduces dependency drift

### Key Files

| File / Path                                          | Role / Purpose                                                           | Maintenance Notes                                                                       |
| ---------------------------------------------------- | ------------------------------------------------------------------------ | --------------------------------------------------------------------------------------- |
| `gradle/libs.versions.toml`                          | Central Gradle Version Catalog (aliases, bundles, plugin & lib versions) | Primary source of truth for versions                                                    |
| `settings.gradle.kts`                                | Declares included projects & (optionally) plugin management              | Keep aligned with catalog usage                                                         |
| `build.gradle.kts` (root)                            | Common repositories, shared plugin/application of conventions            | Avoid hard‑coded versions; delegate to catalog                                          |
| `apps/*/build.gradle.kts`, `libs/*/build.gradle.kts` | Module build scripts consuming catalog aliases                           | Should not redefine versions directly                                                   |
| `gradle.properties`                                  | JVM & Gradle configuration (memory, flags, toolchain hints)              | Version bumps rarely here                                                               |
| `buildSrc/`                                          | Local convention plugins (Java, Spring Boot, Lombok, Jacoco, publishing) | Contains a few hard‑coded versions (JUnit, Lombok, Jacoco) that must be manually synced |

!!! note "buildSrc maintenance"

    Individual plugin scripts inside `buildSrc` are intentionally not listed here; treat the directory as a single maintenance surface. During updates, grep for literal versions inside `buildSrc` and align them with the catalog.

## Version Catalog Conventions

The goal of the catalog is to provide a single, predictable naming scheme that makes it obvious which family a dependency belongs to and how versions are shared. Consistent aliasing reduces diffs, improves discoverability, and prevents accidental version divergence.

### General Rules

1. Derive library family aliases from the dependency `group` (not the full `group:artifact`) when multiple artifacts share a version (e.g., `com.fasterxml.jackson.core` → `jackson`).
2. Use kebab-case for all aliases: `spring-boot`, `spring-cloud`, `jackson`, `logback`, `micrometer`, `junit`, `lombok`.
3. Use a single version key per family in `[versions]`; individual artifacts reference it via `version.ref`.
4. Do not encode version numbers in alias names (avoid `jackson-2-18`).
5. Use suffixes only when they add semantic clarity:
   - `-bom` for Bill of Materials / platform entries (e.g., `spring-boot-bom`).
   - `-plugin` for Gradle plugin entries where an ID could collide (`spotless-plugin`).
6. Keep aliases stable; renaming causes repo‑wide churn. Only rename to correct a clear inconsistency.
7. Prefer short, unambiguous roots. If a group has a long prefix chain (`io.github.resilience4j`), choose `resilience4j`.
8. For a single artifact with no family context, derive alias from the artifact, trimming redundant prefixes (e.g., `micrometer-registry-prometheus` → `micrometer-prometheus`).

### Recommended Catalog Structure (Excerpt)

```toml
[versions]
jackson = "2.18.0"
spring-boot = "3.3.3"
junit = "5.10.2"

[libraries]
jackson-core = { group = "com.fasterxml.jackson.core", name = "jackson-core", version.ref = "jackson" }
jackson-databind = { group = "com.fasterxml.jackson.core", name = "jackson-databind", version.ref = "jackson" }
junit-jupiter = { group = "org.junit.jupiter", name = "junit-jupiter", version.ref = "junit" }

[bundles]
jackson = ["jackson-core", "jackson-databind"]

[plugins]
spring-boot = { id = "org.springframework.boot", version.ref = "spring-boot" }
```

### Naming Transformation Examples

| Group / Artifact                               | Rationale                                   | Alias / Pattern         |
| ---------------------------------------------- | ------------------------------------------- | ----------------------- |
| `com.fasterxml.jackson.core` (family)          | Shared version across multiple artifacts    | `jackson` (version key) |
| `org.springframework.boot`                     | Core Spring Boot ecosystem / plugin         | `spring-boot`           |
| `org.springframework.cloud`                    | Distinct Spring sub-ecosystem               | `spring-cloud`          |
| `io.micrometer:micrometer-registry-prometheus` | Artifact-specific; family root `micrometer` | `micrometer-prometheus` |
| `ch.qos.logback`                               | Simple unique family                        | `logback`               |
| `org.projectlombok:lombok`                     | Single artifact; group & artifact align     | `lombok`                |

### Anti‑Patterns vs Preferred

| Anti‑Pattern                   | Issue                                  | Preferred                  |
| ------------------------------ | -------------------------------------- | -------------------------- |
| `com-fasterxml-jackson-core`   | Leaks full group, verbose              | `jackson`                  |
| `springBoot`                   | Mixed casing, not kebab                | `spring-boot`              |
| `jackson-databind-version` key | Version name tied to specific artifact | `jackson` (family version) |
| `micrometerRegistryPrometheus` | CamelCase and artifact untrimmed       | `micrometer-prometheus`    |

!!! tip "When to introduce a new version key"

    Create a new version entry only when artifacts within the same group must intentionally diverge in version. Document the divergence with an inline comment to avoid future “cleanup” PRs that accidentally re‑align them.

!!! warning "Renaming Aliases"

    Renaming an existing alias creates a large diff across the repository. Avoid unless the current name is misleading or blocks adoption of a consistent pattern.

## Update Sources

!!! tip "Planned (Cadence) Updates"

    Run a scheduled dependency refresh (e.g., monthly for patch/minor; quarterly review for majors / platform upgrades) even if no urgent trigger appears. This prevents large, risky jumps later.

Typical triggers for an update:

- Scheduled maintenance window (planned cadence refresh)
- Security advisory (CVE) or transitive vulnerability exposure
- Framework / BOM alignment (e.g., Spring Boot + Spring Cloud compatibility)
- New feature / API needed in a newer library version
- Upcoming EOL (JDK, framework, or library support window closing)
- Performance / memory / stability regression fix available upstream
- Build warnings or deprecations indicating future breakage
- (Future) Automated bot PR (Renovate / Dependabot) once enabled

## Workflow: Routine Upgrade (Patch / Minor)

1. Identify candidate updates with `./gradlew dependencyUpdates --no-parallel`.
2. Edit the target version(s) in `gradle/libs.versions.toml`.
3. Manually synchronize any hard‑coded versions in `buildSrc` (JUnit, JUnit Platform launcher, Lombok, Jacoco).
   Update those literals to match the catalog (or the selected new version) and add an inline comment if deliberate divergence.
4. Rerun `./gradlew dependencyUpdates --no-parallel` to confirm that the updates have been applied.
5. Run a clean build:
   ```bash
   ./gradlew clean build
   ```
6. Run tests selectively if change is scoped, otherwise full test suite.
7. Commit with message: `chore(deps): bump <lib> to <version>`.
8. Push branch & open/refresh PR.

### Batching Patch & Minor Updates

Batching several low‑risk version bumps into a single PR reduces review overhead and keeps the catalog tidy. Follow these guidelines to keep risk controlled:

**Safe to Batch When**

- All changes are patch or minor (no majors / milestones / RCs / betas).
- No code changes or API migrations required (catalog + lock + `buildSrc` literals only).
- All libraries belong to a small number of logical families (e.g., testing, logging, serialization) OR are clearly unrelated but trivially safe (pure patch).
- Build, tests, and lint produce no new warnings that demand immediate action.

**Good Grouping Examples**

- Testing stack: JUnit, Mockito, AssertJ, Testcontainers.
- Jackson family (core / databind / annotations) at the same patch.
- Spring Boot + compatible Spring Cloud patch/minor (after checking release notes).
- Observability: Micrometer core + registry modules.

**When to Split Instead**

- Any major version is involved.
- A dependency has known behavioral change even in a minor (e.g., Hibernate SQL generation tweaks, Netty event loop adjustments).
- A security fix (CVE) needs fast‑track isolation for audit clarity.
- A build tool / Gradle plugin upgrade could impact cache keys or task wiring.
- You must add resolution strategy rules or exclusions to resolve conflicts.

**Practical Limits**

- Aim for ≤ ~15 edited version lines (catalog + `buildSrc`) per batch PR.
- If reviewing the diff requires excessive scrolling, split by family.

**Commit / PR Style**

- Single commit: `chore(deps): batch patch/minor updates (testing + jackson)`
- Or one commit per family inside one PR if you want granular blame.

**Checklist for a Batched PR**

- [ ] Only patch/minor versions included
- [ ] `gradle/libs.versions.toml` updated & tidy (no orphaned commented versions)
- [ ] `buildSrc` literals (JUnit / Jacoco / Lombok) synced if touched
- [ ] `./gradlew dependencyUpdates --no-parallel` now shows only pending majors (or nothing relevant)
- [ ] `./gradlew clean build` passes
- [ ] Targeted / affected integration tests run (if any)
- [ ] No new deprecation floods (or documented in PR description)
- [ ] PR description lists families updated + highlights anything security‑related

!!! warning "Avoid over‑broad batches"

    Mixing unrelated ecosystems (e.g., database driver + logging + Spring + build plugins) increases risk and review fatigue. Smaller, coherent batches are merged faster.

!!! tip "Automate grouping later"

    A future enhancement could script grouping by scanning the catalog diff and clustering changes by group prefix (e.g., `com.fasterxml.jackson.*`).

## Workflow: Major Upgrade

1. Review upstream release notes & migration guide.
2. Create a dedicated branch: `chore/deps/java-<lib>-<major>-upgrade`.
3. Update version in `libs.versions.toml`.
4. Run build & inspect compilation/runtime failures.
5. Apply required code/config migrations (document in PR description).
6. Run extended test matrix (integration, contract, API compatibility if applicable).
7. Add `BREAKING CHANGE:` footer to the PR description if public API impact.
8. Request second reviewer (recommended for high-risk changes).

## Security / CVE Response

1. Open branch: `security/deps/<cve-id>-<lib>`.
2. Update only the affected library versions.
3. Run targeted tests + any security scan tasks.
4. Merge promptly after review.
5. Include mitigation note in release summary.

## Tooling & Commands

| Action                            | Command                                     | Notes                       |
| --------------------------------- | ------------------------------------------- | --------------------------- |
| List dependency insights          | `./gradlew dependencies`                    | Per module                  |
| (Optional) Check for new versions | `./gradlew dependencyUpdates --no-parallel` | Requires versions plugin    |
| Build & test all                  | `./gradlew clean build`                     | Ensures no stale outputs    |
| Run a single module build         | `./gradlew :path:to:module:build`           | Faster feedback             |
| Generate dependency report        | `./gradlew htmlDependencyReport`            | If report plugin configured |

## Dependency Scopes

| Scope                | Use For                                  | Notes                      |
| -------------------- | ---------------------------------------- | -------------------------- |
| `implementation`     | Internal library use                     | Not exposed transitively   |
| `api`                | Libraries whose APIs leak to consumers   | Use sparingly              |
| `compileOnly`        | Annotation processors, compile-time only | Avoid runtime reliance     |
| `runtimeOnly`        | Drivers, logging impls                   | Not needed at compile time |
| `testImplementation` | Test frameworks & utilities              | Keep isolation             |

## Managing Transitive Dependencies

- Prefer allowing Gradle to resolve transitives unless a direct version is required to fix a CVE or conflict.
- Use `strictly` or `reject` rules only when necessary.
- Document any enforced versions in a dedicated section (e.g., below) to avoid accidental removal.

### Example Enforcement Block

```kotlin
configurations.all {
    resolutionStrategy {
        force("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    }
}
```

(Shift to version catalog alignment if possible instead of forcing.)

### When (Rarely) to Use Forcing

Use a forced version only as a short‑lived mitigation when one of these applies:

- A transitive dependency pulls an older vulnerable version (security / CVE hotfix).
- Upstream libraries have not yet released an aligned version and you must unblock a build.
- You are bisecting a regression and need to pin a single suspect artifact temporarily.

!!! warning "Forces are global"

    A `force` statement affects every configuration it touches. It can hide legitimate incompatibilities and introduce subtle runtime or test failures later. Treat it as an exception, not a pattern.

### Prefer These Alternatives First

| Goal                                 | Preferred Mechanism                                                             |
| ------------------------------------ | ------------------------------------------------------------------------------- |
| Keep related artifacts aligned       | Shared version key in `libs.versions.toml`                                      |
| Enforce a coherent ecosystem version | Import a BOM/platform (`platform(libs.spring.boot.bom)`)                        |
| Override one problematic transitive  | Dependency constraint (`constraints { implementation(...) }`)                   |
| Remove obsolete / conflicting module | Exclusion on the specific dependency (`exclude(group = "...", module = "...")`) |
| Document intentional divergence      | Inline comment + constraint (not a force)                                       |

Example constraint (scoped, clearer intent than a global force):

```kotlin
dependencies {
    constraints {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0") {
            because("CVE-2024-XXXX fixed in 2.18.0")
        }
    }
}
```

### Temporary Force Checklist

- [ ] Justification (CVE, blocking regression) noted as a code comment.
- [ ] Issue / ticket created to remove the force.
- [ ] Catalog already reflects the target version (avoid hidden divergence).
- [ ] Evaluated BOM / constraint alternative and documented why not used.
- [ ] Full build + tests + key integration tests pass.

### Migration Off a Force

1. Monitor upstream releases until dependencies naturally resolve to the desired version.
2. Remove the `force` clause.
3. Run dependency insight to ensure the resolved graph is clean:
   ```bash
   ./gradlew dependencyInsight --dependency jackson-databind
   ```
4. If multiple versions remain, add a constraint instead of re‑adding the force.
5. Commit: `chore(deps): remove temporary force on jackson-databind`.

### Auditing Existing Forces

Quick commands to discover and validate enforced versions:

| Purpose                        | Command                                                     |
| ------------------------------ | ----------------------------------------------------------- |
| List all places using strategy | `grep -R "resolutionStrategy" -n .`                         |
| Inspect resolution path        | `./gradlew dependencyInsight --dependency jackson-databind` |
| View runtime graph (sample)    | `./gradlew dependencies --configuration runtimeClasspath`   |

!!! tip "Automate enforcement hygiene"

    Consider a small CI script that fails if new `force(` usages are added without an inline `# justified:` marker, encouraging disciplined use.

## Version Alignment / BOMs

If a platform (BOM) is used (e.g., Spring Boot):

```kotlin
dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
}
```

Keep BOM version updates isolated to ease troubleshooting.

## Testing Strategy After Upgrades

1. Unit tests (fast feedback)
2. Integration tests (service wiring, DB migrations)
3. Contract/API tests (if publishing endpoints or libraries)
4. Performance smoke (optional: startup time, memory)

## Performance & Regression Checks (Optional)

Track key metrics (startup time, heap, request latency). For significant framework upgrades, capture before/after diffs.

## Common Issues & Resolutions

| Issue                          | Cause                  | Resolution                                       |
| ------------------------------ | ---------------------- | ------------------------------------------------ |
| `NoSuchMethodError`            | Mixed library versions | Ensure single version in catalog                 |
| Annotation processor conflicts | Duplicate processors   | Exclude older processor                          |
| Classpath length errors        | Deep transitive graph  | Prune unused libs, prefer lighter alternatives   |
| Build cache misses             | Unstable inputs        | Pin plugin versions, avoid timestamp-based tasks |

## PR Review Checklist (Java)

- [ ] Versions updated only in `libs.versions.toml`
- [ ] No stray version strings left in module `build.gradle.kts`
- [ ] All tests pass locally/CI
- [ ] Release notes/migration doc referenced (for majors)
- [ ] No forced resolutions added without justification
- [ ] Security concerns addressed / CVE linked if relevant

## Future Improvements

- Adopt automated version report in CI
- Add SBOM generation (e.g., CycloneDX Gradle plugin)
- Integrate vulnerability scanning (OWASP, Snyk, etc.)

## References

- Gradle Version Catalogs: https://docs.gradle.org/current/userguide/platforms.html
- Dependency Management Best Practices: https://docs.gradle.org

---

## Custom Gradle Extensions (buildSrc)

The monorepo defines reusable Gradle convention plugins and helper scripts in `buildSrc/`. These act like lightweight, locally published plugins automatically available to every build without extra coordinates.

### What Are They?

Gradle loads any code under `buildSrc/` as an included build. Kotlin script files under `buildSrc/src/main/kotlin/` (e.g. `sage.java-library.gradle.kts`) are effectively precompiled script plugins. Applying `id("sage.java-library")` in a module delegates to the corresponding file.

### Where They Live

| File                                      | Purpose                                               |
| ----------------------------------------- | ----------------------------------------------------- |
| `sage.java-common.gradle.kts`             | Base Java configuration (Java 21, encoding)           |
| `sage.java-library.gradle.kts`            | Library conventions + JUnit (publishing)              |
| `sage.spring-boot-application.gradle.kts` | Spring Boot app conventions (tests, image)            |
| `sage.spring-boot-library.gradle.kts`     | Spring Boot library conventions (no bootJar)          |
| `sage.lombok.gradle.kts`                  | Lombok annotation processing/version override support |
| `sage.jacoco-coverage.gradle.kts`         | Jacoco coverage + verification rules                  |

### Why Versions Are Duplicated Here

These scripts cannot (directly) consume the central version catalog (`libs.versions.toml`) because Gradle resolves `buildSrc` earlier in the lifecycle. As a result, any dependency or tool versions hard-coded inside these scripts (e.g., JUnit, Jacoco, Lombok) must be kept manually in sync with the catalog to avoid drift.

### Manual Sync Required

When updating versions (e.g., via `./gradlew dependencyUpdates --no-parallel`):

1. Update `gradle/libs.versions.toml` for runtime & library use.
2. Manually inspect `buildSrc/src/main/kotlin/*.gradle.kts` for hard-coded versions:
   - JUnit (`org.junit.jupiter:junit-jupiter:<ver>`)
   - JUnit Platform launcher
   - Lombok default version in `LombokExtension`
   - Jacoco `toolVersion`
3. Bump those values to match the catalog (if defined there) or to the chosen new version if catalog does not track it yet.
4. Run a clean build to detect mismatches:
   ```bash
   ./gradlew clean build
   ```
5. If a version in `buildSrc` intentionally differs (rare), document the rationale in an inline comment.

### Suggested Future Improvement

- Introduce a verification task that parses `buildSrc` for known version literals and compares them to `libs.versions.toml`, failing the build when they diverge.
- Optionally migrate to an included build (composite) plugin project that can itself use a version catalog.

### PR Review Checklist Additions

- [ ] `buildSrc` scripts updated for any dependency bumped in catalog
- [ ] JUnit / Jacoco / Lombok versions aligned
- [ ] Inline comment added if intentional divergence

---

## Build Tool & JDK Upgrades

This section covers upgrading the Gradle build tool itself and the Java toolchain used by the monorepo. Perform these upgrades separately from routine library dependency batches for clearer review and rollback.

### Upgrading Gradle

Upgrading Gradle keeps build performance, security, and deprecation coverage current. Wrapper‑based upgrades are low risk when validated systematically.

#### When to Upgrade

- New major or minor with performance improvements or important bug fixes
- Deprecation warnings appearing in current builds that will become errors next release
- Plugin ecosystem (e.g., Spring Boot plugin) now officially supports a newer Gradle baseline
- Security advisory in an older Gradle distribution

#### Pre‑Flight Checklist

- [ ] Review [latest Gradle release](https://gradle.org/releases) and its release notes
- [ ] Verify core & third‑party plugins declare compatibility
- [ ] Ensure CI images / dev container already have a compatible JDK (Gradle 9 requires Java 21+)
- [ ] No custom init scripts or build logic relying on removed APIs

#### Upgrade Procedure (Example: 9.1.0)

1. Baseline build:
   ```bash
   ./gradlew clean build
   ```
2. Regenerate wrapper:
   ```bash
   ./gradlew wrapper --gradle-version 9.1.0 --distribution-type bin
   ```
3. Inspect changes:
   - `gradle/wrapper/gradle-wrapper.properties` updated (distributionUrl points to `gradle-9.1.0-bin.zip`)
   - `gradlew` / `gradlew.bat` updated (commit them)
   - Do not manually edit wrapper JAR/scripts; regenerate if unexpected
4. Verify version:

   ```bash
   ./gradlew --version
   ```

   Expected style of output:

   ```console
   ------------------------------------------------------------
   Gradle 9.1.0
   ------------------------------------------------------------

   Build time:    2025-09-18 13:05:56 UTC
   Revision:      e45a8dbf2470c2e2474ccc25be9f49331406a07e

   Kotlin:        2.2.0
   Groovy:        4.0.28
   Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
   Launcher JVM:  21.0.7 (Microsoft 21.0.7+6-LTS)
   Daemon JVM:    /usr/local/sdkman/candidates/java/21.0.7-ms
   OS:            Linux 6.1.148-173.267.amzn2023.x86_64 amd64
   ```

5. Full rebuild to surface deprecations:
   ```bash
   ./gradlew clean build
   ```
6. Validate multi‑project & Docker image builds:
   ```bash
   nx run-many --target=build,build-image --projects=tag:language:java
   ```
7. Remove the Gradle wrapper for Windows:
   ```bash
   rm -fr gradlew.bat
   ```
8. Commit:
   ```bash
   git add gradle/wrapper/gradle-wrapper.properties gradlew
   git commit -m "build: upgrade to Gradle 9.1.0"
   ```
9. PR description should include:
   - Release notes link
   - Summary of any new deprecation warnings (or none)
   - Confirmation of successful full + Docker image builds

#### Post‑Upgrade Validation

- [ ] CI pipelines green
- [ ] No new flaky tests introduced
- [ ] (If using scans) No major regression in configuration time
- [ ] Team notified to clear local caches only if necessary

#### Rollback

If a blocking issue appears:

```bash
./gradlew wrapper --gradle-version <previous-version> --distribution-type bin
git add gradle/wrapper/gradle-wrapper.properties gradlew gradlew.bat
git commit -m "revert: downgrade Gradle to <previous-version> (regression)"
```

!!! warning "Plugin Compatibility"

    Confirm critical plugins (Spring Boot, Spotless, Jacoco, Testcontainers, publishing) list the new Gradle version in their matrix. Upgrade lagging plugins first.

!!! tip "Wrapper Integrity"

    Always regenerate rather than manually editing wrapper files to ensure checksum authenticity.

### Updating Java (Placeholder)

<!-- To be documented in a future PR: JDK toolchain strategy, supported majors, upgrade cadence, testing matrix, rollback guidance. -->
