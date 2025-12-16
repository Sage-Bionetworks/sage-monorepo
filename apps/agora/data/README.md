# Agora Data

## Introduction

This Python project downloads an Agora data release from Synapse, then seeds and indexes the data in MongoDB.

## Usage

### Prepare the Python environment

```
nx prepare agora-data
```

### Configure Data Release and Credentials

```
nx create-config agora-data
```

In the `.env` file,

- specify the desired data release using `DATA_FILE` and `DATA_VERSION`
- update `SYNAPSE_AUTH_TOKEN` to a Synapse PAT with view/download permissions for a user that can
  download the data release (i.e. the data manifest file specified by `DATA_FILE` and
  `DATA_VERSION` and all files described within the manifest) and the team image files (i.e. all
  files within the folder specified by `TEAM_IMAGES_ID`)

### Start MongoDB then Run the Python script

```
nx serve-detach agora-mongo
nx serve agora-data
```

### Build the image

```
nx build-image agora-data
```

### Run the container "manually"

Run the container after building the image locally:

```
docker run --rm ghcr.io/sage-bionetworks/agora-data:local
```

### Run the container and its dependencies (MongoDB)

```
nx serve-detach agora-data
```

### Starting the entire stack

```
nx serve-detach agora-app
```

### Cleaning the database

Data will persist in MongoDB via a Docker volume when the `agora-mongo` container is restarted, so
this service will only need to be run when initally setting up the agora stack or after changing a
data release version. If a clean install is needed, then follow these steps before restarting the
stack:

```bash
# stop and remove the agora-mongo container
docker stop agora-mongo
docker rm agora-mongo

# remove agora-mongo-data volume
docker volume rm agora-mongo-data

# restart stack
nx serve-detach agora-app
```
