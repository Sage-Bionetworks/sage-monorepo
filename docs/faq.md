# FAQ

## Unable to create Nx workspace because of GPG-related bug

The solution is to temporarily disable the git config `commit.gpgsign`.

```console
git config --global commit.gpgsign false
yarn create nx-workspace challenge-registry --preset=empty --packageManager=yarn
git config --global commit.gpgsign true
```
