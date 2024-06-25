#!/bin/bash

# Expects the following environment variables to be set: 
# - DB_HOST - mongo db host
# - DB_USER - mongo db root username
# - DB_PASS - mongo db root password
# - DB_NAME - mongo db name
# - COLLECTIONS_FILE - path to file that maps collection names to file names
# - DATA_DIR - path to directory that contains collections data
# - TEAM_IMAGES_DIR - path to directory that contains team images files
# - CREATE_INDEXES_FILE - path to file that creates mongo db indexes

# Abort on error: https://bertvv.github.io/cheat-sheets/Bash.html#writing-robust-scripts-and-debugging
set -o errexit   # abort on nonzero exitstatus
set -o nounset   # abort on unbound variable
set -o pipefail  # don't hide errors within pipes

# Import collections into mongodb
echo "importing collections"
while IFS=',' read -r collection filename
do
    echo "importing ${collection} from ${filename}"
    mongoimport \
      -h "${DB_HOST}" -d "${DB_NAME}" -u "${DB_USER}" -p "${DB_PASS}" \
      --authenticationDatabase admin \
      --collection "${collection}" \
      --jsonArray --drop \
      --file "${DATA_DIR}/${filename}"
done < "${COLLECTIONS_FILE}"

# Create indexes
echo "creating indexes"
mongosh \
  --host "${DB_HOST}" -u "${DB_USER}" -p "${DB_PASS}" \
  --authenticationDatabase admin \
  "${CREATE_INDEXES_FILE}"

# Import images
echo "importing images"
pushd "${TEAM_IMAGES_DIR}"
ls -1r *.{jpg,jpeg,png} | while read x; do mongofiles -h "${DB_HOST}" -d "${DB_NAME}" -u "${DB_USER}" -p "${DB_PASS}" --authenticationDatabase admin -v put $x; echo $x; done
popd