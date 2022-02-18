```json
    "lint": {
      "executor": "@nrwl/linter:eslint",
      "outputs": ["{options.outputFile}"],
      "options": {
        "lintFilePatterns": ["apps/web-app-react-e2e/**/*.{js,ts}"]
      }
    }
```