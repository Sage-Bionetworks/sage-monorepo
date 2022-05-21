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

### Importing realms and users from files

1. Import KC data from `data/import`.
   ```console
   docker compose -f docker-compose.yml -f docker-compose.import.yml up
   ```

### Editing KC data

1. Start KC so that it can save its data to `data/h2`.
   ```console
   docker compose up
   ```
2. Login into KC Admin Console.
3. Edit KC data.
4. Stop KC.

### Exporting realms and users

1. Export KC data to `data/import`.
   ```console
   docker compose -f docker-compose.yml -f docker-compose.export.yml up
   ```
2. Fix the exported realm files (need to be done only once).
     - In `test-realm.json`, replace `"policies" : [{...}, {...}],` by
       `"policies" : [],`.