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
FROM ubuntu:noble-20250714

# To update, change to newer tag:
FROM ubuntu:noble-20250901  # Use latest available tag
```

**Best Practices:**

- Use specific tags rather than `latest` for reproducibility
- Check [Ubuntu Docker Hub](https://hub.docker.com/_/ubuntu) for available tags
- Prefer LTS versions for stability
- Test locally before committing changes

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

**Update Process:**

1. Check each tool's release page (links provided in comments)
2. Update version numbers to latest stable releases
3. Pay attention to major version changes that might require additional modifications
4. Test critical tools after updates

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

### During Update

- [ ] Update base Ubuntu image tag
- [ ] Update all tool versions in Dockerfile build args
- [ ] Update dev container features and their versions
- [ ] Update any hardcoded version references in scripts

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
