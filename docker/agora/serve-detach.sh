#!/usr/bin/env bash

source docker/agora/compose-files.sh

docker compose "${files[@]}" up $1 --detach --remove-orphans
