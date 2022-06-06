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

function challenge-db-cli {
  node dist/apps/challenge-db-cli/src/index.js
}

function challenge-seed-db {
  node dist/apps/challenge-db-cli/src/index.js seed "$CHALLENGE_DIR/apps/challenge-db-cli/data/seeds/production/"
}

function challenge-welcome {
  echo "Welcome to the Challenge monorepo! 👋"
}