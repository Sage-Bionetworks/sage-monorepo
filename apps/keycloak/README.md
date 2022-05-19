# Keycloak

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

## Development

### Exporting realms and users

1. Start KC so that it can save its data to `data/h2`.
   ```console
   docker compose -f docker-compose.yml -f docker-compose.edit.yml up
   ```
2. Login into KC Admin Console.
3. Edit KC configuration.
4. Stop KC.
5. Export KC data to `data/import`.
   ```console
   docker compose -f docker-compose.yml -f docker-compose.export.yml up
   ```