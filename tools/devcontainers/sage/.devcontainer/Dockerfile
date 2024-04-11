ARG imageVersion=jammy-20240227
FROM ubuntu:$imageVersion

# Safer bash scripts with 'set -euxo pipefail'
SHELL ["/bin/bash", "-euxo", "pipefail", "-c"]

ARG devcontainerVersion=""
ENV DEVCONTAINER_VERSION=${devcontainerVersion}

# https://github.com/nektos/act
ARG actVersion="0.2.49"
# https://github.com/sharkdp/hyperfine
ARG hyperfineVersion="1.17.0"
# https://github.com/yarnpkg/berry
ARG yarnVersion="3.6.1"
# https://www.npmjs.com/package/@devcontainers/cli
ARG devcontainerCliVersion="0.50.2"
# https://pypi.org/project/poetry
ARG poetryVersion="1.8.2"
# https://docs.posit.co/resources/install-r/#specify-r-version
ARG rVersion="4.2.3"
# https://aquasecurity.github.io/trivy
ARG trivyVersion="0.44.1"
# https://github.com/rstudio/renv
ARG renvVersion="1.0.0"
# https://www.npmjs.com/package/playwright
ARG playwrightVersion="1.40.1"
# https://github.com/pnpm/pnpm/releases
ARG pnpmVersion="8.7.0"
# https://github.com/SonarSource/sonar-scanner-cli/releases
ARG sonarScannerVersion="5.0.1.3006"
# https://github.com/hadolint/hadolint
ARG hadolintVersion="2.12.0"

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
    # Required by Hadolint
    shellcheck \
  # Add Node.js repository
  && mkdir -p /etc/apt/keyrings \
  && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key \
    | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
  && NODE_MAJOR=18 \
  && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" \
    | tee /etc/apt/sources.list.d/nodesource.list \
  # Add Hadolint
  && curl https://github.com/hadolint/hadolint/releases/download/v${hadolintVersion}/hadolint-Linux-x86_64 -o hadolint \
  && mv hadolint /usr/local/bin/. \
  && chmod +x /usr/local/bin/hadolint \
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
    terraform \
    vault \
  # Fix Vault CLI
  # See https://github.com/hashicorp/vault/issues/10924
  && setcap -r /usr/bin/vault \
  # Install Yarn
  && corepack enable yarn \
  && corepack prepare "yarn@${yarnVersion}" --activate \
  # Instal pnpm
  # && curl -fsSL https://get.pnpm.io/install.sh | env PNPM_VERSION=${pnpmVersion} bash - \
  && corepack enable pnpm \
  && corepack prepare "pnpm@${pnpmVersion}" --activate \
  # Install @devcontainers/cli
  && npm install -g "@devcontainers/cli@${devcontainerCliVersion}" \
  # Install act
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
  # Install R
  && curl "https://cdn.rstudio.com/r/ubuntu-2204/pkgs/r-${rVersion}_1_amd64.deb" -o /tmp/r_amd64.deb \
  && gdebi --non-interactive /tmp/r_amd64.deb \
  && rm -fr /tmp/r_amd64.deb \
  && ln -s /opt/R/${rVersion}/bin/R /usr/local/bin/R \
  && ln -s /opt/R/${rVersion}/bin/Rscript /usr/local/bin/Rscript \
  && R -e "options(repos = c(POSIT = \"https://packagemanager.posit.co/all/__linux__/jammy/latest\", CRAN = \"https://mirror.las.iastate.edu/CRAN\")); install.packages(\"renv\", version = \"${renvVersion}\")" \
  # Install Trivy
  && curl -fsSL "https://github.com/aquasecurity/trivy/releases/download/v${trivyVersion}/trivy_${trivyVersion}_Linux-64bit.deb" -o /tmp/trivy.deb \
  && dpkg -i /tmp/trivy.deb \
  && rm -fr /tmp/trivy.deb \
  # Cleanup
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/* \
  && pip install --no-cache-dir pipenv

# Setting up a UTF-8 locale
RUN sed -i '/en_US.UTF-8/s/^# //g' /etc/locale.gen \
  && locale-gen
ENV LANG=en_US.UTF-8 \
  LC_ALL=en_US.UTF-8

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
    "" | tee -a "/home/$user/.bashrc" \
  # Set the default version of Yarn
  && corepack prepare "yarn@${yarnVersion}" --activate

# Install SonarScanner CLI
ARG SONAR_SCANNER_HOME=/opt/sonar-scanner
ENV SONAR_SCANNER_HOME=${SONAR_SCANNER_HOME} \
  SONAR_USER_HOME=${SONAR_SCANNER_HOME}/.sonar \
  PATH=${SONAR_SCANNER_HOME}/bin:${PATH}
RUN curl https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-${sonarScannerVersion}.zip --output sonar-scanner-cli.zip \
  && unzip sonar-scanner-cli.zip \
  && mv sonar-scanner-${sonarScannerVersion} ${SONAR_SCANNER_HOME} \
  && mkdir -p "${SONAR_USER_HOME}" "${SONAR_USER_HOME}/cache" \
  && chown -R ${user}:${user} "${SONAR_SCANNER_HOME}" \
  && chmod -R 777 "${SONAR_USER_HOME}"

USER $user

# Install browsers used for e2e testing with Playwright
# Note: The version of Playwright should be the same as the one used by your project (package.json)
RUN npx playwright@${playwrightVersion} install --with-deps \
    chromium \
    firefox \
    webkit \
  # Install pyenv as the user
  && curl https://raw.githubusercontent.com/pyenv/pyenv-installer/master/bin/pyenv-installer | bash -

# Install Python environments
ENV PYENV_ROOT /home/${user}/.pyenv
ENV PATH $PYENV_ROOT/shims:$PYENV_ROOT/bin:$PATH
RUN pyenv install \
  3.9.2 \
  3.10.14 \
  3.11.0

CMD ["bash"]