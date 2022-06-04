# Challenge Registry Web App

## Development

Start the REST API server using Docker. This will also start the DB instance
that backs the REST API. This command must be run from the root of the
workspace.

```console
docker compose up challenge-registry-api challenge-keycloak -d
```

Another option is to start the REST API server in development mode if you aim to
contribute to it at the same time you develop this web app. In that case, start
the DB instance using Docker and start the REST API server in development mode.
See the [README of the REST API](../api/README.md) on how to prepare this
project. This command must be run from the root of the workspace.

```console
docker compose up challenge-mongodb challenge-keycloak -d
```

Then, start the REST API server in development mode.

```console
nx server api
```

Either way, the web app can now be started in development mode.

```console
nx serve web-app
```

## TODO

Restore target `test`:

```json
    "test": {
      "executor": "@nrwl/jest:jest",
      "outputs": ["coverage/apps/web-app"],
      "options": {
        "jestConfig": "apps/web-app/jest.config.js",
        "passWithNoTests": true
      }
    },
```