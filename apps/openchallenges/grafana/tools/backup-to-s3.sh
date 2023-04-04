#!/usr/bin/env bash

docker run \
  --rm \
  --name grafana-backup-tool \
  --env-file tools/grafana-backup-tool/.env \
  --network openchallenges \
  ysde/docker-grafana-backup-tool:1.2.6-slim