FROM ubuntu:noble-20250910

# Set safer bash scripts.
SHELL ["/bin/bash", "-euxo", "pipefail", "-c"]

# List of build arguments.
# https://www.npmjs.com/package/@devcontainers/cli
ARG devcontainerCliVersion="0.80.1"
# https://github.com/astral-sh/uv
ARG uvVersion="0.8.22"
# https://docs.posit.co/resources/install-r/#specify-r-version
ARG rVersion="4.5.1"
# https://github.com/gruntwork-io/terragrunt
ARG terragruntVersion="0.87.5"
# https://aquasecurity.github.io/trivy
ARG trivyVersion="0.66.0"
# https://github.com/rstudio/renv
ARG renvVersion="1.1.5"
# https://nodejs.org/en/about/previous-releases
ARG nodeVersionMajor="22"
# https://github.com/pnpm/pnpm/releases
ARG pnpmVersion="10.17.1"
# https://github.com/SonarSource/sonar-scanner-cli/releases
ARG sonarScannerVersion="7.2.0.5079"
# https://github.com/hadolint/hadolint
ARG hadolintVersion="2.14.0"
# https://pinterest.github.io/ktlint/latest/install/cli/#download-and-verification
ARG ktlintVersion="1.7.1"
# https://zlib.net/fossils
ARG zlibVersion="1.3.1"
# The version of this dev container image (specified in devcontainer.json).
ARG devcontainerVersion=""
# The username of the non-root user.
# The user `ubuntu` is available since Ubuntu 24.04.
ENV user="ubuntu"

# Set environment variables.
ENV DEVCONTAINER_VERSION=${devcontainerVersion}
ENV LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8

# Install system packages and other tools.
# hadolint ignore=DL3008
RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    ca-certificates curl git bash-completion gnupg2 lsb-release ssh sudo \
    python3-pip python3-dev python-is-python3 pipx openjdk-17-jdk \
    htop unzip vim wget lsof iproute2 build-essential \
    jq ca-certificates-java gdebi-core \
    # Required by AWS CLI.
    mandoc \
    # Required for setting up locales.
    locales \
    # Required by Hadolint.
    shellcheck \
    # Required by canvas (npm package)
    build-essential libcairo2-dev libpango1.0-dev libjpeg-dev libgif-dev librsvg2-dev \
    # Required to build statically linked binaries.
    musl musl-dev musl-tools \
    # Required by `nx login`
    xdg-utils \
  # Add Node.js repository.
  && curl -fsSL https://deb.nodesource.com/setup_${nodeVersionMajor}.x -o nodesource_setup.sh \
  && bash nodesource_setup.sh \
  # Add GitHub CLI repository.
  && curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | \
    gpg --dearmor -o /usr/share/keyrings/githubcli-archive-keyring.gpg \
  && echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | \
    tee /etc/apt/sources.list.d/github-cli.list > /dev/null \
  # Add OpenTofu repository.
  && install -m 0755 -d /etc/apt/keyrings \
  && curl -fsSL https://get.opentofu.org/opentofu.gpg | tee /etc/apt/keyrings/opentofu.gpg >/dev/null \
  && curl -fsSL https://packages.opentofu.org/opentofu/tofu/gpgkey | gpg --no-tty --batch --dearmor -o /etc/apt/keyrings/opentofu-repo.gpg >/dev/null \
  && chmod a+r /etc/apt/keyrings/opentofu.gpg \
  && echo \
    "deb [signed-by=/etc/apt/keyrings/opentofu.gpg,/etc/apt/keyrings/opentofu-repo.gpg] https://packages.opentofu.org/opentofu/tofu/any/ any main \
    deb-src [signed-by=/etc/apt/keyrings/opentofu.gpg,/etc/apt/keyrings/opentofu-repo.gpg] https://packages.opentofu.org/opentofu/tofu/any/ any main" | \
    tee /etc/apt/sources.list.d/opentofu.list > /dev/null \
  # Install additional packages.
  && apt-get update -qq -y \
  && apt-get install --no-install-recommends -qq -y nodejs gh \
  # Install OpenTofu
  tofu \
  # Enable corepack.
  && corepack enable \
  # Set up UTF-8 locale.
  && echo "en_US.UTF-8 UTF-8" > /etc/locale.gen && locale-gen \
  # Install R (must be done before clearing the apt cache)
  # See https://github.com/rstudio/r-builds
  && curl -fsSL "https://cdn.rstudio.com/r/ubuntu-2404/pkgs/r-${rVersion}_1_amd64.deb" -o /tmp/r_amd64.deb \
    && gdebi --non-interactive /tmp/r_amd64.deb \
    && rm -fr /tmp/r_amd64.deb \
    && ln -s /opt/R/${rVersion}/bin/R /usr/local/bin/R \
    && ln -s /opt/R/${rVersion}/bin/Rscript /usr/local/bin/Rscript \
    && R -e "options(repos = c(POSIT = \"https://packagemanager.posit.co/all/__linux__/jammy/latest\", CRAN = \"https://mirror.las.iastate.edu/CRAN\")); install.packages(\"renv\", version = \"${renvVersion}\")" \
  # Cleanup.
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

