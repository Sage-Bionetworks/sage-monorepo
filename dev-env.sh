# This file to be sourced in the terminal for development.

export CHALLENGE_REGISTRY_DIR=$(pwd)

if [ -e /proc/cpuinfo ]; then # Linux
  CHALLENGE_REGISTRY_BUILD_PROCS=$(grep -c ^processor /proc/cpuinfo)
elif [ $(sysctl -n hw.ncpu) ]; then # Mac
  CHALLENGE_REGISTRY_BUILD_PROCS=$(sysctl -n hw.ncpu)
else # Other/fail
  CHALLENGE_REGISTRY_BUILD_PROCS=4
fi
export CHALLENGE_REGISTRY_BUILD_PROCS

# cd to the workspace directory
function challenge-registry-cd {
  cd $CHALLENGE_REGISTRY_DIR
}

# Add local npm binaries to PATH
export PATH="$PATH:$(yarn bin)"

function challenge-registry-prepare {
  nx run-many --all --parallel --target=prepare
}

# Setup Python virtualenvs
function challenge-registry-python {
  nx run-many --all --parallel --target=python
}

function challenge-registry-lint {
  nx run-many --all --target=lint
}

function challenge-registry-build {
  nx run-many --all --target=build
}

function challenge-registry-test {
  nx run-many --all --target=test
}

function challenge-registry-serve {
  nx run-many --target=serve --projects=api-db,api,web-app
}

function challenge-registry-docker {
  nx run-many --all --parallel --target=docker
}

function challenge-registry-seed-db {
  yarn db-cli seed "$CHALLENGE_REGISTRY_DIR/apps/db-cli/data/seeds/production/"
}
