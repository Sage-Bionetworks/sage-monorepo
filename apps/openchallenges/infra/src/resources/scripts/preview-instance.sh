#!/usr/bin/env bash

apt update

# Set the hostname
hostnamectl set-hostname openchallenges-preview-instance

# Install Docker Engine on Ubuntu
apt install -y ca-certificates curl gnupg

install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

apt update
apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Post-installation steps for Docker Engine
user="ubuntu"

groupadd --force docker
usermod -a --groups docker $user

# Run Nginx server to test HTTP traffic (TODO: to remove when deploying the OC stack)
# docker run -it --rm -d -p 8080:80 --name web nginx

# Install git, jq and Node.js
curl -sL https://deb.nodesource.com/setup_18.x | bash -
apt install -y git jq nodejs gcc g++ make

# Install devcontainer CLI
npm install -g @devcontainers/cli@0.25.2

# Deploy the stack from its source.
sudo -i -u ubuntu bash << EOF
cd ~

# Clone the repository and checkout the commit specified
git clone --filter=blob:none --no-checkout https://github.com/Sage-Bionetworks/sage-monorepo.git
cd sage-monorepo
git checkout ed39cfa29b7dc59aafd3e58b4b049d900e8f78ad
EOF

