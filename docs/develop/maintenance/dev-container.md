# Dev Container Updates

This guide provides comprehensive instructions for updating the development container used in the
Sage Monorepo. The dev container provides a consistent development environment across all
contributors and includes all necessary tools and dependencies.

## Overview

Updating the dev container is an intentionally **two-step, two-PR workflow** that separates
image BUILD from image ACTIVATION. This decoupling lets us publish and test a new image across
multiple branches before the whole team is switched over.

### Terminology

- **Dockerfile** (`.github/.devcontainer/Dockerfile`): Defines the base Ubuntu LTS image, pinned
  build ARG versions, and any tools installed directly via shell/apt scripts.
- **Build Definition (Intermediate) devcontainer.json** (`.github/.devcontainer/devcontainer.json`):
  Extends the Dockerfile with Dev Container Features (Docker-in-Docker, Java, Go, Kubernetes tooling, etc.).
  This file is used only to BUILD & PUBLISH the final composite image to GHCR. It is NOT what VS Code opens.
- **Active devcontainer.json** (`.devcontainer/devcontainer.json`): The definition actually consumed
  by VS Code / `devcontainer up` when developers open the monorepo. It references an already-published
  image tag (from GHCR) and should remain stable except when activating a new image.
- **Published Image**: `ghcr.io/sage-bionetworks/sage-monorepo-devcontainer:<tag>` produced by Step 1.

### Two-Step Workflow

| Step | PR Focus                  | Touched Files                                                                 | Result                                                 | Why Separate?                                                                                             |
| ---- | ------------------------- | ----------------------------------------------------------------------------- | ------------------------------------------------------ | --------------------------------------------------------------------------------------------------------- |
| 1    | BUILD & PUBLISH new image | `.github/.devcontainer/Dockerfile`, `.github/.devcontainer/devcontainer.json` | New image pushed to GHCR (tag = commit SHA or version) | Allows iterative hardening & branch testing without forcing all developers onto a possibly unstable image |
| 2    | ACTIVATE published image  | `.devcontainer/devcontainer.json`                                             | Team starts using the new image                        | Minimal diff; easy rollback by reverting single tag change                                                |

### Rationale for Separation

1. **Safety** – Risky base upgrades (Ubuntu date tag, Docker engine, language majors) are isolated from the day-to-day environment until validated.
2. **Parallel Testing** – Multiple feature branches can temporarily reference the newly published image for smoke tests before activation.
3. **Fast Rollback** – If problems surface after activation, reverting one commit (image tag change) restores the previous environment.
4. **Auditability** – PR 1 documents image construction changes; PR 2 documents operational adoption.

### Typical Flow

1. Create branch `chore/devcontainer/build-<date>` and update:

    - Dockerfile (Ubuntu date tag + tool ARG bumps + removals)
    - Build Definition devcontainer file (feature version bumps, add/remove Features)

2. Open PR (BUILD). Opening and updating the PR triggers a build of the Docker images.
3. Merge PR (PUBLISH). Builds & publishes image → GHCR tag.
4. (Optional) Test in other branches by temporarily overriding the active file locally (do NOT commit those overrides) or using `devcontainer build/up/exec` commands referencing the new image.
5. Once validated, create branch `chore/devcontainer/activate-<tag>` updating only `.devcontainer/devcontainer.json` to the new image tag.
6. Open PR (ACTIVATE). After merge, developers rebuild / reopen in container and begin using the new environment.

!!! note

    Keep any temporary local tag substitutions (e.g., `:local`) out of committed code. Only SHA or explicitly versioned tags should appear in the activated definition.

### Summary

You modify two different `devcontainer.json` files for two distinct purposes:

| File                                      | Purpose                                                         | When Edited            |
| ----------------------------------------- | --------------------------------------------------------------- | ---------------------- |
| `.github/.devcontainer/devcontainer.json` | Compose & bake the final published image together with Features | Step 1 (BUILD) only    |
| `.devcontainer/devcontainer.json`         | Point developer workflows at an existing published image        | Step 2 (ACTIVATE) only |

