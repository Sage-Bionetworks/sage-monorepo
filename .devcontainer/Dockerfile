ARG imageVersion=bullseye-20211220-slim
FROM debian:$imageVersion

ARG dockerGid

# Create the docker group before Docker is installed to specify its GID.
# The GID must match the value on the host.
# cut -d: -f3 < <(getent group docker)
RUN groupadd --gid $dockerGid docker \
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    ca-certificates curl git gnupg lsb-release sudo \
    python3-pip python-is-python3 \
  && curl -sL https://deb.nodesource.com/setup_16.x | sudo bash - \
  && apt-get update -qq -y \
  && apt-get install --no-install-recommends -qq -y \
    nodejs \
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