# Install Trivy.
RUN curl -fsSL "https://github.com/aquasecurity/trivy/releases/download/v${trivyVersion}/trivy_${trivyVersion}_Linux-64bit.deb" -o /tmp/trivy.deb \
  && dpkg -i /tmp/trivy.deb \
  && rm -fr /tmp/trivy.deb

# Install AWS CLI.
RUN curl -fsSL https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o awscliv2.zip \
  && unzip awscliv2.zip \
  && ./aws/install \
  && rm -fr awscliv2.zip ./aws \
  # Install AWS Session Manager plugin.
  && curl -fsSL "https://s3.amazonaws.com/session-manager-downloads/plugin/latest/ubuntu_64bit/session-manager-plugin.deb" -o /tmp/session-manager-plugin.deb \
  && dpkg -i /tmp/session-manager-plugin.deb \
  && rm -fr /tmp/session-manager-plugin.deb

# Install AWS SAM CLI.
RUN curl -Lo aws-sam-cli-linux-x86_64.zip https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-x86_64.zip \
  && unzip aws-sam-cli-linux-x86_64.zip -d sam-installation \
  && ./sam-installation/install \
  && rm -rf aws-sam-cli-linux-x86_64.zip sam-installation \
  && sam --version

# Install the devcontainer CLI.
RUN npm install -g "@devcontainers/cli@${devcontainerCliVersion}"

# Install Hadolint.
RUN curl -fsSL https://github.com/hadolint/hadolint/releases/download/v${hadolintVersion}/hadolint-Linux-x86_64 -o hadolint \
  && mv hadolint /usr/local/bin/. \
  && chmod +x /usr/local/bin/hadolint

# Install SonarScanner CLI.
ARG SONAR_SCANNER_HOME=/opt/sonar-scanner
ENV SONAR_SCANNER_HOME=${SONAR_SCANNER_HOME} \
  SONAR_USER_HOME=${SONAR_SCANNER_HOME}/.sonar \
  PATH=${SONAR_SCANNER_HOME}/bin:${PATH}
RUN curl -fsSL https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-${sonarScannerVersion}.zip --output sonar-scanner-cli.zip \
  && unzip sonar-scanner-cli.zip \
  && mv sonar-scanner-${sonarScannerVersion} ${SONAR_SCANNER_HOME} \
  && mkdir -p "${SONAR_USER_HOME}" "${SONAR_USER_HOME}/cache" \
  && chown -R ${user}:${user} "${SONAR_SCANNER_HOME}" \
  && chmod -R 777 "${SONAR_USER_HOME}"

# Install ktlint.
RUN curl -sSLO https://github.com/pinterest/ktlint/releases/download/${ktlintVersion}/ktlint \
  && chmod a+x ktlint && mv ktlint /usr/local/bin/

# Install Terragrunt.
RUN curl -fsSL https://github.com/gruntwork-io/terragrunt/releases/download/v${terragruntVersion}/terragrunt_linux_amd64 -o /usr/bin/terragrunt \
  && chmod +x /usr/bin/terragrunt

# Set the user's password.
RUN echo "$user:$user" | chpasswd \
  # Grant passwordless sudo access to all users in the 'sudo' group.
  # This is required for certain development container features, such as
  # Docker-in-Docker, to install and function correctly without user
  # intervention.
  # While this introduces a slight security risk, it is acceptable in a
  # controlled development environment
  && echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

# Build zlib with musl from source
RUN curl -O "https://zlib.net/fossils/zlib-${zlibVersion}.tar.gz" && \
  tar -xzf "zlib-${zlibVersion}.tar.gz" && \
  pushd "zlib-${zlibVersion}" && \
  CC=musl-gcc ./configure --static && \
  make && cp libz.a /usr/lib/x86_64-linux-musl/. && \
  popd && \
  rm -rf "zlib-${zlibVersion}" "zlib-${zlibVersion}.tar.gz"

# Switch to the non-root user.
USER $user

# Install pnpm.
RUN corepack install --global pnpm@"${pnpmVersion}"

# Install uv.
RUN pipx ensurepath \
  && pipx install uv=="${uvVersion}"

# Prepare the user's environment.
RUN usermod --shell /bin/bash $user \
  && printf "%s\n" \
    "" \
    "# source dev-env.sh if found in the current directory" \
    "if [ -f dev-env.sh ]; then" \
    "  . ./dev-env.sh" \
    "  workspace-initialize-env" \
    "fi" \
    "" | tee -a "/home/$user/.bashrc"
