# Keycloak 3

## Introduction

TODO

## Preparing

```console
nx prepare keycloak
nx docker keycloak
```

## Deploying

```console
nx serve keycloak
```

## Accessing Kecloak Admin Console

- Navigate to http://localhost:8081
- Click on `Administrative Console`
- Enter the credentials defined in `.env`
  - `KEYCLOAK_ADMIN`
  - `KEYCLOAK_ADMIN_PASSWORD`