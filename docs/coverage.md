## Code Coverage

### Generating coverage manually

Create a file `.coveralls.yml` which at minimum should contain this one line:

```
repo_token: "coveralls-token-for-your-repo"
```

Push a coverage report to Coveralls.

```console
npx coveralls < coverage/libs/web/about/lcov.info
```