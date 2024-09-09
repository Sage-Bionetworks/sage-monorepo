# OpenChallenges EDAM ETL

## Introduction

This Python project downloads the EDAM ontolgy from X, processes the data for the needs of OC and
export them to the OC DB.

## Usage

### Prepare the Python environment

```
$ nx prepare openchallenges-edam-etl

> nx run openchallenges-edam-etl:prepare

Creating virtualenv openchallenges-edam-etl in /workspaces/sage-monorepo/apps/openchallenges/edam-etl/.venv
Using virtualenv: /workspaces/sage-monorepo/apps/openchallenges/edam-etl/.venv
Installing dependencies from lock file
Installing the current project: openchallenges-edam-etl (0.1.0)

 ——————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————

 >  NX   Successfully ran target prepare for project openchallenges-edam-etl (2s)
```

### Run the Python script

```
$ nx serve openchallenges-edam-etl

> nx run openchallenges-edam-etl:serve

[TODO] Download EDAM concepts from GitHub or S3 bucket (CSV file)
[TODO] Process the EDAM concepts
[TODO] Push the EDAM concept to the OC challenge service DB

 ——————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————

 >  NX   Successfully ran target serve for project openchallenges-edam-etl
```

### Build the image

```
nx build-image openchallenges-edam-etl
```

### Run the container "manually"

Run the container after building the image locally:

```
$ docker run --rm ghcr.io/sage-bionetworks/openchallenges-edam-etl:local
[TODO] Download EDAM concepts from GitHub or S3 bucket (CSV file)
[TODO] Process the EDAM concepts
[TODO] Push the EDAM concept to the OC challenge service DB
```

### Run the container and its dependencies (MariaDB)

```
$ nx serve-detach openchallenges-edam-etl

> nx run openchallenges-edam-etl:serve-detach

 Container openchallenges-mariadb  Running
 Container openchallenges-edam-etl  Creating
 Container openchallenges-edam-etl  Created
 Container openchallenges-mariadb  Waiting
 Container openchallenges-mariadb  Healthy
 Container openchallenges-edam-etl  Starting
 Container openchallenges-edam-etl  Started

 ——————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————

 >  NX   Successfully ran target serve-detach for project openchallenges-edam-etl (2s)

   View logs and investigate cache misses at https://cloud.nx.app/runs/cy57Bl3y2b

(.venv) vscode@dee30b82cf44:/workspaces/sage-monorepo$ docker logs openchallenges-edam-etl
[TODO] Download EDAM concepts from GitHub or S3 bucket (CSV file)
[TODO] Process the EDAM concepts
[TODO] Push the EDAM concept to the OC challenge service DB
```

### Starting the entire stack

```
nx serve-detach openchallenges-apex
```
