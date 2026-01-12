# This file to be sourced in the terminal for development.

export WORKSPACE_DIR=$(pwd)

if [ -e /proc/cpuinfo ]; then # Linux
  WORKSPACE_BUILD_PROCS=$(grep -c ^processor /proc/cpuinfo)
elif [ $(sysctl -n hw.ncpu) ]; then # Mac
  WORKSPACE_BUILD_PROCS=$(sysctl -n hw.ncpu)
else # Other/fail
  WORKSPACE_BUILD_PROCS=4
fi
export WORKSPACE_BUILD_PROCS

# cd to the workspace directory
function workspace-cd {
  cd $WORKSPACE_DIR
}

# Add local npm binaries to PATH
export PATH="$PATH:$WORKSPACE_DIR/node_modules/.bin"

# Alias nx to use pnpm nx to avoid Nx CLI issues
# See: https://github.com/nrwl/nx/issues/30470
# Update: Nx fixed the issue via #33369, which is not released yet.
# Keeping the alias for now to avoid issues until the fix is released.
alias nx='pnpm nx'

function workspace-install-nodejs-dependencies {
  pnpm install --frozen-lockfile
}

function workspace-install-python-dependencies {
  uv sync
}

function workspace-install {
  workspace-install-nodejs-dependencies
  workspace-install-python-dependencies

  nx run-many --target=create-config
  # Projects that can be prepared in parallel
  nx run-many --target=prepare --projects=!tag:language:java
}

function workspace-install-affected {
  workspace-install-nodejs-dependencies
  workspace-install-python-dependencies

  nx affected --target=create-config
  # Projects that can be prepared in parallel
  nx affected --target=prepare --exclude='tag:language:java'
}

# Setup Python virtualenvs
# function challenge-python {
#   nx run-many --parallel --target=python
# }

function workspace-kill-port {
  port="$1"
  pids="$(lsof -t -i:$port)"
  if [ -z "$pids" ]; then
    echo "There are no processes listening to the port $port."
  else
    echo "Killing the processes listening to the port $port."
    kill -9 $pids
  fi
}

function workspace-lint {
  nx run-many --target=lint
}

function workspace-build {
  nx run-many --target=build
}

function workspace-test {
  nx run-many --target=test
}

function workspace-build-images {
  nx run-many --parallel --target=build-image
}

function workspace-graph {
  nx graph
}

function openchallenges-infra {
  node dist/apps/openchallenges/infra/src/main.js "$@"
}

function amp-als-build-images {
  nx run-many --target=build-image --projects=amp-als-* --parallel=3
}

function agora-build-images {
  nx run-many --target=build-image --projects=agora-* --parallel=3
}

function agora-docker-start {
  nx serve-detach agora-apex
}

function agora-docker-stop {
  docker stop $(docker ps -a --filter "name=^/agora-" --format "{{.ID}}")
}

function agora-docker-rm {
  docker rm -f $(docker ps -a --filter "name=^/agora-" --format "{{.ID}}")
}

function agora-test {
  nx run-many --target=test --projects=agora-* --parallel=10
}

function agora-test-affected {
  nx affected --target=test --projects=agora-* --parallel=10
}

function bixarena-build-images {
  nx run-many --target=build-image --projects=bixarena-* --parallel=3
}

function bixarena-docker-start {
  nx serve-detach bixarena-apex
}

function bixarena-docker-rm {
  docker rm -f $(docker ps -a --filter "name=^/bixarena-" --format "{{.ID}}")
}

function observability-build-images {
  nx run-many --target=build-image --projects=observability-* --parallel=3
}

function observability-docker-start {
  nx serve-detach observability-apex
}

function observability-docker-stop {
  docker stop $(docker ps -a --filter "name=^/observability-" --format "{{.ID}}")
}

function observability-docker-rm {
  docker rm -f $(docker ps -a --filter "name=^/observability-" --format "{{.ID}}")
}

function model-ad-build-images {
  nx run-many --target=build-image --projects=model-ad-* --parallel=3
}

function model-ad-docker-start {
  nx serve-detach model-ad-apex
}

function model-ad-docker-stop {
  docker stop $(docker ps -a --filter "name=^/model-ad-" --format "{{.ID}}")
}

