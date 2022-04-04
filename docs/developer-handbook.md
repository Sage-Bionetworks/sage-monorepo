# Developer Handbook

## Developer Workflow

Embracing the monorepo-style development often requires some changes to the
development workflow.

Our CI should run the following checks:

- It checks that the changed code is formatted properly (`nx format:check`).
- It runs lint checks for all the projects affected by a PR/commit.
- It runs unit tests for all the projects affected by a PR/commit.
- It runs e2e tests for all the apps affected by a PR/commit.
- It rebuilds all the apps affected by a PR/commit.

**Note all the projects affected by a PR/commit.** This is very important.
Monorepo-style development only works if we rebuild, retest, and relint only the
projects that can be affected by our changes. If we instead retest everything,
we will get the the following problems:
