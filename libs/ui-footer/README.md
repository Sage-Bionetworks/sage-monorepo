# ui-footer

This library was generated with [Nx](https://nx.dev).

## Running unit tests

Run `nx test ui-footer` to execute the unit tests via [Jest](https://jestjs.io).

## TODO

To restore:

```json
    "lint": {
      "executor": "@nrwl/linter:eslint",
      "outputs": ["{options.outputFile}"],
      "options": {
        "lintFilePatterns": ["libs/ui-footer/**/*.ts"]
      }
    },
    "test": {
      "executor": "@nrwl/jest:jest",
      "outputs": ["coverage/libs/ui-footer"],
      "options": {
        "jestConfig": "libs/ui-footer/jest.config.js",
        "passWithNoTests": true
      }
    }
```