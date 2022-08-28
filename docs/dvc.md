# Data Version Control

## Overview

## Configuration

The DVC configuration file is located at `.dvc/config`.

## Add an S3 remote

```console
dvc remote add --default gh-challenge s3://gh-challenge
```

The configuration of the S3 remote in `.dvc/config` should look something like the following.

```ini
[core]
    remote = gh-challenge
['remote "gh-challenge"']
    url = s3://gh-challenge
```

## Create S3 bucket




Object URL: https://github-challenge-registry-data.s3.amazonaws.com/74/1d96c652588cd30c73150982b124d4

## References