The remainder of this guide details how to update the Docker base image, tool versions, Features, test the resulting image, and safely activate it.

### Key Files

| File / Path                               | Role / Purpose                                                                                                                                                           | Maintenance Notes                                                                                                                                          |
| ----------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `.github/.devcontainer/Dockerfile`        | Base layer (Ubuntu LTS date tag + apt + direct installs of some tools).                                                                                                  | Step 1 only. Pin ARG versions; prefer removing rather than leaving unused tooling. Keep image reproducible (no moving tags).                               |
| `.github/.devcontainer/devcontainer.json` | BUILD Definition: aggregates the Dockerfile + Dev Container Features to bake the final image that will be published to GHCR. Not used directly by developers in VS Code. | Edit in Step 1 only. Use explicit feature versions. Treat as build recipe; never reference a production image tag here—always `build` from the Dockerfile. |
| `.devcontainer/devcontainer.json`         | ACTIVE Definition: points to the published image tag developers consume locally and in CI.                                                                               | Edit in Step 2 only. Single-line diff (image tag) ideally. Keep clean—should not contain build-time Feature composition once image is published.           |
| `dev-env.sh`                              | Workspace initialization (env vars, helper functions, wrappers).                                                                                                         | Review after major tool/runtime bumps. Sync deprecated tool names or paths.                                                                                |
| `tools/prepare-*-envs.js`                 | Language ecosystem setup (Node, Python, Java, R, etc.).                                                                                                                  | Validate they still succeed after base image & feature updates (Node majors, Python minors, JDK changes). Update literals referencing versions/paths.      |
| `.github/workflows/*devcontainer*.yml`\*  | CI workflows that build & push the Step 1 image.                                                                                                                         | Ensure cache key changes when ARGs change. Confirm push includes immutable tag (SHA) and optional semantic tag.                                            |

\* Actual workflow filenames may vary; search for `devcontainer` in `.github/workflows` when adjusting build logic.

!!! note "Maintenance Guide Scope"

    Only files directly influencing the container build, configuration, or activation workflow are listed. Application/service Dockerfiles are out of scope for this maintenance guide.

## Step 1a: Update the Docker Base Image

> See the "Update Checklist" at the end of this document for an actionable summary of this section.

### Base Image Update

The dev container uses Ubuntu as its base image. To update:

```dockerfile
# Current example:
FROM ubuntu:noble-20250910

# To update, change to newer tag with release date:
FROM ubuntu:noble-20251015  # Use date-specific tag
```

**Ubuntu Version Requirements & Tagging:**

Use only Ubuntu LTS date-specific tags for reproducibility (e.g. `ubuntu:noble-20250910`). Avoid moving tags (`noble`, `latest`) and non‑LTS releases. Update periodically (e.g. monthly or when security fixes land). Reference: official LTS list at https://releases.ubuntu.com and available date tags at Docker Hub. Always test locally before committing and track EOL to plan major migrations.

### Tool Version Updates

The Dockerfile includes numerous tools with pinned versions. Update the build arguments at the top of the file:

```dockerfile
# Example updates:
ARG hyperfineVersion="1.19.0"        # Check: https://github.com/sharkdp/hyperfine
ARG devcontainerCliVersion="0.80.0"  # Check: https://www.npmjs.com/package/@devcontainers/cli
ARG uvVersion="0.7.21"               # Check: https://github.com/astral-sh/uv
ARG rVersion="4.5.1"                 # Check: https://docs.posit.co/resources/install-r/
ARG trivyVersion="0.64.1"            # Check: https://aquasecurity.github.io/trivy
ARG nodeVersionMajor="22"            # Check: https://nodejs.org/en/about/previous-releases
ARG pnpmVersion="10.13.1"            # Check: https://github.com/pnpm/pnpm/releases
```

