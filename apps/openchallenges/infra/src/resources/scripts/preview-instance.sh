#!/usr/bin/env bash

apt update

# Set the hostname
hostnamectl set-hostname openchallenges-preview-instance

# Install Docker Engine on Ubuntu
apt-get install -y ca-certificates curl gnupg

install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Post-installation steps for Docker Engine
user="ubuntu"

groupadd --force docker
usermod -a --groups docker $user

# Run Nginx server to test HTTP traffic (TODO: to remove when deploying the OC stack)
docker run -it --rm -d -p 8080:80 --name web nginx

# Prepare OpenChallenges stack
apt install git

# Install Node.js
curl -sL https://deb.nodesource.com/setup_18.x | bash -
apt-get install -y nodejs gcc g++ make
# apt-get install -y gcc g++ make

# Install devcontainer CLI
npm install -g @devcontainers/cli@0.25.2

# Start OC stack inside the Sage monorepo devcontainer
sudo -i -u ubuntu bash << EOF
cd ~
git clone --filter=blob:none https://github.com/Sage-Bionetworks/sage-monorepo.git
devcontainer up --workspace-folder sage-monorepo
EOF

# devcontainer exec --workspace-folder sage-monorepo bash -c \
#   ". ./dev-env.sh \
#   && workspace-install \
#   && openchallenges-build-images"