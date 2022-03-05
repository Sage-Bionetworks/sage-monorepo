ARG imageVersion=bullseye-20211220-slim
FROM debian:$imageVersion

ARG dockerGid=1001

# Create the docker group before installing Docker to specify its GID.
# Then, install Python, Node and Docker.
RUN groupadd --gid $dockerGid docker \
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    ca-certificates curl git gnupg lsb-release sudo \
    python3-pip python-is-python3 \
  && curl -sL https://deb.nodesource.com/setup_16.x | sudo bash - \
  && curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg \
    --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg \
  && echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian \
    $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null \
  && apt-get update -qq -y \
  && apt-get install --no-install-recommends -qq -y \
    nodejs \
    docker-ce docker-ce-cli containerd.io \
  && npm install --global yarn \
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/* \
  && pip install pipenv

ARG user=vscode
RUN useradd -m $user \
  && echo "$user:$user" | chpasswd \
  && usermod -a --groups sudo,$dockerGid $user \
  && usermod --shell /bin/bash $user \
  && echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

USER $user

CMD ["bash"]