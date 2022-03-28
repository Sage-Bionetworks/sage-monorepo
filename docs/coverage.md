## Code Coverage

### Generating coverage manually

Create a file `.coveralls.yml` which at minimum should contain this one line:

```
repo_token: "coveralls-token-for-your-repo"
```

Generate the coverage report for a project.

```console
nx test web-about
```

Push a coverage report to Coveralls.

```console
npx coveralls < coverage/libs/web/about/lcov.info
```
