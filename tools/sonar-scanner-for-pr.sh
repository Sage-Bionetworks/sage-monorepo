#!/usr/bin/env bash

if [ $# -lt 1 ]
then
  echo "The argument <project_key> must be specified."
  exit 1
fi

PROJECT_KEY="$1"
SOURCES="$2"
PULL_REQUEST_KEY="$3"
PULL_REQUEST_BRANCH="$4"
PULL_REQUEST_BASE="${5:-main}"

echo "Project key: $PROJECT_KEY"
echo "Sources: $SOURCES"

sonar-scanner \
  -Dsonar.organization=sage-bionetworks \
  -Dsonar.projectKey=$PROJECT_KEY \
  -Dsonar.sources=$SOURCES \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.python.coverage.reportPaths=coverage.xml \
  -Dsonar.pullrequest.key=$PULL_REQUEST_KEY \
  -Dsonar.pullrequest.branch=$PULL_REQUEST_BRANCH \
  -Dsonar.pullrequest.base=$PULL_REQUEST_BASE \