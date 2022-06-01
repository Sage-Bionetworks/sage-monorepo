#!/bin/bash
#
# Download and install Git Credential Manager on Linux.
#
# Note: GCM should not be installed in devcontainers. This script is currently
# used mainly to install GCM in devcontainers that run using WSL (Windows
# Subsystem for Linux).

# https://github.com/GitCredentialManager/git-credential-manager/releases
pacakgeUrl=https://github.com/GitCredentialManager/git-credential-manager/releases/download/v2.0.696/gcmcore-linux_amd64.2.0.696.deb

# Download and install Git Crede
wget $pacakgeUrl -P /tmp
sudo dpkg -i /tmp/$pacakgeUrl
git-credential-manager-core configure

# Cleanup
rm -fr /tmp/$pacakgeUrl