# Challenge Registry Cheat Sheet

## Overview

This cheat sheet provides an overview of the commands needed when developing in
this monorepo.

## Workspace

The workspace of this monorepo was generated with:

```console
yarn create nx-workspace challenge-registry --preset=empty --packageManager=yarn
```

## Project

Nx projects are created using a "generator". A generator must be installed
before being able to use it. The generators listed in this document are
automatically installed when the npm dependencies of this workspace have been
installed.

    yarn add -D <generator>

Create a new project:

    nx g <generator>:<project-type> <project> [--dry-run]

The option `--dry-run` simulates the generation of the project and shows the
location of the files that would be generated.

Generators used in this workspace:

| Project Type   | Generator Command                    |
|----------------|--------------------------------------|
| Angular app    | `nx g @nrwl/angular:app <project>`   |
| React app      | `nx g @nrwl/react:app <project>`     |
| TypeScript CLI | `nx generate @nrwl/js:app <project>` |
| Angular lib    | `nx g @nrwl/angular:lib <project>`   |

Newly created projects are added to [workspace.json].

## Create an Angular component

    nx g @nrwl/angular:component <component> --project <project>

Example:

Add a component named `header` to the library `web-ui`:

```console
nx g @nrwl/angular:component header --project web-ui --dry-run

CREATE libs/web/ui/src/lib/header/header.component.scss
CREATE libs/web/ui/src/lib/header/header.component.html
CREATE libs/web/ui/src/lib/header/header.component.spec.ts
CREATE libs/web/ui/src/lib/header/header.component.ts
UPDATE libs/web/ui/src/lib/web-ui.module.ts
```

## Create an Angular service

    nx g @nrwl/angular:service page-title --project web-data-access

Example:

Add a service named `page-title` to the library `web-data-access`:

```console
nx g @nrwl/angular:service page-title --project web-data-access --dry-run

CREATE libs/web/data-access/src/lib/page-title.service.spec.ts
CREATE libs/web/data-access/src/lib/page-title.service.ts
```

## Nx

Each project includes the file `project.json` that defines one or more "targets".

Run a single target:

```console
nx <target> <project>  // OR
nx run <project>:<target>
```

Run a given target for multiple projects:

```console
[nx run-many] --target=test --all
nx run-many --target=test --all --parallel  // executes in parallel (default: 3)
nx run-many --target=test --all --parallel=2
nx run-many --target=test --projects=proj1,proj2
```

List the apps and libs that are affected by uncommitted changes.

```console
nx affected:apps
nx affected:libs
```

Run a given target for all the projects affected:

```
nx affected:<target>
nx affected:<target> --parallel
```

### Analyzing & Visualizing Workspaces

Explore the project dependency graph:

    nx graph

    >  NX   Affected criteria defaulted to --base=main --head=HEAD
    >  NX   Project graph started at http://127.0.0.1:4211

    nx affected:graph  // select all the projects affected

<!-- Links -->

[workspace.json]: ../workspace.json