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

### Editing KC data

1. Remove `data/h2/*.db` files.
2. Start KC so that it can save its data to `data/h2`.
   ```console
   docker compose -f docker-compose.yml -f docker-compose.edit.yml up
   ```
3. Login into KC Admin Console.
4. Edit KC data.
5. Stop KC.

### Exporting realms and users

1. Export KC data to `data/import`.
   ```console
   docker compose -f docker-compose.yml -f docker-compose.export.yml up
   ```
2. Fix the exported realm files (need to be done only once).
     - In `test-realm.json`, replace `"policies" : [{...}, {...}],` by
       `"policies" : [],`.