#!/bin/bash

# Set the environment variables.
source ./set_env_variables.sh

build=false

# If the `-b or --build` flag is passed, set build to true.
while [ ! $# -eq 0 ]
do
    case "$1" in
        --build | -b)
            >&2 echo -e "${GREEN}Build requested${NC}"
            build=true
        ;;
    esac
    shift
done

if [ "${build}" = true ]
then
    # Build and start the container.
    docker-compose up -d --build
else
    # Start the container.
    docker-compose up -d
fi

check_status() {
    status_code=$(curl --write-out %{http_code} --silent --output /dev/null localhost:${FLASK_RUN_PORT}/graphiql)
    if [[ ${iterator} -lt 35 && ${status_code} -eq 200 || ${status_code} -eq 302 || ${status_code} -eq 400 ]]
    then
        >&2 echo -e "${GREEN}GraphiQL is Up at localhost:${FLASK_RUN_PORT}/graphiql${NC}"
        open http://localhost:${FLASK_RUN_PORT}/graphiql
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