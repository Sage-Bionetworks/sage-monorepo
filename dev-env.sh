# This file to be sourced in the terminal for development.

export CHALLENGE_DIR=$(pwd)

if [ -e /proc/cpuinfo ]; then # Linux
  CHALLENGE_BUILD_PROCS=$(grep -c ^processor /proc/cpuinfo)
elif [ $(sysctl -n hw.ncpu) ]; then # Mac
  CHALLENGE_BUILD_PROCS=$(sysctl -n hw.ncpu)
else # Other/fail
  CHALLENGE_BUILD_PROCS=4
fi
export CHALLENGE_BUILD_PROCS

bold=$(tput bold)
italic=$(tput sitm)
reset=$(tput sgr0)

orange=$(tput setaf 166)


# cd to the workspace directory
function challenge-cd {
  cd $CHALLENGE_DIR
}

# Add local npm binaries to PATH
export PATH="$PATH:$(yarn bin)"

function challenge-install {
  yarn install --frozen-lockfile
}

function challenge-prepare {
  nx run-many --all --parallel --target=prepare
}

# Setup Python virtualenvs
# function challenge-python {
#   nx run-many --all --parallel --target=python
# }

function challenge-lint {
  nx run-many --all --target=lint
}

function challenge-lint-html {
  nx run-many --all --target=lint-html
}

function challenge-build {
  nx run-many --all --target=build
}

function challenge-test {
  nx run-many --all --target=test
}

function challenge-build-images {
  nx run-many --all --parallel --target=build-image
}

function challenge-graph {
  nx graph
}

function challenge-registry-serve {
  nx serve challenge-registry
}

function challenge-elk-serve-detach {
  nx serve-detach challenge-kibana
}

function challenge-db-cli {
  node dist/apps/challenge-db-cli/src/index.js
}

function challenge-seed-db {
  node dist/apps/challenge-db-cli/src/index.js seed "$CHALLENGE_DIR/apps/challenge-db-cli/data/seeds/production/"
}

function challenge-nx-cloud-help {
  printf "%s\n" \
    "" \
    "This workspace is not configured to use Nx Cloud. To configure it," \
    "  - Run ${bold}cp nx-cloud.env.example nx-cloud.env${reset}" \
    "  - Add Nx Cloud credentials to ${italic}nx-cloud.env${reset} (contact thomas.schaffter@sagebionetworks.org)"
}

function challenge-welcome {
  echo "Welcome to the Challenge monorepo! 👋"

  if [ ! -d "node_modules" ]; then
    printf "%s\n" \
      "" \
      "Run ${bold}challenge-install${reset} to install workspace tools like ${bold}nx${reset} and ${bold}jest${reset}." \
      "Run this command each time ${italic}package-lock.json${reset} may have changed."
  fi

  if [ ! -f "nx-cloud.env" ]; then
    challenge-nx-cloud-help
  fi
}

function challenge-docker-stop {
  docker stop $(docker ps -q)
}

function challenge-initialize-env {
  challenge-welcome

  if [ -f "./tools/configure-hostnames.sh" ]; then
    sudo ./tools/configure-hostnames.sh
  fi
}