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

check_status() {
    status_code=$(curl --write-out %{http_code} --silent --output /dev/null localhost:${PORT}/graphiql)
    if [[ ${iterator} -lt 35 && ${status_code} -eq 200 || ${status_code} -eq 302 || ${status_code} -eq 400 ]]
    then
        >&2 echo -e "${GREEN}GraphiQL is Up at localhost:${PORT}/graphiql${NC}"
        open http://localhost:${PORT}/graphiql
    elif [[ ${iterator} -eq 35 ]]
    then
        >&2 echo -e "${YELLOW}Did not work :(${NC}"
    else
        sleep 1
        ((iterator++))
        check_status
    fi
}

iterator=0
check_status

# Open a command line prompt in the container.
docker exec -ti iatlas-api-dev bash