#!/bin/bash

# Check if a prefix argument was provided
if [ -z "$1" ]; then
  echo "Please provide a project prefix (e.g., agora-)."
  exit 1
fi

# Prefix provided by the user
PREFIX=$1

# Find all projects that start with the provided prefix
PROJECTS=$(nx show projects | grep "^$PREFIX" | tr '\n' ',' | sed 's/,$//')

# Check if any projects were found
if [ -z "$PROJECTS" ]; then
  echo "No projects found with the prefix '$PREFIX'."
  exit 1
fi

# Run the tests for the filtered projects
nx run-many --target=test --projects=$PROJECTS
