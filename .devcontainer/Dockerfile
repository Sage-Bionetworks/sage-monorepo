ARG imageVersion=bullseye-20211220-slim
FROM debian:$imageVersion

# Create the docker group so that we can assign it to the user.
# This is to enable the non-root user to use the command `docker`.
RUN groupadd docker \
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
  && usermod -a --groups docker,sudo $user \
  && usermod --shell /bin/bash $user \
  && echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

USER $user

CMD ["bash"]