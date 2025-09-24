# Dev Container Updates

This guide provides comprehensive instructions for updating the development container used in the Sage Monorepo. The dev container provides a consistent development environment across all contributors and includes all necessary tools and dependencies.

## Overview

The dev container update process consists of two main steps:

1. **Update the Docker base image** - Modify the `Dockerfile` to use updated base images and tool versions
2. **Update the dev container definition** - Update the `devcontainer.json` configuration to enable new features or change settings

## File Locations

- **Dockerfile**: `.github/.devcontainer/Dockerfile`
- **Dev Container Configuration**: `.github/.devcontainer/devcontainer.json`
- **Docker Compose (if used)**: `.github/.devcontainer/docker-compose.yml`

## Step 1: Update the Docker Base Image

### 1.1 Base Image Update

The dev container uses Ubuntu as its base image. To update:

```dockerfile
# Current example:
FROM ubuntu:noble-20250910

# To update, change to newer tag with release date:
FROM ubuntu:noble-20251015  # Use date-specific tag
```

**Ubuntu Version Requirements:**

- **LTS Only**: Always use Ubuntu LTS (Long Term Support) versions for stability and extended support
- **Non-moving tags**: Use tags that include the release date (e.g., `noble-20250910`) instead of moving tags like `noble` or `latest`
- **Current LTS versions**:
  - Ubuntu 24.04 LTS (Noble Numbat) - Supported until April 2029
  - Ubuntu 22.04 LTS (Jammy Jellyfish) - Supported until April 2027
  - Ubuntu 20.04 LTS (Focal Fossa) - Supported until April 2025

**Tag Format:**

- ✅ **Correct**: `ubuntu:noble-20250910` (LTS version with date)
- ✅ **Correct**: `ubuntu:jammy-20250910` (LTS version with date)
- ❌ **Avoid**: `ubuntu:noble` (moving tag)
- ❌ **Avoid**: `ubuntu:latest` (moving tag)
- ❌ **Avoid**: `ubuntu:24.10` (non-LTS version)

**Best Practices:**

