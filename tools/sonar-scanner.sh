#!/usr/bin/env bash

if [ $# -lt 1 ]
then
  echo "The argument <project_key> must be specified."
  exit 1
fi

PROJECT_KEY="$1"
SOURCES="${2:-$PWD}"
BRANCH_NAME="$3"

echo "Project key: $PROJECT_KEY"
echo "Sources: $SOURCES"

sonar-scanner \
  -Dsonar.organization=sage-bionetworks \
  -Dsonar.projectKey=$PROJECT_KEY \
  -Dsonar.sources=$SOURCES \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.python.coverage.reportPaths=coverage.xml \
  -Dsonar.branch.name=$BRANCH_NAME