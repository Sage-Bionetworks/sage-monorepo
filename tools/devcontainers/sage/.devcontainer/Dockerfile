ARG imageVersion=bullseye-20221004-slim
FROM debian:$imageVersion

# Safer bash scripts with 'set -euxo pipefail'
SHELL ["/bin/bash", "-euxo", "pipefail", "-c"]

ARG devcontainerVersion=""
ENV DEVCONTAINER_VERSION=${devcontainerVersion}

# https://github.com/nektos/act
ARG actVersion="0.2.34"
# https://github.com/sharkdp/hyperfine
ARG hyperfineVersion="1.15.0"
# https://github.com/yarnpkg/berry
ARG yarnVersion="3.3.0"
# https://www.npmjs.com/package/@devcontainers/cli
ARG devcontainerCliVersion="0.25.2"
# https://pypi.org/project/poetry
ARG poetryVersion="1.3.1"
# https://docs.posit.co/resources/install-r/#specify-r-version
ARG rVersion="4.2.3"

# Create the docker group so that we can assign it to the user.
# This is to enable the non-root user to use the command `docker`.
# hadolint ignore=DL3008,DL3013
RUN groupadd docker \
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    ca-certificates curl git bash-completion gnupg2 lsb-release ssh sudo \
    python3-pip python3-dev python-is-python3 openjdk-17-jdk \
    htop unzip vim wget lsof iproute2 build-essential \
    kafkacat jq ca-certificates-java gdebi-core \
    # Required by AWS CLI
    mandoc \
    # Required for setting up locales
    locales \
    # Required by pyenv
    make build-essential libssl-dev zlib1g-dev libbz2-dev \
    libreadline-dev libsqlite3-dev wget curl llvm libncurses5-dev libncursesw5-dev \
    xz-utils tk-dev libffi-dev liblzma-dev \
  # Add Node.js repository
  && curl -sL https://deb.nodesource.com/setup_18.x | bash - \
  # Add GitHub CLI repository
  && curl -sSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | \
      gpg --dearmor -o /usr/share/keyrings/githubcli-archive-keyring.gpg \
  && echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | \
      tee /etc/apt/sources.list.d/github-cli.list > /dev/null \
  # Add ngrok repository
  && curl -s https://ngrok-agent.s3.amazonaws.com/ngrok.asc | tee /etc/apt/trusted.gpg.d/ngrok.asc >/dev/null \
  && echo "deb https://ngrok-agent.s3.amazonaws.com bullseye main" | tee /etc/apt/sources.list.d/ngrok.list \
  # Add hashicorp repository
  && curl -fsSL https://apt.releases.hashicorp.com/gpg | apt-key add - \
  && echo "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | tee /etc/apt/sources.list.d/hashicorp.list \
  # Install extra packages
  && apt-get update -qq -y \
  && apt-get install --no-install-recommends -qq -y \
    nodejs gh ngrok \
    terraform=1.4.6-1 \
    vault=1.13.2-1 \
  # Fix Vault CLI
  # See https://github.com/hashicorp/vault/issues/10924
  && setcap -r /usr/bin/vault \
  && corepack enable yarn \
  && corepack prepare "yarn@${yarnVersion}" --activate \
  # Install @devcontainers/cli
  && npm install -g "@devcontainers/cli@${devcontainerCliVersion}" \
  # Install nektos/act
  && curl "https://raw.githubusercontent.com/nektos/act/v${actVersion}/install.sh" | bash - \
  # Install hyperfine
  && curl -sSL "https://github.com/sharkdp/hyperfine/releases/download/v${hyperfineVersion}/hyperfine_${hyperfineVersion}_amd64.deb" \
    -o /tmp/hyperfine.deb \
  && apt-get install --no-install-recommends -qq -y /tmp/hyperfine.deb \
  && rm -fr /tmp/hyperfine.deb \
  # Install AWS CLI
  && curl https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o awscliv2.zip \
  && unzip awscliv2.zip \
  && ./aws/install \
  && rm -fr awscliv2.zip ./aws \
  # Add AWS Session Manager plugin
  && curl -fsSL "https://s3.amazonaws.com/session-manager-downloads/plugin/latest/ubuntu_64bit/session-manager-plugin.deb" -o /tmp/session-manager-plugin.deb \
  && dpkg -i /tmp/session-manager-plugin.deb \
  && rm -fr /tmp/session-manager-plugin.deb \
  # Install Poetry
  && curl -sSL https://install.python-poetry.org | POETRY_HOME=/etc/poetry python3 - --version "${poetryVersion}" \
  && ln -s /etc/poetry/bin/poetry /usr/local/bin/. \
  # Increase the max number of file watchers
  # See https://github.com/Sage-Bionetworks/sage-monorepo/issues/856
  && echo fs.inotify.max_user_watches=524288 | tee -a /etc/sysctl.conf \
  # Cleanup
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/* \
  && pip install --no-cache-dir pipenv

# Setting up a UTF-8 locale
RUN sed -i '/en_US.UTF-8/s/^# //g' /etc/locale.gen \
  && locale-gen
ENV LANG=en_US.UTF-8

## Install the R environment
RUN curl "https://cdn.rstudio.com/r/debian-11/pkgs/r-${rVersion}_1_amd64.deb" -o /tmp/r_amd64.deb \
  && gdebi --non-interactive /tmp/r_amd64.deb \
  && rm -fr /tmp/r_amd64.deb

ARG user=vscode
RUN useradd -m $user \
  && echo "$user:$user" | chpasswd \
  && usermod -a --groups docker,sudo $user \
  && usermod --shell /bin/bash $user \
  && echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers \
  && printf "%s\n" \
    "" \
    "#export PYENV_ROOT=\"\$HOME/.pyenv\"" \
    "#export PATH=\"\$PYENV_ROOT/bin:\$PATH\"" \
    "eval \"\$(pyenv init --path)\"" \
    "eval \"\$(pyenv virtualenv-init -)\"" \
    "" \
    "# source dev-env.sh if found in the current directory" \
    "if [ -f dev-env.sh ]; then" \
    "  . ./dev-env.sh" \
    "  workspace-initialize-env" \
    "fi" \
    "" | tee -a "/home/$user/.bashrc"

USER $user

# Install pyenv as the user
RUN curl https://raw.githubusercontent.com/pyenv/pyenv-installer/master/bin/pyenv-installer | bash -

## Install Python environments
ENV PYENV_ROOT /home/${user}/.pyenv
ENV PATH $PYENV_ROOT/shims:$PYENV_ROOT/bin:$PATH
RUN pyenv install \
  3.10.0 \
  3.11.0

CMD ["bash"]