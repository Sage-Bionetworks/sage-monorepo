## Workspace setup

### Requirements

- [yarn]

### Create Nx workspace

```console
git config --global commit.gpgsign false  // Because of a bug with nx
yarn create nx-workspace challenge-registry --preset=empty --packageManager=yarn
git config --global commit.gpgsign true
```

<!-- Links -->

[yarn]: https://yarnpkg.com/