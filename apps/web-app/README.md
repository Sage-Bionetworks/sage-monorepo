# Challenge Registry Web App

## Development

Start the MongoDB instance that hosts the data managed by the challenge registry
API. The following command must be run from the root of the workspace.

    docker compose up challenge-registry-api-db -d

Start the challenge registry REST API in development mode.

    nx serve api


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