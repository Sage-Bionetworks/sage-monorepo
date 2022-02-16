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
  yarn install --frozen-lockfile
  nx run-many --all --parallel --target=prepare
}
