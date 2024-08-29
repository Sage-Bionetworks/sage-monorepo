# Base image
ARG imageVersion=jammy-20240808
FROM ubuntu:$imageVersion

# Set safer bash scripts
SHELL ["/bin/bash", "-euxo", "pipefail", "-c"]

# Image arguments
# https://github.com/nektos/act
ARG actVersion="0.2.65"
# https://github.com/sharkdp/hyperfine
ARG hyperfineVersion="1.18.0"
# https://www.npmjs.com/package/@devcontainers/cli
ARG devcontainerCliVersion="0.50.2"
# https://pypi.org/project/poetry
ARG poetryVersion="1.8.3"
# https://docs.posit.co/resources/install-r/#specify-r-version
ARG rVersion="4.2.3"
# https://aquasecurity.github.io/trivy
ARG trivyVersion="0.54.1"
# https://github.com/rstudio/renv
ARG renvVersion="1.0.0"
# https://nodejs.org/en/about/previous-releases
ARG nodeVersionMajor="20"
# https://github.com/pnpm/pnpm/releases
ARG pnpmVersion="9.9.0"
# https://github.com/SonarSource/sonar-scanner-cli/releases
ARG sonarScannerVersion="5.0.1.3006"
# https://github.com/hadolint/hadolint
ARG hadolintVersion="2.12.0"
# The version of this dev container image
ARG devcontainerVersion=""
# The username of the non-root user
ARG user=vscode

# Set environment variables
ENV DEVCONTAINER_VERSION=${devcontainerVersion} \
  LANG=en_US.UTF-8 \
  LC_ALL=en_US.UTF-8

# Install system packages
# hadolint ignore=DL3008,DL3013
RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
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
  && curl -fsSL https://deb.nodesource.com/setup_${nodeVersionMajor}.x -o nodesource_setup.sh \
  && bash nodesource_setup.sh \
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
  # Install additional packages
  && apt-get update -qq -y \
  && apt-get install --no-install-recommends -qq -y nodejs gh ngrok terraform vault \
  # Enable corepack
  && corepack enable \
  # Fix Vault CLI
  # See https://github.com/hashicorp/vault/issues/10924
  && setcap -r /usr/bin/vault \
  # Set up UTF-8 locale
  && echo "en_US.UTF-8 UTF-8" > /etc/locale.gen && locale-gen \
  # Install R (must be done before clearing the apt cache)
  && curl "https://cdn.rstudio.com/r/ubuntu-2204/pkgs/r-${rVersion}_1_amd64.deb" -o /tmp/r_amd64.deb \
    && gdebi --non-interactive /tmp/r_amd64.deb \
    && rm -fr /tmp/r_amd64.deb \
    && ln -s /opt/R/${rVersion}/bin/R /usr/local/bin/R \
    && ln -s /opt/R/${rVersion}/bin/Rscript /usr/local/bin/Rscript \
    && R -e "options(repos = c(POSIT = \"https://packagemanager.posit.co/all/__linux__/jammy/latest\", CRAN = \"https://mirror.las.iastate.edu/CRAN\")); install.packages(\"renv\", version = \"${renvVersion}\")" \
  # Cleanup
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

# Install Poetry
RUN curl -sSL https://install.python-poetry.org | POETRY_HOME=/etc/poetry python3 - --version "${poetryVersion}" \
  && ln -s /etc/poetry/bin/poetry /usr/local/bin/.

# Install Trivy
RUN curl -fsSL "https://github.com/aquasecurity/trivy/releases/download/v${trivyVersion}/trivy_${trivyVersion}_Linux-64bit.deb" -o /tmp/trivy.deb \
  && dpkg -i /tmp/trivy.deb \
  && rm -fr /tmp/trivy.deb

# Install act
RUN curl "https://raw.githubusercontent.com/nektos/act/v${actVersion}/install.sh" | bash -

# Install AWS CLI
RUN curl https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o awscliv2.zip \
  && unzip awscliv2.zip \
  && ./aws/install \
  && rm -fr awscliv2.zip ./aws \
  # Add AWS Session Manager plugin
  && curl -fsSL "https://s3.amazonaws.com/session-manager-downloads/plugin/latest/ubuntu_64bit/session-manager-plugin.deb" -o /tmp/session-manager-plugin.deb \
  && dpkg -i /tmp/session-manager-plugin.deb \
  && rm -fr /tmp/session-manager-plugin.deb

# Install the devcontainer CLI
RUN npm install -g "@devcontainers/cli@${devcontainerCliVersion}"

# Install Hadolint
RUN curl https://github.com/hadolint/hadolint/releases/download/v${hadolintVersion}/hadolint-Linux-x86_64 -o hadolint \
  && mv hadolint /usr/local/bin/. \
  && chmod +x /usr/local/bin/hadolint

# Install hyperfine
RUN curl -sSL "https://github.com/sharkdp/hyperfine/releases/download/v${hyperfineVersion}/hyperfine_${hyperfineVersion}_amd64.deb" \
    -o /tmp/hyperfine.deb \
  && apt-get install --no-install-recommends -qq -y /tmp/hyperfine.deb \
  && rm -fr /tmp/hyperfine.deb

# Create non-root user
RUN useradd -m $user \
  && echo "$user:$user" | chpasswd \
  && groupadd docker \
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

# Switch to non-root user
USER $user

# Install the global version of pnpm
RUN corepack install --global "pnpm@${pnpmVersion}"

# Set up Python environments
ENV PYENV_ROOT /home/${user}/.pyenv
ENV PATH $PYENV_ROOT/shims:$PYENV_ROOT/bin:$PATH
RUN pip install --no-cache-dir pipenv \
  && curl https://raw.githubusercontent.com/pyenv/pyenv-installer/master/bin/pyenv-installer | bash - \
  && pyenv install 3.9.2 3.10.14 3.11.8

CMD ["bash"]