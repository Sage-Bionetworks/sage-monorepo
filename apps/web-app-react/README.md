```json
    "lint": {
      "executor": "@nrwl/linter:eslint",
      "outputs": ["{options.outputFile}"],
      "options": {
        "lintFilePatterns": ["apps/web-app-react/**/*.{ts,tsx,js,jsx}"]
      }
    },
    "test": {
      "executor": "@nrwl/jest:jest",
      "outputs": ["coverage/apps/web-app-react"],
      "options": {
        "jestConfig": "apps/web-app-react/jest.config.js",
        "passWithNoTests": true
      }
    }
```