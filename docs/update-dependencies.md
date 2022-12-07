# Update Dependencies

## Renovate

This workspace manages dependency updates using [Renovate], which is controlled by a GitHub
workflow. The benefit of Renovate is that it's behavior can be fully customized, unlike with
Dependabot. For example, the following strategies are used to minimize the number of email/in-app
notifications:

- Define when Renovate runs
- Group selected dependency updates in a single PR
- Limit the number of PRs that Renovate can open
- Merge dependency updates automatically, thus avoiding the notification that would have come with
  the creation of a PR.
  > **Note**
  > This strategy is probably viable only for projects that have a high level of test coverage.

Renovate provides this [dashboard] to manage dependency updates. The following section describes the
workflow currently applied to update the dependencies.

## Managing dependency updates

TODO

## Node.js

Identify whether a new version is available for a package.

```console
yarn outdated <package>
```

Update the package.

```console
yarn upgrade <package> --latest
```

Example:

```console
$ yarn outdated @nxrocks/nx-spring-boot
yarn outdated v1.22.19
info Color legend :
 "<red>"    : Major Update backward-incompatible updates
 "<yellow>" : Minor Update backward-compatible features
 "<green>"  : Patch Update backward-compatible bug fixes
Package                 Current Wanted Latest Package Type    URL
@nxrocks/nx-spring-boot 4.0.2   4.1.0  4.1.0  devDependencies https://github.com/tinesoft/nxrocks/blob/master/packages/nx-spring-boot#readme
Done in 4.26s.

$ yarn upgrade @nxrocks/nx-spring-boot --latest
...
success Saved 2 new dependencies.
info Direct dependencies
└─ @nxrocks/nx-spring-boot@4.1.0
info All dependencies
├─ @nxrocks/common@1.1.0
└─ @nxrocks/nx-spring-boot@4.1.0
Done in 162.06s.
```

## Devcontainer

### Update yarn

1. Update the version of `yarn` in the Dockerfile of the devcontainer.
2. Rebuild and restart the devcontainer.
3. Update the version of `yarn` used in the workspace.
    ```console
    yarn set version <version>
    ```

<!-- Links -->

[Renovate]: https://github.com/renovatebot/renovate
[dashboard]: https://github.com/Sage-Bionetworks/challenge-registry/issues/798