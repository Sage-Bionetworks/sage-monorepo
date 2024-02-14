# Updating tools and dependencies

- [Renovate](#Renovate)
- [Dependency update workflow](#Dependency-update-workflow)
    - [Exceptions](#Exceptions)
    - [Dev container](#Dev-container)
    - [Yarn](#Yarn)

## Renovate

This workspace manages dependency updates using [Renovate], which is controlled by a GitHub
workflow. The main benefit of Renovate is that its behavior can be fully customized, unlike
Dependabot's. For example, the following strategies are used to minimize the number of email/in-app
notifications:

- Define when Renovate runs
- Group several dependency updates in a single PR
- Limit the number of PRs that Renovate can open at a given time
- Merge dependency updates automatically, thus avoiding the notification that would have come with
  the creation of a PR.
  > **Note**
  > This strategy is probably viable only for projects that have a high level of test coverage.

Renovate provides this [dashboard] to manage dependency updates. The following section describes the
workflow currently applied to update the dependencies.

## Dependency update workflow

> **Note**
> This workflow is a work in progress.

1. Go to Renovate's [dashboard].
2. Review the dependency updates listed in the section "Open", for which a PR is already open.
    > **Note** Currently, Renovate is configured to open up to 5 PRs for the sake of reducing
    > notifications.
3. Merge the PRs that you are confident will not break the workspace.
    > **Note** For PRs that affect projects with low test coverage, it is recommended to checkout
    > the branch locally and manually test that the main projects still work (e.g. the Challenge
    > Registry app).
4. Trigger the creation of PRs from the dashboard for dependencies listed in the section
   "Rate-Limited" (minor and patch updates) that you are confident you can merge according to the
   criteria listed in Step 3.
5. Trigger the creation of PRs from the dashboard for dependencies listed in the section "Pending
   Approval" (major updates) that you are confident you can merge according to the criteria listed
   in Step 3.

### Exceptions

The following tools must be updated manually as described. Continuing to ask Renovate to report on
updates available for these dependencies is still benefitial. One improvement could be to prevent
Renovate from automatically opening PRs for these tools. Once dependencies have been manually
updated, Renovate will automatically remove the corresponding items from the dashboard.

- Major updates of Nx, Angular and Jest must be applied using Nx [`migrate` tool]. Nx will take care
  of updating its version number as well as the version number of supported tools like Angular and
  Jest. Nx will also modify files like the `project.json` files.
- Node.js and Yarn are installed as part of the dev container. Renovate's attempts to update these
  tools are currently making the CI/CD workflow fail because Renovate does not detect their version
  numbers defined in the dev container Dockerfile.

### Dev container

The workflow used to update the dev container is described in this
[ticket](https://github.com/Sage-Bionetworks/sage-monorepo/issues/975).

### Yarn

1. Update the version of `yarn` in the Dockerfile of the devcontainer.
2. Rebuild and restart the devcontainer.
3. Update the version of `yarn` used in the workspace.
    ```console
    yarn set version <version>
    ```

## Attic

### Node.js

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

<!-- Links -->

[Renovate]: https://github.com/renovatebot/renovate
[dashboard]: https://github.com/Sage-Bionetworks/sage-monorepo/issues/798
[`migrate` tool]: https://nx.dev/core-features/automate-updating-dependencies