function model-ad-docker-rm {
  docker rm -f $(docker ps -a --filter "name=^/model-ad-" --format "{{.ID}}")
}

function openchallenges-build-images {
  nx run-many --target=build-image --projects=openchallenges-* --parallel=3
}

function openchallenges-docker-start {
  nx serve-detach openchallenges-apex
}

function openchallenges-docker-stop {
  docker stop $(docker ps -a --filter "name=^/openchallenges-" --format "{{.ID}}")
}

function openchallenges-docker-rm {
  docker rm -f $(docker ps -a --filter "name=^/openchallenges-" --format "{{.ID}}")
}

function iatlas-build-images {
  nx run-many --target=build-image --projects=iatlas-* --parallel=3
}

function synapse-build-images {
  nx run-many --target=build-image --projects=synapse-* --parallel=3
}

function workspace-nx-cloud-help {
  printf "%s\n" \
    "" \
    "This workspace is not configured to use Nx Cloud. To configure it," \
    "  - Run \`cp nx-cloud.env.example nx-cloud.env\`" \
    "  - Add Nx Cloud credentials to nx-cloud.env (contact thomas.schaffter@sagebionetworks.org)"
}

# Utility function to get comparable semver values.
# See https://apple.stackexchange.com/a/123408/11374
function version { echo "$@" | awk -F. '{ printf("%d%03d%03d%03d\n", $1,$2,$3,$4); }'; }

function check-vscode-version {
  expected="1.108.0"
  actual="$(code --version | head -n 1)"
  if [ $(version $actual) -lt $(version $expected) ]; then
    echo "📦 Please update VS Code (${actual}) to version ${expected} or above."
  fi
}

function workspace-welcome {
  echo "Welcome to Sage Monorepo! 👋"

  if [ ! -d "node_modules" ]; then
    printf "%s\n" \
      "" \
      "Run \`workspace-install\` to install workspace tools like nx and jest."
  fi

  # if [ ! -f "nx-cloud.env" ]; then
  #   workspace-nx-cloud-help
  # fi

  if command -v code &> /dev/null
  then
    check-vscode-version
  fi
}

function workspace-docker-stop {
  num_containers_running=$(docker ps -q | wc -l)
  if [[ $num_containers_running -gt 0 ]]; then
    docker stop $(docker ps -q)
  fi
}

function workspace-unset-empty-nx-env-vars {
  # Get all environment variables that start with "NX_" and have empty values
  local empty_nx_vars=$(env | grep '^NX_' | grep '=$' | cut -d= -f1)

  if [ -n "$empty_nx_vars" ]; then
    while IFS= read -r var_name; do
      if [ -n "$var_name" ]; then
        unset "$var_name"
      fi
    done <<< "$empty_nx_vars"
  fi
}

function workspace-initialize-env {
  workspace-welcome

  # Unset empty NX_ environment variables
  # See https://github.com/nrwl/nx/issues/31921
  workspace-unset-empty-nx-env-vars

  if [ -f "./tools/configure-hostnames.sh" ]; then
    sudo ./tools/configure-hostnames.sh
  fi

  # Needed to run ES containers
  # See https://github.com/Sage-Bionetworks/sage-monorepo/issues/1899
  sudo sysctl -w vm.max_map_count=262144 1> /dev/null

  # Prevent Corepack showing the URL when it needs to download software
  # https://github.com/nodejs/corepack/blob/main/README.md#environment-variables
  export COREPACK_ENABLE_DOWNLOAD_PROMPT="0"
}

function workspace-nuke {
  rm -fr \
    .angular \
    .cache \
    .nx \
    .pnpm-store \
    coverage \
    playwright-report \
    reports

    # find . -name "build" -print0 | xargs -0 rm -fr  # but not OA build folders
    find . -name ".coverage" -print0 | xargs -0 rm -fr
    find . -name ".gradle" -print0 | xargs -0 rm -fr
    find . -name ".pytest_cache" -print0 | xargs -0 rm -fr
    find . -name ".venv" -print0 | xargs -0 rm -fr
    find . -name "bin" -print0 | xargs -0 rm -fr
    find . -name "dist" -print0 | xargs -0 rm -fr
    find . -name "node_modules" -print0 | xargs -0 rm -fr
}