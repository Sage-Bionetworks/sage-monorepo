# Agora Data

## Introduction

This Python project downloads an Agora data release from Synapse, then seeds and indexes the data in a MongoDB.

## Usage

### Prepare the Python environment

```
$ nx prepare agora-data

> nx run agora-data:prepare
TODO: copy output
```

### Run the Python script

```
$ nx serve agora-data

> nx run agora-data:serve
TODO: copy output
```

### Build the image

```
nx build-image agora-data
```

### Run the container "manually"

Run the container after building the image locally:

```
$ docker run --rm ghcr.io/sage-bionetworks/agora-data:local
TODO: copy output
```

### Run the container and its dependencies (MongoDB)

```
$ nx serve-detach agora-data

> nx run agora-data:serve-detach
TODO: copy output
```

### Starting the entire stack

```
nx serve-detach agora-app
```