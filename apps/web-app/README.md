# Challenge Registry Web App

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