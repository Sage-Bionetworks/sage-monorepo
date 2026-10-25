# Agora Data

## Introduction

This Python project downloads an Agora data release from Synapse, then seeds and indexes the data in
MongoDB.

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
  `DATA_VERSION` and all files described within the manifest) and the team images (i.e. all
  files within the folder specified by `TEAM_IMAGES_ID`)
- set `LOAD_TEAM_IMAGES="false"` to skip downloading the team images, e.g. to load faster; the
  photos already in MongoDB stay in place

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

Use `nx serve-detach agora-data` (below), which passes the container its `.env` settings and
connects it to MongoDB.

### Run the container and its dependencies (MongoDB)

```
nx serve-detach agora-data
```

### Starting the entire stack

```
nx serve-detach agora-app
```

### How a release is loaded

The loader uses the shared [explorers data loader](../../../libs/explorers/data-loader/README.md).
It imports the release into staging collections and swaps them in only once every collection has
loaded and been indexed, so a failed or interrupted run leaves the previous release in place, and
the next run cleans up after it. That README describes each step and every log event.

### Reading the logs

The loader writes one JSON object per line. Each run ends with a `run_completed` record that says
whether it succeeded and, if not, which step failed and which release the database now serves. It
is always the last line:

```
docker logs agora-data 2>/dev/null | tail -1
```

To read a run as plain lines instead, set `LOG_FORMAT="text"` in the `.env` file.

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