- **LTS versions only**: Ensures 5-year support lifecycle and stability
- **Date-specific tags**: Provides reproducible builds across environments
- **Regular updates**: Update to newer date tags monthly or as security updates are released
- **Check Ubuntu Hub**: Visit [Ubuntu Docker Hub](https://hub.docker.com/_/ubuntu/tags) for available date-tagged LTS images
- **Test locally**: Always test container builds locally before committing changes
- **Monitor EOL dates**: Plan migration to newer LTS versions well before end-of-life

### 1.2 Tool Version Updates

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

**Version Selection Criteria:**

- **LTS Only**: For tools that offer LTS versions (especially Node.js), always use LTS releases for stability
- **Release Age**: Only use releases that are at least 1 day old for security reasons
- **Stable Releases**: Avoid pre-release, beta, or release candidate versions
- **Security Updates**: Prioritize versions that include security fixes

**Update Process:**

1. **Check release pages**: Follow the links provided in comments for each tool
2. **Verify release date**: Visit the tool's GitHub repository to confirm the release is at least 1 day old
3. **LTS preference**: For Node.js and similar tools, ensure you're using LTS versions only
4. **Version validation**: Update to latest stable releases, avoiding pre-release versions
5. **Breaking changes**: Pay attention to major version changes that might require additional modifications
6. **Test critical tools**: Verify functionality after updates, especially for frequently used tools

**Node.js LTS Requirements:**

- ✅ **Use**: Even-numbered versions (18, 20, 22) - these are LTS
- ❌ **Avoid**: Odd-numbered versions (17, 19, 21) - these are current/non-LTS
- **Check**: [Node.js Release Schedule](https://nodejs.org/en/about/previous-releases) for LTS status and EOL dates

### 1.3 System Package Updates

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

### 1.4 Tool Management and Removal

To maintain security and reduce the attack surface, the dev container tool set should be periodically reviewed and pruned.

**Tool Removal Guidelines:**

- **Security First**: Remove rarely used tools to reduce potential attack vectors
- **Usage Analysis**: Regularly assess which tools are actively used by the development team
- **Team Consensus**: Tool removal requires consensus among team members before implementation
- **Impact Assessment**: Evaluate potential workflow disruption before removing any tool

**Review Process:**

1. **Quarterly Reviews**: Conduct tool usage reviews every 3-4 months
2. **Usage Tracking**: Monitor which tools are being used in CI/CD pipelines and development workflows
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

## Step 2: Update Dev Container Features

After updating the Dockerfile (Step 1), the dev container definition file (`.github/.devcontainer/devcontainer.json`) must be updated. This file references the updated Dockerfile and defines additional features and configuration for the development environment.

### 2.1 Dev Container Features

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

**Feature Trust and Selection Criteria:**

- **Trusted Organizations**: Prefer features from established organizations like:

  - `ghcr.io/devcontainers/features/*` (Official Dev Containers team)
  - `ghcr.io/microsoft/*` (Microsoft)
  - `ghcr.io/docker/*` (Docker)
  - Other major cloud providers and established open-source organizations

- **Community Feature Evaluation**: For features from smaller communities, conduct thorough review:
  - **GitHub Stars**: Minimum 50+ stars for community trust
  - **Release Frequency**: Regular releases (at least quarterly) in the past year
  - **Active Contributors**: Multiple contributors (not single-maintainer projects)
  - **Issue Response**: Active issue triage and community engagement
  - **Documentation Quality**: Comprehensive README and usage examples
  - **Source Code Review**: Review the feature's source code for security practices

**Version Requirements:**

- **Non-moving Tags**: Always use specific version numbers (e.g., `1.6.3`) instead of `latest` or branch names
- **Reproducibility**: Ensures consistent builds across environments and time
- **Audit Trail**: Enables tracking of exactly which versions are deployed
- **Security**: Prevents automatic updates that might introduce vulnerabilities

**Update Process:**

1. **Check official registry**: Visit [Dev Containers Features](https://containers.dev/features) for latest versions
2. **Verify trust criteria**: Ensure features meet organizational or community trust standards
3. **Version validation**: Use specific, non-moving version tags only
4. **Feature compatibility**: Verify feature versions work together
5. **Tool version updates**: Update tool versions within each feature configuration
6. **Security review**: For new community features, conduct security assessment
7. **Test functionality**: Verify all features work correctly after updates

### 2.2 Community Feature Evaluation Process

When considering features from smaller communities, use this evaluation checklist:

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

### 2.3 Feature-Specific Updates

#### Docker-in-Docker

- Update Docker version to latest stable
- Consider security implications of Docker-in-Docker

#### Java

- Coordinate with project Java version requirements
- Update both primary and additional JDK versions
- Ensure compatibility with Gradle/Maven configurations

#### Go

- Update Go version for latest language features
- Update golangci-lint for latest linting rules

#### Kubernetes Tools

- Update kubectl to match target Kubernetes versions
- Update Helm for latest chart compatibility
- Update Minikube for local development

## Testing Process

### 3.1 Manual Dev Container Testing

After updating both the Dockerfile and devcontainer.json, perform comprehensive testing using the devcontainer CLI:

#### Build the Dev Container Image

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

- **Build Inside Dev Container**: The dev container includes the devcontainer CLI, so you can build the new container from within an existing dev container

#### Test Container Startup and Functionality

1. **Start the dev container:**

   ```bash
   devcontainer up --workspace-folder .github
   ```

2. **Connect to the running container:**

   ```bash
   docker exec -it sage-monorepo-devcontainer-prebuild bash
   ```

3. **Verify tool installations:**

   ```bash
   # Test key tools are available and working
   node --version
   python --version
   java --version
   go version
   kubectl version --client
   uv --version
   trivy --version
   ```

4. **Test development workflows:**

   ```bash
   # Test package managers
   npm --version
   pnpm --version

   # Test build tools
   gradle --version
   mvn --version

   # Test container tools
   docker --version
   kubectl version --client
   ```

5. **Exit the container:**

   ```bash
   # Use Ctrl+C to exit the container session
   exit
   ```

6. **Clean up the test container:**
   ```bash
   docker rm -f sage-monorepo-devcontainer-prebuild
   ```

### 3.2 Alternative Docker Build Testing

For quick Dockerfile-only testing:

1. **Build the container locally:**

   ```bash
   cd .github/.devcontainer
   docker build -t sage-monorepo-dev-test .
   ```

2. **Test container startup:**
   ```bash
   docker run -it --rm sage-monorepo-dev-test bash
   ```

### 3.2 Integration Testing

1. **Test in VS Code:**

   - Open the project in VS Code
   - Select "Rebuild and Reopen in Container"
   - Verify all extensions load correctly
   - Test basic development workflows

2. **Test CI/CD compatibility:**
   - Ensure GitHub Actions continue to work
   - Verify any external integrations still function

### 3.3 Performance Testing

- Compare container build times before/after changes
- Test startup time and resource usage
- Ensure development experience remains smooth

## Update Checklist

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

- [ ] Build dev container using devcontainer CLI (`devcontainer build`)
- [ ] Troubleshoot Docker version issues if build fails
- [ ] Start dev container (`devcontainer up --workspace-folder .github`)
- [ ] Connect to running container (`docker exec -it sage-monorepo-devcontainer-prebuild bash`)
- [ ] Verify all development tools work correctly (node, python, java, go, etc.)
- [ ] Test development workflows (npm, pnpm, gradle, mvn, docker, kubectl)
- [ ] Exit container and clean up (`docker rm -f sage-monorepo-devcontainer-prebuild`)
- [ ] Test in VS Code dev container environment ("Rebuild and Reopen in Container")
- [ ] Run sample builds/tests to ensure compatibility

### Post-Update

- [ ] Update documentation if new tools added/removed
- [ ] Create PR with clear description of changes
- [ ] Monitor CI/CD pipelines after merge
- [ ] Update team on any workflow changes

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