**Selection Criteria (condensed):** Prefer LTS where applicable, skip anything <24h old, avoid pre-releases, prefer versions with security fixes, and note breaking changes on major bumps.

**Process:** Check release page → confirm age ≥1 day → confirm LTS (when relevant) → update ARG → build & smoke test critical tooling.

**Node.js:** Use even-numbered LTS majors (18/20/22). Avoid odd majors. Check the Node.js release schedule for current LTS + EOL.

### System Package Updates

System packages are installed via apt. While most use latest available versions, some may be pinned:

```dockerfile
RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    ca-certificates curl git bash-completion gnupg2 lsb-release ssh sudo \
    python3-pip python3-dev python-is-python3 pipx openjdk-17-jdk \
    # ... other packages
```

**Considerations:**

- Most packages will automatically use latest versions from Ubuntu repositories
- Major Ubuntu version changes may affect package availability
- Test package installations if changing base Ubuntu version

### Tool Management and Removal

To maintain security and reduce the attack surface, the dev container tool set should be periodically reviewed and pruned.

**Tool Removal Guidelines:**

- **Security First**: Remove rarely used tools to reduce potential attack vectors
- **Usage Analysis**: Regularly assess which tools are actively used by the development team
- **Team Consensus**: Tool removal requires consensus among team members before implementation
- **Impact Assessment**: Evaluate potential workflow disruption before removing any tool

**Review Process:**

1. **Quarterly Reviews**: Conduct tool usage reviews every 3-4 months
2. **Usage Tracking**: Monitor which tools are being used in CI/CD pipelines and development workflows (future work)
3. **Team Survey**: Poll team members about tool usage and necessity
4. **Deprecation Notice**: Provide advance notice before removing tools to allow workflow adjustments
5. **Documentation**: Update documentation when tools are removed

**Tools to Monitor for Removal:**

- Development utilities with overlapping functionality
- Language-specific tools for unused programming languages
- CLI tools that haven't been used in recent projects
- Legacy tools that have modern replacements

**Before Removing a Tool:**

- [ ] Confirm tool is not used in any CI/CD pipelines
- [ ] Check if tool is referenced in documentation
- [ ] Verify no active projects depend on the tool
- [ ] Get explicit approval from team leads
- [ ] Plan migration path if tool replacement is needed

## Step 1b: Update Dev Container Features

> Refer to the "Update Checklist" for a summarized set of required review items.

After updating the Dockerfile (Step 1a), update `.github/.devcontainer/devcontainer.json` (feature set & runtime configuration). This remains part of Step 1 (BUILD & PUBLISH).

### Feature Set

The `devcontainer.json` file references the updated Dockerfile and uses pre-built features from the Dev Containers specification:

```jsonc
"features": {
  "ghcr.io/devcontainers/features/docker-in-docker:2.12.2": {
    "version": "28.3.2",
    "installDockerComposeSwitch": false
  },
  "ghcr.io/devcontainers/features/java:1.6.3": {
    "version": "21.0.7-ms",
    "additionalVersions": ["21.0.7-graal"]
  },
  "ghcr.io/devcontainers/features/go:1.3.2": {
    "version": "1.24.5",
    "golangciLintVersion": "2.2.2"
  },
  "ghcr.io/devcontainers/features/kubectl-helm-minikube:1.2.2": {
    "version": "1.33.3",
    "helm": "3.17.0",
    "minikube": "1.36.0"
  }
}
```

**Selection & Trust (condensed):** Prefer official sources (`devcontainers`, `microsoft`, `docker`, major vendors). For community features: ≥50 stars, active in last 3 months, multi-contributor, semantic releases, documented, responsive issues, passes source/security skim. Always pin immutable version tags.

**Mini Process:** Look up latest → validate trust → pin explicit version → ensure cross-feature compatibility → test build.

### Community Feature Evaluation Checklist

When considering features from smaller communities, use this evaluation checklist:

!!! tip

    The checkboxes below are clickable—check them as you evaluate a community feature for inclusion in the dev container to track your review progress.

