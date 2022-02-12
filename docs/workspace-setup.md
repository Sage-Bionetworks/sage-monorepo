## Workspace setup

## Nx references

- [Approved plugins]

### Requirements

- [yarn]

### Create Nx workspace

```console
git config --global commit.gpgsign false  // Because of a bug with nx
yarn create nx-workspace challenge-registry --preset=empty --packageManager=yarn
git config --global commit.gpgsign true
```

### Add Angular web-app

```console
yarn add -D @nrwl/angular
yarn nx g @nrwl/angular:app web-app --mfe --mfeType=host --routing=true
yarn nx g @nrwl/angular:app login --mfe --mfeType=remote --port=4201 --host=web-app --routing=true
nx g @nrwl/angular:lib shared/data-access-user
nx g @nrwl/angular:service user --project=shared-data-access-user
```

### Add api-spec library

```console
yarn add -D @trumbitta/nx-plugin-openapi
yarn add -D @redocly/openapi-cli
```

<!-- Links -->

[yarn]: https://yarnpkg.com/
[Approved plugins]: https://github.com/nrwl/nx/blob/master/community/approved-plugins.json
