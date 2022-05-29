# Keycloak

## Introduction

TODO

## Preparing

```console
nx prepare challenge-keycloak
nx docker challenge-keycloak
```

## Deploying

```console
nx serve challenge-keycloak
```

## Accessing Kecloak Admin Console

- Navigate to http://localhost:8080
- Click on `Administrative Console`
- Enter the credentials defined in `.env`
  - `KEYCLOAK_ADMIN`
  - `KEYCLOAK_ADMIN_PASSWORD`

## Development

### Importing realms and users from files

Import development data from JSON file and generate data in `./data/h2/`. This
command must be run when the Keycloak server is not running.

```console
nx import-dev-data challenge-keycloak
```

### Editing KC data

Start Keycloak in development mode and make edits.

```console
nx serve challenge-keycloak
```

### Exporting realms and users

Export development data from `./data/h2/` to JSON files. This command must be
run when the Keycloak server is not running.

```console
nx export-dev-data challenge-keycloak
```