**Repository Assessment:**

- [ ] GitHub repository has 50+ stars
- [ ] Repository is actively maintained (commits within last 3 months)
- [ ] Issues are actively triaged and responded to
- [ ] Multiple contributors (not single-person projects)
- [ ] Clear contribution guidelines and code of conduct

**Release Management:**

- [ ] Regular release cadence (at least quarterly)
- [ ] Semantic versioning is used
- [ ] Release notes document changes clearly
- [ ] No breaking changes without major version bumps

**Code Quality:**

- [ ] Comprehensive README with usage examples
- [ ] Automated testing (CI/CD pipelines)
- [ ] Security scanning in place
- [ ] Code follows security best practices
- [ ] Dependencies are up-to-date and secure

**Community Health:**

- [ ] Active maintainer response to issues
- [ ] Community discussions and engagement
- [ ] Clear project roadmap or vision
- [ ] Responsive to security vulnerability reports

**Feature-Specific Review:**

- [ ] Source code review for security vulnerabilities
- [ ] Feature functionality aligns with monorepo needs
- [ ] No unnecessary privileges or access requirements
- [ ] Compatible with existing features and tools
- [ ] Performance impact is acceptable

**Approval Process:**

1. Complete evaluation checklist
2. Document findings and recommendation
3. Get approval from security team (if applicable)
4. Test feature in isolated environment
5. Get team consensus before adding to production

### Feature-Specific Notes

#### Docker-in-Docker

- Update Docker version to latest stable

#### Java

- Coordinate with project Java version requirements
- Update both primary and additional JDK versions
- Ensure compatibility with Gradle configurations

#### Go

- Update Go version for latest language features
- Update golangci-lint for latest linting rules

#### Kubernetes Tools

- Update kubectl to match target Kubernetes versions
- Update Helm for latest chart compatibility
- Update Minikube for local development

## Step 1c: Test the Dev Container Locally

!!! tip Quick reference

    All actions here are summarized in the "Update Checklist".

### Manual Dev Container Testing

After updating both the Dockerfile and `devcontainer.json`, perform comprehensive testing using the devcontainer CLI.

#### Build the Dev Container Image

The dev container includes the devcontainer CLI, so you can build the new container from within an existing dev container.

From the root workspace directory:

```bash
devcontainer build \
  --workspace-folder .github \
  --image-name ghcr.io/sage-bionetworks/sage-monorepo-devcontainer:local
```

**Build Troubleshooting:**

