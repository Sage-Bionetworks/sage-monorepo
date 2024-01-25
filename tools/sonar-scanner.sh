#!/usr/bin/env bash

# This script scan a project of the monorepo with Sonar.
#
# Requires the environment variable SONAR_TOKEN to be set.
# Run locally, e.g. nx sonar openchallenges-app

# Default variable values
## The Sonar host receiving scan results
sonar_host="https://sonarcloud.io"
## The organization on SonarCloud that owns the project
organization="sage-bionetworks"
## The key of the project on SonarCloud (must match the name of the project in the monorepo)
project_key=""
## The path to the project folder
project_dir=""
## The pull request number (if the scan is triggered by a PR event)
pull_request_number="$SONAR_PULL_REQUEST_NUMBER"

# Function to display script usage
usage() {
 echo "Usage: $0 [OPTIONS]"
 echo "Options:"
 echo " -h, --help                      Display this help message"
 echo " --project-key [key]             The key of the project on SonarCloud"
 echo " --project-dir [path]            The path to the project folder"
 echo " --pull-request-number [number]  The pull request number (if the scan is triggered by a PR event)"
}

has_argument() {
  [[ ("$1" == *=* && -n ${1#*=}) || ( ! -z "$2" && "$2" != -*)  ]];
}

# Note: Does not support --key=value, only --key value
extract_argument() {
  echo "${2:-${1#*=}}"
}

# Function to handle options and arguments
handle_options() {
  while [ $# -gt 0 ]; do
    case $1 in
      -h | --help)
        usage
        exit 0
        ;;
      --project-key*)
        if ! has_argument $@; then
          echo "Project key not specified." >&2
          usage
          exit 1
        fi

        project_key=$(extract_argument $@)

        shift
        ;;
      --project-dir*)
        if ! has_argument $@; then
          echo "Project dir not specified." >&2
          usage
          exit 1
        fi

        project_dir=$(extract_argument $@)

        shift
        ;;
      --pull-request-number*)
        if ! has_argument $@; then
          echo "Pull request number not specified." >&2
          usage
          exit 1
        fi

        pull_request_number=$(extract_argument $@)

        shift
        ;;
      *)
        echo "Invalid option: $1" >&2
        usage
        exit 1
        ;;
    esac
    shift
  done
}

# Main script execution
handle_options "$@"

# Build the scanner options
args=(
  -Dsonar.host.url=$sonar_host
  -Dsonar.organization=$organization
  -Dsonar.projectKey=$project_key
  -Dsonar.sources=$project_dir
  -Dsonar.python.coverage.reportPaths=coverage.xml
)

pull_request_number=2458

# Include the PR key if specified
if [[ ! -z ${pull_request_number+z} ]];
then
  args+=("-Dsonar.pullrequest.key=${pull_request_number}")
fi

# Run the Sonar scanner
sonar-scanner "${args[@]}"
