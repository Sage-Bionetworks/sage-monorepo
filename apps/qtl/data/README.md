# QTL Data

## Introduction

This Python project downloads a QTL data release from Synapse, then seeds and indexes the data in
MongoDB.

## Usage

### Prepare the Python environment

```
nx prepare qtl-data
```

### Configure Data Release and Credentials

```
nx create-config qtl-data
```

In the `.env` file,

- update `SYNAPSE_AUTH_TOKEN` to a Synapse PAT with view/download permissions for a user that can
  download the data release (i.e. the data manifest file and all files described within it)

The release to load is the `local` entry of `src/data/data-releases.json`. To load a different
release, edit that entry without committing it. `nx serve` reads the file directly; for
`nx serve-detach`, rebuild the image first (`nx build-image qtl-data`).

### Start MongoDB then Run the Python script

```
nx serve-detach qtl-mongo
nx serve qtl-data
```

### Build the image

```
nx build-image qtl-data
```

### Run the container "manually"

Use `nx serve-detach qtl-data` (below), which passes the container its `.env` settings and
connects it to MongoDB.

### Run the container and its dependencies (MongoDB)

```
nx serve-detach qtl-data
```

### Starting the entire stack

```
nx serve-detach qtl-app
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
docker logs qtl-data 2>/dev/null | tail -1
```

To read a run as plain lines instead, set `LOG_FORMAT="text"` in the `.env` file.

### Cleaning the database

Data will persist in MongoDB via a Docker volume when the `qtl-mongo` container is restarted, so
this service will only need to be run when initally setting up the qtl stack or after changing a
data release version. If a clean install is needed, then follow these steps before restarting the
stack:

```bash
# stop and remove the qtl-mongo container
docker stop qtl-mongo
docker rm qtl-mongo

# remove qtl-mongo-data volume
docker volume rm qtl-mongo-data

# restart stack
nx serve-detach qtl-app
```
