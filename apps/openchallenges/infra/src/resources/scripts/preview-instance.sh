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
git checkout 45fdf04c406d6f24cfdd832ea19bbf16312ffd19

# So that the images built inside the dev container are available on the host (i.e. outside the dev
# container)
./tools/switch-devcontainer-to-docker-outside-of-docker.sh

# Step outside of the repository
cd ..

# Start the dev container
devcontainer up --workspace-folder sage-monorepo

# Build the images
devcontainer exec --workspace-folder sage-monorepo bash -c \
  ". ./dev-env.sh \
  && workspace-install \
  && ./tools/switch-devcontainer-to-docker-outside-of-docker.sh \
  && openchallenges-build-images"

# Remove the dev container
docker rm -f sage_devcontainer
EOF