- **Docker Version Issues**: If build fails, the Docker engine version in `devcontainer.json` might be too recent

      - Try specifying an older Docker version in the `docker-in-docker` feature
      - Example: Change from `"version": "28.3.2"` to `"version": "27.3.2"` or earlier
      - Check [Docker releases](https://docs.docker.com/engine/release-notes/) for stable versions

#### Test Container Startup and Functionality

1. Start the dev container:
    ```bash
    devcontainer up --workspace-folder .github
    ```

2. Connect to the running container:
    ```bash
    docker exec -it sage-monorepo-devcontainer-prebuild bash
    ```

3. Verify tool installations, especially new and updates tools:
    ```bash
    # Test key tools are available and working
    docker --version
    go version
    java --version
    kubectl version --client
    node --version
    python --version
    trivy --version
    uv --version
    ```

4. Exit the container:
    ```bash
    # Use Ctrl+C to exit the container session or enter:
    exit
    ```

5. Clean up the test container:
    ```bash
    docker rm -f sage-monorepo-devcontainer-prebuild
    ```

### Headless Monorepo Runtime Testing

After basic functionality testing, run a comprehensive, CLI‑driven test pass with the entire monorepo mounted into the container using the devcontainer CLI **without opening the workspace in VS Code** (i.e. VS Code is not attached to this container yet). This validates the baked image + features + toolchain against real workspace code (builds, tests, scripts) but does **not** exercise:

- VS Code extension set / settings sync
- Editor‑integrated language servers started via the active `devcontainer.json`
- Any VS Code tasks, launch configs, or UI workflows

For those editor integration aspects, see the later section "IDE & CI Integration Testing" which performs an actual VS Code rebuild using the active dev container definition.

!!! note "Why 'Headless'?"

    "Headless" here means the container is running and the repo is mounted, but VS Code has not been reopened against it, so no editor extensions, language servers, tasks, or settings from the active devcontainer definition have initialized. It’s a fast runtime/tooling validation step before paying the cost of a full VS Code rebuild & attachment.

#### Verify Local Image Availability

Confirm the dev container image was built successfully:

```bash
docker images | grep devcontainer
# Expected output:
# ghcr.io/sage-bionetworks/sage-monorepo-devcontainer   local   <image-id>   <time>   ~5GB
```

#### Update Dev Container Definition

Update the main dev container configuration file (`.devcontainer/devcontainer.json`) to reference the new local image if needed for testing.

Example diff (do NOT commit this change; for local testing only):

```diff
 // .devcontainer/devcontainer.json
 {
    "name": "Sage Monorepo Dev Container",
-  "image": "ghcr.io/sage-bionetworks/sage-monorepo-devcontainer:sha-6269b9b3d863831c296f843edd84b2c7e1d4733d",
+  "image": "ghcr.io/sage-bionetworks/sage-monorepo-devcontainer:local",
    // ... rest of configuration
 }
```

After testing, revert the image line back to the published SHA tag before opening or updating any PRs.

#### Deploy with Monorepo Codebase

Instead of manually stepping into the test container, you can run the comprehensive test from your current dev container:

!!! warning "Workspace Side Effects"

    The steps below mount your *current* monorepo working copy into a throwaway dev container. Any
    tasks you run (e.g. `workspace-install`, Nx builds, tests) will mutate files **on your host**, not
    inside an isolated scratch layer. Typical side effects include updates to:

    - `node_modules/`
    - Nx / build caches (e.g. `.nx/cache`)
    - Generated artifacts (dist / build folders)
    - Potential lockfile adjustments if dependency resolution differs

    These changes persist after you remove the test container. To avoid unexpected diffs or
    performance regressions when you return to other feature work, choose one of the following
    strategies:

    **Safer Approaches**

      1. Use a temporary clone or worktree:
        - `git worktree add ../sage-devcontainer-test <branch>`
        - Run all container tests from that directory
        - Remove when done: `git worktree remove ../sage-devcontainer-test`
      2. Copy the repo for testing (choose one):
         - Tar stream (preserves permissions, excludes .git):
           ```bash
           mkdir -p ../sage-devcontainer-test-copy
           tar --exclude .git -cf - . | tar -C ../sage-devcontainer-test-copy -xf -
           ```
         - Simple cp fallback (copies .git then removes it):
           ```bash
           cp -a . ../sage-devcontainer-test-copy
           rm -rf ../sage-devcontainer-test-copy/.git
           ```
      3. If testing in-place, plan cleanup:
        - Remove build caches: `rm -rf .nx/cache` (and any `dist/` or `build/` dirs)
        - Recreate dependencies fresh: `rm -rf node_modules && workspace-install`
        - Discard unintended changes: `git restore .` / `git clean -fdX` (review before running)

    If you notice unexplained build speed regressions after testing, clear caches as above before
    investigating further.

1. Start dev container with monorepo:

   ```bash
   # From the root of the monorepo
   devcontainer up --workspace-folder .
   ```

2. Execute comprehensive test directly:

   ```bash
   devcontainer exec --workspace-folder . bash -c ". ./dev-env.sh \
     && workspace-install \
     && nx run-many --target=create-config,build,test --skip-nx-cache"
   ```

3. Clean up test container:

   ```bash
   # After the command completes, remove the test container
   docker rm -f sage-monorepo-devcontainer
   ```

**Benefits of this approach:**

- **Streamlined**: No need to manually connect and navigate inside the test container
- **Automated**: Single command runs the entire test suite
- **Clean**: Remains in your active development environment throughout
- **Efficient**: Uses `--skip-nx-cache` to ensure fresh builds and tests

### IDE & CI Integration Testing

1. **Test in VS Code:**

    - Open the project in VS Code
    - Select "Rebuild and Reopen in Container"
    - Verify all extensions load correctly
    - Test basic development workflows

2. **Test CI/CD compatibility:**

    - Ensure GitHub Actions continue to work
    - Verify any external integrations still function

## Step 1d: Build & Publish Image (PR 1)

> The checklist later includes an abbreviated version of these PR 1 steps.

### Creating the Build PR

After successful local testing:

1. **Revert local testing changes:**

    - **Critical**: Revert any image tag changes in `.devcontainer/devcontainer.json` made for local testing
    - Ensure the file references the current production image tag, not "local" or test configurations
    - Example: Change from `"image": "ghcr.io/sage-bionetworks/sage-monorepo-devcontainer:local"` back to the current SHA-based tag

2. **Create feature branch PR:**

    - Create a pull request with all dev container changes
    - Include comprehensive description of updates made
    - Document any breaking changes or new requirements
    - Verify `.devcontainer/devcontainer.json` uses production image reference

3. **PR triggers automated build:**

    - GitHub workflow will build the new dev container image
    - Automated tests will validate the container functionality
    - Review any build failures or test issues

### Publishing the New Image

Upon merging the PR to the main branch:

1. **Automated image build and publish:**

    - Docker image is built automatically via GitHub Actions
    - Image is published to GitHub Container Registry (GHCR)
    - New image receives a unique tag based on commit SHA

2. **Image availability:**

    - Published image becomes available at `ghcr.io/sage-bionetworks/sage-monorepo-devcontainer:<tag>`
    - Tag format typically includes commit SHA or version identifier

## Step 2: Activate Published Image (PR 2)

> See the checklist for a condensed activation + verification summary.

### Activation Overview

!!! info "Activation PR"

    **Important**: Using the new dev container requires a second PR. This *activation* PR is what
    actually enables the new image for **all contributors**. Until it merges, only users who
    temporarily point their local `.devcontainer/devcontainer.json` at the new image (or use a
    local `:local` tag) will experience the changes.

    After the activation PR is merged into `main`:

    - When contributors pull the updated `main` branch, VS Code detects the changed dev container
      image reference and pops a notification prompting them to "Rebuild" / "Reopen in Container".
    - A reminder line is also printed in their terminal the next time the workspace starts inside
      the prior container, advising a rebuild to pick up the new environment.
    - Rebuilding pulls the published GHCR image directly (does not re-run the full Docker build
      locally unless the layer cache is cold).

    If a developer dismisses the prompt, they can manually trigger it later via the command palette
    ("Dev Containers: Rebuild and Reopen in Container"). They remain on the previous environment
    until they rebuild, which can explain version skew in support questions.

1. **Update image reference:**

    - Create a new PR updating `.devcontainer/devcontainer.json`
    - Change the image tag to reference the newly published image
    - Example: Update from previous tag to new GHCR tag

2. **Two-step deployment rationale:**

    - **Safety**: Separates image building from activation
    - **Validation**: Allows testing of published image before activation
    - **Rollback**: Easy to revert to previous working image if issues arise

3. **Activation process:**
   ```jsonc
   // In .devcontainer/devcontainer.json
   {
     "name": "Sage Monorepo Dev Container",
     "image": "ghcr.io/sage-bionetworks/sage-monorepo-devcontainer:<new-tag>",
     // ... rest of configuration
   }
   ```

### Post-Deployment Verification

After the activation PR is merged:

1. **Team notification:**

    - (Auto) Notify team members of the new dev container availability
    - Provide any migration notes or new requirements
    - Document any workflow changes

2. **Monitor for issues:**

    - Watch for team reports of container problems
    - Check CI/CD pipeline functionality
    - Be prepared to quickly rollback if critical issues arise

## Update Checklist

!!! tip "Interactive Checklist"

    The checkboxes below are clickable in rendered docs. Use them to track your progress while performing an update; you can clear them to rehearse or review the process later. Their state lives only in your browser and is not persisted or shared. Opening the page in another browser or device, or after clearing site data, resets all boxes.

### Pre-Update

- [ ] Document current versions for rollback reference
- [ ] Check changelogs for breaking changes in tools being updated
- [ ] Coordinate with team on timing of updates
- [ ] Review tool usage and identify candidates for removal
- [ ] Get team consensus on any tool removals

### During Update

- [ ] Update base Ubuntu image tag (LTS with date-specific tag only)
- [ ] Verify all tool releases are at least 1 day old
- [ ] Ensure Node.js uses LTS version (even-numbered major versions)
- [ ] Update all tool versions in Dockerfile build args to latest stable releases
- [ ] Update dev container features using non-moving version tags only
- [ ] Verify all features are from trusted organizations or approved community sources
- [ ] Complete community feature evaluation for any new features
- [ ] Update any hardcoded version references in scripts
- [ ] Remove approved unused tools to reduce attack surface

### Testing

#### Basic Functionality Testing

- [ ] Build prebuild image (see "Build the Dev Container Image")
- [ ] If build fails, consult "Build Troubleshooting" (adjust Docker version if needed)
- [ ] Start container (see step 1 in "Test Container Startup and Functionality")
- [ ] Attach shell (see step 2 in same section)
- [ ] Spot‑check core tools (see step 3 list — all versions print successfully)
- [ ] Exercise representative workflows (package install, Gradle tasks, Docker, kubectl) — no errors
- [ ] Remove prebuild container (see cleanup step 5)

#### Headless Monorepo Runtime Testing

Headless = dev container started via CLI (devcontainer up / exec) with the monorepo mounted, but VS Code not reopened inside that container yet. Editor extensions/settings activation is validated later under "VS Code Integration Testing".

**Option A: Manual Testing (step-by-step)**

- [ ] (Optional) Temporarily point `.devcontainer/devcontainer.json` to `:local` for this session (see "[Update Dev Container Definition](#update-dev-container-definition)") — do NOT commit
- [ ] (If using interactive flow) Attach a shell to the running container (see steps in "3.1 Manual Dev Container Testing")
- [ ] Initialize environment & install dependencies (source `dev-env.sh`, then install; see prior section for details)
- [ ] Run full create-config / build / test targets (refer to combined Nx targets described earlier)
- [ ] Verify all projects succeed (review output; no failures reported)
- [ ] Remove the throwaway test container (see cleanup note in "Deploy with Monorepo Codebase")

**Option B: Streamlined Testing (automated)**

- [ ] Confirm local image built (see "Verify Local Image Availability")
- [ ] Run the single exec-based end‑to‑end test flow (see command in "Deploy with Monorepo Codebase" step 2)
- [ ] Review output for failures (all targets should pass)
- [ ] Remove test container (see cleanup step referenced above)

#### VS Code Integration Testing

- [ ] Test in VS Code dev container environment ("Rebuild and Reopen in Container")
- [ ] Verify all extensions load correctly and development workflows function

### Post-Update

#### First PR - Dev Container Changes

- [ ] **Revert local testing changes**: Ensure `.devcontainer/devcontainer.json` uses production image tag, not "local"
- [ ] Update documentation if new tools added/removed
- [ ] Create PR with clear description of changes (Dockerfile, devcontainer.json, etc.)
- [ ] Verify PR includes only production-ready configurations
- [ ] Ensure PR includes comprehensive changelog of all updates
- [ ] Monitor GitHub Actions build of new dev container image
- [ ] Verify successful image publication to GHCR after merge

#### Second PR - Activate New Container

- [ ] Create second PR updating `.devcontainer/devcontainer.json` image reference
- [ ] Update image tag to newly published GHCR image
- [ ] Test the published image before merging activation PR
- [ ] Monitor CI/CD pipelines after activation merge
- [ ] Notify team of new dev container availability and any workflow changes

## Troubleshooting

### Common Issues

#### Build Failures

- **Package not found**: Check if package names changed in new Ubuntu version
- **Version conflicts**: Ensure all pinned versions are still available
- **Architecture issues**: Verify all tools support the target architecture

#### Dev Container Build Failures

- **Docker version compatibility**: Latest Docker versions in `docker-in-docker` feature may cause build failures

    - **Solution**: Specify an older, stable Docker version (e.g., `"version": "27.3.2"` instead of `"28.3.2"`)
    - **Check logs**: Look for Docker daemon startup errors or compatibility issues

- **Feature conflicts**: Multiple features trying to install the same tools

    - **Solution**: Remove duplicate installations or use feature-specific configurations
    - **Review**: Check feature documentation for known conflicts

- **Memory/disk space**: Large feature installations may exceed available resources

    - **Solution**: Increase Docker desktop memory/disk limits or use smaller base images
    - **Monitor**: Check Docker system resource usage during build

- **Network issues**: Features downloading from external sources may fail

    - **Solution**: Retry build or check network connectivity
    - **Alternative**: Use cached or mirror sources when available

- **Permission errors**: Features may fail to install due to user permission issues

    - **Solution**: Ensure proper user configuration in devcontainer.json
    - **Check**: Verify the `remoteUser` setting matches the Dockerfile user

#### Dev Container Runtime Issues

- **Container won't start**: Check `devcontainer up` logs for startup errors

    - **Solution**: Verify all required ports are available and not in use
    - **Check**: Ensure Docker daemon is running and accessible

- **Tools not available**: Installed tools not found in PATH

    - **Solution**: Check if feature installation completed successfully
    - **Debug**: Connect to container and manually verify tool locations

- **VS Code integration fails**: Container starts but VS Code can't connect

    - **Solution**: Rebuild container with "Rebuild and Reopen in Container"
    - **Check**: Verify VS Code Dev Containers extension is updated

#### Runtime Issues

- **Tool missing**: Check if installation commands succeeded
- **Permission errors**: Verify user permissions and sudo access
- **Path issues**: Ensure all tools are in PATH correctly

#### Performance Issues

- **Slow builds**: Consider multi-stage builds or caching strategies
- **Large image size**: Remove unused packages and clean up in same RUN command
- **Memory usage**: Monitor resource consumption of new tools

### Recovery Process

If issues arise after an update:

1. **Immediate rollback**: Revert to previous Dockerfile/devcontainer.json
2. **Isolate the issue**: Test changes incrementally
3. **Check compatibility**: Verify tool versions work together
4. **Consult documentation**: Review tool-specific upgrade guides

## Security Considerations

### Image Security

- Regularly scan base images for vulnerabilities
- Use minimal base images when possible
- Keep system packages updated

### Tool Security

- Verify checksums/signatures when downloading tools
- Use official distribution sources
- Monitor security advisories for installed tools

### Access Security

- Limit container privileges
- Use non-root user when possible
- Secure secrets and credentials

## Automation Opportunities

Consider automating parts of the update process:

- **Dependabot**: For automatic dependency updates
- **Scheduled builds**: Regular container rebuilds with latest packages
- **Security scanning**: Automated vulnerability scanning
- **Version checking**: Scripts to check for tool updates

## Resources

- [Dev Containers Specification](https://containers.dev/)
- [Dev Container Features](https://containers.dev/features)
- [Ubuntu Docker Images](https://hub.docker.com/_/ubuntu)
- [VS Code Dev Containers Documentation](https://code.visualstudio.com/docs/devcontainers/containers)

---

For questions or issues with dev container updates, please create an issue in the repository or reach out to the maintenance team.
