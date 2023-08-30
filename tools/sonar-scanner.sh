#!/usr/bin/env bash

PROJECT_KEY="$1"
SOURCES="$2"

echo "Scanning $PROJECT_KEY"
echo "Sources: $SOURCES"

sonar-scanner \
  -Dsonar.organization=sage-bionetworks \
  -Dsonar.projectKey=$PROJECT_KEY \
  -Dsonar.sources=$SOURCES \
  -Dsonar.host.url=https://sonarcloud.io