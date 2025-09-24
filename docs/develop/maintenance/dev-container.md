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

### 2.1 Dev Container Features

The `devcontainer.json` file uses pre-built features from the Dev Containers specification:

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

**Update Process:**

1. Check [Dev Containers Features](https://containers.dev/features) for latest versions
2. Update feature versions in the format `name:version`
3. Update tool versions within each feature configuration
4. Test feature functionality after updates

### 2.2 Feature-Specific Updates

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

### 3.1 Local Testing

Before committing changes:

1. **Build the container locally:**

   ```bash
   cd .github/.devcontainer
   docker build -t sage-monorepo-dev-test .
   ```

2. **Test container startup:**

   ```bash
   docker run -it --rm sage-monorepo-dev-test bash
   ```

3. **Verify tool installations:**
   ```bash
   # Test key tools are available and working
   node --version
   python --version
   java --version
   go version
   kubectl version --client
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
- [ ] Update dev container features and their versions
- [ ] Update any hardcoded version references in scripts
- [ ] Remove approved unused tools to reduce attack surface

### Testing

- [ ] Build container locally without errors
- [ ] Test container startup and basic functionality
- [ ] Verify all development tools work correctly
- [ ] Test in VS Code dev container environment
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
