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

function workspace-install {
  yarn install --immutable
  nx run-many --target=create-config
  nx run-many --target=prepare --projects=tag:language:java --parallel=1
  nx run-many --target=prepare --projects=tag:language:python --projects=tag:language:r
}

function workspace-install-affected {
  yarn install --immutable
  nx affected --target=create-config
  nx affected --target=prepare --exclude '!tag:language:java' --parallel=1
  nx affected --target=prepare --exclude 'tag:language:java'
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
    kill $pids
  fi
}

function workspace-lint {
  nx run-many --target=lint
}

function workspace-lint-html {
  nx run-many --target=lint-html
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

function model-ad-build-images {
  nx run-many --target=build-image --projects=model-ad-* --parallel=3
}

function openchallenges-build-images {
  nx run-many --target=build-image --projects=openchallenges-* --parallel=3
}

function schematic-build-images {
  nx run-many --target=build-image --projects=schematic-* --parallel=3
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
  expected="1.85.2"
  actual="$(code --version | head -n 1)"
  if [ $(version $actual) -lt $(version $expected) ]; then
    echo "📦 Please update VS Code (${actual}) to version ${expected} or above."
  fi
}

function workspace-welcome {
  echo "Welcome to Sage monorepo! 👋"

  if [ ! -d "node_modules" ]; then
    printf "%s\n" \
      "" \
      "Run \`workspace-install\` to install workspace tools like nx and jest."
  fi

  if [ ! -f "nx-cloud.env" ]; then
    workspace-nx-cloud-help
  fi

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

function workspace-initialize-env {
  workspace-welcome

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
