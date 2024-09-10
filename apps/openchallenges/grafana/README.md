# OpenChallenges Grafana

## Overview

[Grafana] is a multi-platform open source analytics and interactive visualization web application.
It provides charts, graphs, and alerts for the web when connected to supported data sources.

### Data Sources

OpenChallenges supports these data sources:

- [Prometheus] to capture compute metrics and logs of Spring components (e.g. microservices, API
  gateway, config server).

## Usage

### Preparation

Create the config files with:

```console
nx create-config openchallenges-grafana
```

The default config file (`.env` located in this project folder) can be used as-is.

### Starting Grafana

```console
nx serve-detach openchallenges-grafana
```

### Open Grafana Web Console

1. Open your browser and navigate to http://localhost:3000.
2. Authenticate with the credentials defined in `.env`.

## Save and restore Grafana data

### Create a Grafana API key

Create a Grafana API key for an Admin user.

```console
$ nx create-api-key openchallenges-grafana

{"id":3,"name":"grafana-backup-tool","key":"eyJrIjoiaURiZEZ3Um1ycVdVWlUwd0YzRHNraThmZWZMWjV6U1MiLCJuIjoiYXBpa2V5Y3VybCIsImlkIjoxfQ=="}
```

If the API key already exists, delete it first using Grafana Web Console:

- Gear icon in the left menu > API keys > Delete the key named "grafana-backup-tool".

In `tools/grafana-backup-tool/.env`, set the value of the property `GRAFANA_TOKEN` to the value of the API key.

> **Note** The properties `GRAFANA_ADMIN_ACCOUNT` and `GRAFANA_ADMIN_PASSWORD` may seem redundant to
> specifying the API key, but they are actually required when restoring Grafana data.

### Specify the credentials to the AWS S3 bucket

In `tools/grafana-backup-tool/.env`, set the value of `AWS_ACCESS_KEY_ID` and
`AWS_SECRET_ACCESS_KEY` so that the [Grafana backup tool] can save and/or restore data to/from the
S3 bucket. The Grafana backup tool requires Docker to be installed.

> **Note** Members of the OpenChallenges team can find the AWS credentials in LastPass (item:
> `openchallenges-grafana`).

### Backup Grafana data to the S3 bucket

The Grafana container must be running in order to backup its data.

```
$ nx save-data openchallenges-grafana

...
created archive at: _OUTPUT_/202303292349.tar.gz
Upload archives to S3:
Upload to S3 was successful
```

### Restore Grafana data from the S3 bucket

The Grafana container must be running in order to restore the data. The ID of the backup to restore
is defined in the script `tools/restore-from-s3.sh`.

```
nx restore-data openchallenges-grafana
```

> **Note** Existing Grafana resources cannot be overwritten by the Grafana backup tool. The solution
> is to wipe Grafana data clean, e.g. by removing Grafana container and volume.

```console
docker rm -f openchallenges-grafana
docker volume rm openchallenges-grafana-data
```

<!-- Links -->

[grafana]: https://grafana.com/
[Prometheus]: https://prometheus.io/
[Grafana backup tool]: https://github.com/ysde/grafana-backup-tool
