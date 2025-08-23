PRs to our repositories are always welcome and can be a quick way to get your
fix or improvement slated for the next release. In general, PRs should:

- Only fix/add the functionality in question **OR** address wide-spread
  whitespace/style issues, not both.
- Add unit or integration tests for fixed or changed functionality (if a test
  suite already exists).
- Address a single concern in the least number of changed lines as possible.
- Include documentation in the repo or on our [docs site].
- Be accompanied by a complete Pull Request template (loaded automatically when
  a PR is created).

For changes that address core functionality or would require breaking changes
(e.g. a major release), it's best to open an Issue to discuss your proposal
first. This is not required but can save time creating and reviewing changes.

In general, we follow the [Forking Workflow]:

1. Fork the repository to your own Github account
2. Clone the project to your machine
3. Create a branch locally with a succinct but descriptive name

   git checkout -b ＜new-branch＞ main

4. Commit changes to the branch
5. Following any formatting and testing guidelines specific to this repo
6. Push changes to your fork
7. Open a PR in our repository and follow the PR template so that we can
   efficiently review the changes.

We recommend that you add this repository as an [upstream remote] to your local
git repository so that you can fetch the latest updates.

```console
$ git remote add upstream https://github.com/Sage-Bionetworks/sage-monorepo.git
$ git remote -v
> ...
> upstream  https://github.com/Sage-Bionetworks/sage-monorepo.git (fetch)
> upstream  https://github.com/Sage-Bionetworks/sage-monorepo.git (push)
```

On your local machine make sure you have the latest version of the `main` branch
from this upstream repository:

```console
git checkout main
git fetch upstream
git rebase upstream/main
```

### Setup Development Environment

This project relies on Node tools and project-specific commands defined in
[package.json] to streamline the development and testing. The command below will
install the required development tools.

Source `dev-env.sh`.

    . dev-env.sh

Prepare the development environment.

    openchallenges-prepare

### Linting

Lint all the projects.

    yarn lint

### Testing

Build and test all the projects.

```console
yarn build
yarn test
```

### Start the OpenChallenges

Start the web app and its dependencies (API, API database).

    yarn start

## Release Procedure

Maintainers are required to follow the procedure below when creating a new
release.

TBA

## Getting Help

Join us on the XXX and post your question to the channel that best matches the
topic of your request.

<!-- Links -->

[upstream remote]: https://help.github.com/en/articles/configuring-a-remote-for-a-fork
[reaction]: https://github.blog/2016-03-10-add-reactions-to-pull-requests-issues-and-comments/
[Forking Workflow]: https://www.atlassian.com/git/tutorials/comparing-workflows/forking-workflow
[package.json]: https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/package.json
[README.md]: ../README.md
