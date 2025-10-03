# Node.js / TypeScript Dependency Maintenance

This guide describes the process for updating and validating Node.js and TypeScript dependencies in the monorepo. It focuses on using pnpm workspaces with a centralized `package.json` and ensuring consistent, reproducible builds across Angular, React, and Node.js applications.

## Overview

Node.js dependencies are managed primarily via the root `package.json` file in conjunction with pnpm workspaces. Individual projects reference shared dependencies from the root, ensuring:

- Version alignment across all applications and libraries
- Simplified upgrades and security remediation
- Reduced dependency drift and bundle duplication
- Efficient disk usage through symlinked `node_modules`

### Key Files

| File / Path                                  | Role / Purpose                                                    | Maintenance Notes                                                 |
| -------------------------------------------- | ----------------------------------------------------------------- | ----------------------------------------------------------------- |
| `package.json` (root)                        | Central dependency management for all TypeScript/Node.js projects | Primary source of truth for versions                              |
| `pnpm-workspace.yaml`                        | Defines workspace packages and exclusions                         | Keep aligned with project structure                               |
| `tsconfig.base.json`                         | TypeScript path mapping and compiler configuration                | Update path mappings when adding/removing libraries               |
| `nx.json`                                    | Nx workspace configuration and task execution                     | Configure build, test, and lint tasks                             |
| `apps/*/project.json`, `libs/*/project.json` | Individual project configurations                                 | Should reference root dependencies via Nx or workspace resolution |
| `apps/*/package.json`, `libs/*/package.json` | Local package configurations for publishable libraries or deployable artifacts | Used for publishable libraries or deployment artifacts (e.g., Lambda functions); must be excluded from pnpm workspace in `pnpm-workspace.yaml` if using shared `node_modules` |
| `pnpm-lock.yaml`                             | Lock file ensuring reproducible installations                     | Automatically updated; commit changes                             |
| `jest.config.ts`, `jest.preset.js`           | Testing framework configuration                                   | Align with testing library versions                               |
| `stylelint.config.mjs`, `tsconfig.base.json` | Linting and TypeScript configuration                              | Keep linter versions synchronized                                 |

