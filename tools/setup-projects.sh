#!/usr/bin/env bash

# Runs tasks to set up all projects for a particular stack.
# $1 - The stack name, e.g. agora.

if [ "$#" -ne 1 ]; then
  echo "Please pass the stack name as an argument".
  exit
fi

stack="$1"
echo "Running setup tasks for ${stack} projects."

projects="${1}-*"

nx run-many --target=create-config --projects="${projects}"
# Projects that can be prepared in parallel
nx run-many --target=prepare --exclude='tag:language:java' --projects="${projects}"
# Java projects must be installed one at a time
nx run-many --target=prepare --exclude='!tag:language:java' --parallel=1 --projects="${projects}"
