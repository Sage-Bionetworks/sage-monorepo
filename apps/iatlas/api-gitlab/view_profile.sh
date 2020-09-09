#!/bin/bash

GREEN="\033[0;32m"
YELLOW="\033[1;33m"
# No Color
NC='\033[0m'

# The project directory.
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
>&2 echo -e "${GREEN}Current project dir - ${PROJECT_DIR}${NC}"

unset options i
while IFS= read -r -d $'\0' f; do
    options[i++]="$f"
done < <(find ${PROJECT_DIR}/.profiles -maxdepth 1 -type f -name "*.profile" -print0 )
select opt in "${options[@]}" "Quit"; do
    case $opt in
        *.profile)
            echo "Profile file $opt selected"
            snakeviz -H 0.0.0.0 -p ${SNAKEVIZ_PORT} -s $opt
            # processing
        ;;
        "Quit")
            echo "Exiting..."
            break
        ;;
        *)
            echo "This is not a number"
        ;;
    esac
done