ARG imageVersion=bullseye-20221004-slim
FROM debian:$imageVersion

# Safer bash scripts with 'set -euxo pipefail'
SHELL ["/bin/bash", "-euxo", "pipefail", "-c"]

ARG devcontainerVersion=""
ENV DEVCONTAINER_VERSION=${devcontainerVersion}

# https://github.com/nektos/act
ARG actVersion="0.2.32"
# https://github.com/sharkdp/hyperfine
ARG hyperfineVersion="1.15.0"

# Create the docker group so that we can assign it to the user.
# This is to enable the non-root user to use the command `docker`.
# hadolint ignore=DL3008,DL3013
RUN groupadd docker \
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    ca-certificates curl git bash-completion gnupg2 lsb-release ssh sudo \
    python3-pip python-is-python3 openjdk-17-jdk \
    htop unzip vim wget lsof iproute2 build-essential \
  # Add Node.js repository
  && curl -sL https://deb.nodesource.com/setup_16.x | bash - \
  # Add GitHub CLI repository
  && curl -sSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | \
      gpg --dearmor -o /usr/share/keyrings/githubcli-archive-keyring.gpg \
  && echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | \
      tee /etc/apt/sources.list.d/github-cli.list > /dev/null \
  # Add ngrok repository
  && curl -s https://ngrok-agent.s3.amazonaws.com/ngrok.asc | tee /etc/apt/trusted.gpg.d/ngrok.asc >/dev/null \
  && echo "deb https://ngrok-agent.s3.amazonaws.com bullseye main" | tee /etc/apt/sources.list.d/ngrok.list \
  # Install extra packages
  && apt-get update -qq -y \
  && apt-get install --no-install-recommends -qq -y \
    nodejs gh ngrok \
  && corepack enable yarn \
  && corepack prepare yarn@3.2.4 --activate \
  # Install @devcontainers/cli
  && npm install -g @devcontainers/cli@0.23.2 \
  # Install nektos/act
  && curl "https://raw.githubusercontent.com/nektos/act/v${actVersion}/install.sh" | bash - \
  # Install hyperfine
  && curl -sSL "https://github.com/sharkdp/hyperfine/releases/download/v${hyperfineVersion}/hyperfine_${hyperfineVersion}_amd64.deb" \
    -o /tmp/hyperfine.deb \
  && apt-get install --no-install-recommends -qq -y /tmp/hyperfine.deb \
  && rm -fr /tmp/hyperfine.deb \
  # Cleanup
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/* \
  && pip install --no-cache-dir pipenv

ARG user=vscode
RUN useradd -m $user \
  && echo "$user:$user" | chpasswd \
  && usermod -a --groups docker,sudo $user \
  && usermod --shell /bin/bash $user \
  && echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers \
  && printf "%s\n" \
    "" \
    "# source dev-env.sh if found in the current directory" \
    "if [ -f dev-env.sh ]; then" \
    "  . ./dev-env.sh" \
    "  workspace-initialize-env" \
    "fi" | tee -a /home/$user/.bashrc

USER $user

CMD ["bash"]