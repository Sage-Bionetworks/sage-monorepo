# FAQ

## Unable to create Nx workspace because of GPG-related bug

The solution is to temporarily disable the git config `commit.gpgsign`.

```console
git config --global commit.gpgsign false
yarn create nx-workspace challenge-registry --preset=empty --packageManager=yarn
git config --global commit.gpgsign true
```

## Task `api:lint` fails

```console
✖  nx run api:lint
    Loading .env environment variables...
    Error: the command black could not be found within PATH or Pipfile's [scripts].
    ERROR: Something went wrong in @nrwl/run-commands - Command failed: pipenv run black ./openapi_server --check --exclude '(models|test)'
```

Run `nx python api` to create the Python virtualenv and install the tools
needed.