!!! note "Workspace dependency resolution"

    Individual projects within the workspace automatically inherit dependencies from the root `package.json`. Local `package.json` files are used in a minority of projects, usually when a library is [publishable](https://nx.dev/concepts/buildable-and-publishable-libraries) or when the `package.json` is an artifact that can be deployed (e.g., as part of an AWS Lambda function). If a project has a local `package.json` but still uses the shared (root) `node_modules`, then the project must be excluded from the pnpm workspace in `pnpm-workspace.yaml`. There is a risk of package definition drift if local `package.json` files are not regularly reviewed and updated to align with the root dependencies and the libraries actually used by the project.

## Dependency Management Strategy

The monorepo uses several strategies to maintain consistent and efficient dependency management:

### Centralized Dependencies

All shared dependencies are defined in the root `package.json`, including:

- **Framework dependencies**: Angular, React, Express
- **Testing frameworks**: Jest, Playwright, Testing Library
- **Build tools**: Nx, TypeScript, Vite, webpack
- **Linting tools**: ESLint, Prettier, Stylelint
- **Development utilities**: Husky, lint-staged

## Version Management Conventions

### General Rules

1. **Single version per package**: Maintain one version of each package across the entire monorepo to prevent conflicts
2. **Pin exact versions**: Use exact version numbers (e.g., `"1.2.3"`) rather than version ranges (e.g., `"^1.2.3"` or `"~1.2.3"`) to ensure reproducible builds and prevent unexpected updates
3. **Semantic versioning awareness**: Understand the impact of major, minor, and patch updates
4. **Framework alignment**: Keep related packages (e.g., all Angular packages) at the same version
5. **Security priority**: Prioritize security updates even for major version jumps
6. **Testing coverage**: Ensure all version updates are covered by existing test suites

### Dependency Categories

| Category               | Examples                                         | Update Strategy                                    |
| ---------------------- | ------------------------------------------------ | -------------------------------------------------- |
| **Framework Core**     | `@angular/core`, `react`, `express`              | Coordinated updates with migration guides          |
| **Nx Ecosystem**       | `@nx/angular`, `@nx/react`, `nx`                 | Keep versions aligned, follow Nx migration guides  |
| **Testing**            | `jest`, `@playwright/test`, `@testing-library/*` | Regular updates, ensure test compatibility         |
| **Build Tools**        | `typescript`, `vite`, `webpack`                  | Test thoroughly, may require configuration changes |
| **Linting/Formatting** | `eslint`, `prettier`, `stylelint`                | Regular updates, check for breaking rule changes   |
| **Development**        | `husky`, `lint-staged`                           | Low-risk updates, test development workflows       |

### Version Alignment Examples

All dependencies should use exact version numbers without ranges:

```json
{
  "dependencies": {
    "@angular/animations": "20.1.8",
    "@angular/common": "20.1.8",
    "@angular/core": "20.1.8",
    "@angular/forms": "20.1.8"
  },
  "devDependencies": {
    "@nx/angular": "21.4.1",
    "@nx/jest": "21.4.1",
    "@nx/workspace": "21.4.1"
  }
}
```

!!! warning "Avoid version ranges"

      Do not use caret (`^1.2.3`) or tilde (`~1.2.3`) ranges as they can lead to inconsistent builds across environments. Always specify exact versions.

## Workflow: Routine Upgrade (Patch / Minor)

1. **Check for updates** using npm or third-party tools:
   ```bash
   pnpm outdated
   ```
2. **Update target versions** in the root `package.json`. Always specify exact versions (e.g., `"20.1.8"`) in package.json. If pnpm adds ranges, manually edit to remove them.
   ```bash
   # Update specific packages
   pnpm update echarts ngx-echarts
   # or manually edit package.json with exact versions
   ```
3. **Install and update lockfile**:
   ```bash
   pnpm install
   ```
4. **Run quality checks**:
   ```bash
   # Build all projects (includes TypeScript compilation)
   nx run-many -t build

   # Run tests
   nx run-many -t test

   # Run linting (includes TypeScript/ESLint checks)
   nx run-many -t lint
   ```
5. **Test applications locally** (sample of key apps):
   ```bash
   agora-build-images && agora-docker-start
   model-ad-build-images && model-ad-docker-start
   ```
6. **Commit changes**:
   ```bash
   git add package.json pnpm-lock.yaml
   git commit -m "chore(deps): update dependencies (patch/minor)"
   ```

### Batching Updates

**Safe to Batch When:**

- All changes are patch or minor versions
- No breaking changes in release notes
- All packages belong to related ecosystems
- Build and tests pass without modifications

**Good Grouping Examples:**

- **Nx ecosystem**: All packages updated by `nx migrate latest`
- **Testing stack**: Jest, Testing Library, Playwright
- **Linting tools**: ESLint, Prettier, Stylelint and their plugins

**Commit Style for Batched Updates:**

```bash
git commit -m "chore(deps): batch minor updates (nx ecosystem)"
git commit -m "chore(deps): update testing dependencies"
```

## Framework Migration Workflows

For framework updates that may include breaking changes, automated migrations can handle most of the heavy lifting. This is especially important for Angular and Nx updates.

!!! important "Unified Migration Strategy"
    **Always use `nx migrate latest`** as the primary migration command. Nx expects all Nx-related packages (including Angular packages) to be on the same version for compatibility. Attempting to migrate individual packages separately can cause version conflicts and missed migrations.

### Migration Workflow

**Use When:**

- Updating any Nx-related packages (`@nx/*`, `@angular/*`, `@angular-devkit/*`)
- Regular maintenance updates
- Major framework version jumps

```bash
# Migrate all Nx and Angular packages together
nx migrate latest

# Review the changes to package.json then install
pnpm install --no-frozen-lockfile

# This creates migrations.json with pending migrations
# Review migrations.json to understand what will change
cat migrations.json

# Run the migrations (this modifies code)
nx migrate --run-migrations

# Copy and paste the content of migrations.json into the PR description

# Commit the migrations file for posterity
git add --force migrations.json
git commit -m "chore(deps): add migrations.json for posterity"

# Clean up after successful migration
git rm --cached migrations.json
git commit -m "chore(deps): remove migrations.json from tracking"
rm migrations.json
```

### Migration Troubleshooting

| Issue                          | Solution                                                                  |
| ------------------------------ | ------------------------------------------------------------------------- |
| Migration fails with conflicts | Reset branch, update packages manually, then run specific migrations      |
| `migrations.json` not generated  | Ensure you're in workspace root, check package versions compatibility     |
| Partial migration completion   | Check git status, commit successful changes, manually handle remaining    |
| Build failures after migration | Review migration logs, check for custom code that needs manual updates    |
| Peer dependency warnings       | Install missing peers or use `pnpm install --ignore-peerDeps` temporarily |

### Migration vs Manual Updates

Use migrations for:

- Major version jumps (Angular 17 → 18 → 19)
- Cross-cutting changes (workspace structure, build configs)
- Breaking API changes

Use manual updates for:

- Patch versions (20.1.8 → 20.1.9)
- Simple dependency version bumps
- Packages without migration support

## Workflow: Major Upgrade

!!! tip "Use Migrations First"
      For Angular and Nx major updates, try the [Migration Workflows](#framework-migration-workflows) first. Fall back to manual upgrade only if migrations fail or are incomplete.

1. **Create dedicated branch**:
   ```bash
   git checkout -b chore/deps/node-<package>-<major>-upgrade
   ```
2. **Try automated migration** for Nx and Angular updates. Follow the steps in [Migration Workflows](#framework-migration-workflows). If the migration succeeds, skip to step 6.
3. **Review upstream changes** (if migration unavailable/fails):
      - Read migration guides and breaking changes
      - Check compatibility with other dependencies
      - Review Angular/React update guides if applicable
4. **Update package.json** with new major version
5. **Install and check for conflicts**:
   ```bash
   pnpm install
   # Resolve any peer dependency warnings
   ```
6. **Run build and address compilation errors**:
   ```bash
   nx run-many -t build
   # Fix TypeScript errors, import changes, API modifications
   ```
7. **Update configurations** as needed:
      - Jest configurations for testing framework updates
      - ESLint rules for linter updates
      - Angular/React configuration files
8. **Run comprehensive tests**:
   ```bash
   nx run-many -t test
   nx run-many -t e2e
   ```
9. **Test critical user paths** manually
10. **Document breaking changes** in PR description

## Security / CVE Response

1. **Identify affected packages**:
   ```bash
   pnpm audit
   # or use GitHub Security alerts
   ```
2. **Create security branch**:
   ```bash
   git checkout -b security/deps/<cve-id>-<package>
   ```
3. **Update to secure version**:
   ```bash
   pnpm update <vulnerable-package>
   ```
4. **Use overrides if necessary**. Use exact versions in overrides to ensure security fixes are applied consistently.
   ```json
   {
     "pnpm": {
       "overrides": {
         "vulnerable-package": "1.2.3"
       }
     }
   }
   ```
5. **Verify fix**:
   ```bash
   pnpm audit
   ```
6. **Test, create PR, and merge promptly**

## Tooling & Commands

| Action                   | Command                                     | Notes                            |
| ------------------------ | ------------------------------------------- | -------------------------------- |
| Check outdated packages  | `pnpm outdated`                             | Shows available updates          |
| Interactive updates      | `pnpm dlx npm-check-updates -i`             | Select updates interactively     |
| Install dependencies     | `pnpm install`                              | Updates lockfile                 |
| Security audit           | `pnpm audit`                                | Check for vulnerabilities        |
| **Framework Migrations** |                                             |                                  |
| Nx & Angular migration   | `nx migrate latest`                         | Migrate all Nx/Angular packages  |
| Run migrations           | `nx migrate --run-migrations`               | Execute pending migrations       |
| **Quality Checks**       |                                             |                                  |
| Build all projects       | `nx run-many -t build`                      | Parallel builds                  |
| Test all projects        | `nx run-many -t test`                       | Run all tests                    |
| Lint all projects        | `nx run-many -t lint`                       | Code quality checks              |
| Affected projects only   | `nx affected -t build`                      | Build only changed projects      |

## Managing Transitive Dependencies

### PNPM Overrides

Use pnpm overrides to force specific versions of transitive dependencies:

```json
{
  "pnpm": {
    "overrides": {
      "axios": "1.8.2",
      "cross-spawn": "7.0.6"
    }
  }
}
```

!!! note "Pin all overrides"
      Always use exact versions in pnpm overrides to ensure predictable dependency resolution.

### When to Use Overrides

- **Security fixes**: Force secure versions of transitive dependencies
- **Compatibility issues**: Resolve version conflicts between packages
- **Bug fixes**: Apply fixes not yet propagated through dependency tree

!!! warning "Overrides are global"
      Overrides affect the entire dependency tree. Use sparingly and document reasons.

### Peer Dependencies

Handle peer dependency warnings appropriately:

- **Install missing peers** if they're genuinely needed
- **Use `pnpm install --ignore-peerDeps`** only temporarily during upgrades
- **Document peer dependency choices** in commit messages

## Framework-Specific Considerations

### Angular Projects

- **Use unified migrations**: Always use `nx migrate latest` to keep all Angular and Nx packages aligned
- **Incremental major updates**: For major Angular version jumps (e.g., 17 → 18 → 19), consider migrating one major version at a time
- **Migration schematics**: Refer to [Migration Workflows](#framework-migration-workflows) for proper migration approach

### React Projects

- **React version compatibility**: Keep `react` and `react-dom` aligned
- **Hook dependencies**: Update libraries that depend on React hooks together
- **Build tools**: Coordinate React updates with Vite/webpack configurations

### Node.js Applications

- **Runtime compatibility**: Ensure packages work with the project's Node.js version
- **Express middleware**: Test middleware compatibility after Express updates
- **Database drivers**: Coordinate database client updates with schema changes

## Common Issues & Resolutions

| Issue                         | Cause                                   | Resolution                                                    |
| ----------------------------- | --------------------------------------- | ------------------------------------------------------------- |
| `Module not found` errors     | Path mapping or dependency issues       | Check `tsconfig.base.json` paths, verify package installation |
| `Peer dependency warnings`    | Version mismatches                      | Install compatible peer dependencies or use overrides         |
| `Jest configuration errors`   | Testing framework updates               | Update Jest config, preset, and transform settings            |
| `Build failures`              | TypeScript or tooling incompatibilities | Check TypeScript version compatibility, update build configs  |
| `Memory issues during builds` | Large dependency trees                  | Increase Node.js memory limit, optimize dependencies          |

## PR Review Checklist

- [ ] Only `package.json` and `pnpm-lock.yaml` modified (for simple updates)
- [ ] All versions are exact (no caret `^` or tilde `~` ranges)
- [ ] All related packages updated together (e.g., all Angular packages)
- [ ] `pnpm audit` shows no new vulnerabilities
- [ ] All builds pass: `nx run-many -t build`
- [ ] All tests pass: `nx run-many -t test`
- [ ] No breaking changes without migration documentation
- [ ] Path mappings updated if new libraries added
