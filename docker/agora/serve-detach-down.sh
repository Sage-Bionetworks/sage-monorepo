#!/usr/bin/env bash

source docker/agora/compose-files.sh

docker compose "${files[@]}" down
