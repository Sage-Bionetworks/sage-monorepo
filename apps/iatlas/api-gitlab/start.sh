#!/bin/bash

# Set the environment variables.
source ./set_env_variables.sh

build=false

# If the `-b` flag is passed, set build to true.
while getopts b: flag; do
    case ${flag} in
        b) build=true;;
    esac
done

if [ build = true ]
then
    # Build and start the container.
    docker-compose up -d --build
else
    # Start the container.
    docker-compose up -d
fi

# Open a command line prompt in the container.
docker exec -ti iatlas-api-dev bash