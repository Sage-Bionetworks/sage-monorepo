# Challenge Registry

[![GitHub CI](https://img.shields.io/github/workflow/status/Sage-Bionetworks/challenge-registry/CI.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&logo=github)](https://github.com/Sage-Bionetworks/challenge-registry/actions)
[![Coverage Status](https://img.shields.io/coveralls/github/Sage-Bionetworks/challenge-registry.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&label=coverage&logo=Coveralls)](https://coveralls.io/github/Sage-Bionetworks/challenge-registry?branch=main)
[![GitHub License](https://img.shields.io/github/license/Sage-Bionetworks/challenge-registry.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&logo=github)](https://github.com/Sage-Bionetworks/challenge-registry/blob/main/LICENSE)
[![Open in Remote - Containers](https://img.shields.io/static/v1?label=Remote%20-%20Containers&message=Open&color=blue&logo=visualstudiocode&style=for-the-badge)](https://vscode.dev/redirect?url=vscode://ms-vscode-remote.remote-containers/cloneInVolume?url=https://github.com/Sage-Bionetworks/challenge-registry "Open in VS Code Remote - Containers")

## Introduction

This monorepo includes the codebase of the Challenge Registry.

## Workspace, Projects and Targets

This workspace was generated using [Nx](https://nx.dev).

The table shown below lists the main *projects* included with this workspace.
Each project is managed using multiple *targets* such as `build`, `test` and
`serve`. One way to execute a target is by using the command `nx <target>
<project>`.

|                  | prepare | lint | lint-fix | build | test | serve | e2e | build-image |
|------------------|---------|------|----------|-------|------|-------|-----|--------|
| apps/api         | ✔️       | ✔️    | ✔️        |       | ✔️    | ✔️     |     | ✔️      |
| apps/api-db      | ✔️       |      |          |       |      | ✔️     |     | ✔️      |
| apps/db-cli      |         | ✔️    | ✔️        | ✔️     | ✔️    | ✔️     |     |        |
| apps/web-app     | ✔️       | ✔️    | ✔️        | ✔️     | ✔️    | ✔️     |     | ✔️      |
| apps/web-app-e2e |         | ✔️    | ✔️        |       |      |       | ✔️   |        |
| libs/api-angular |         | ✔️    | ✔️        | ✔️     | ✔️    |       |     |        |
| libs/api-docs    |         |      |          | ✔️     |      | ✔️     |     |        |
| libs/api-spec    |         | ✔️    |          | ✔️     |      | ✔️     |     |        |

See this [cheat sheet] to learn more about Nx commands.

## Requirements

- [Docker]
- [Node.js] ^14.17.0 or >=16.0.0
- [Yarn] >=1.22

## Usage

### Preparing the workspace

Clone this repository. If you plan to contribute to this project, please create a fork and use its
URL for cloning.  For more information on contributing and/or our Forking Workflow approach, see
[CONTRIBUTING.md](.github/CONTRIBUTING.md).

    git clone --depth 1 <repo url>

Source `dev-env.sh`.

    . ./dev-env.sh

Install Node.js dependencies:

    yarn install --frozen-lockfile

Prepare the development environment.

    challenge-registry-prepare

### Running with Docker

Build the Docker images.

    yarn build-images

Start the Challenge Registry.

    docker compose up -d

Seed the API DB with sample Challenge data.

    nx build db-cli && yarn seed-db

In your browser, open http://localhost.

## Development

The recommended way to contribute to this workspace is to use the [development
container] for VS Code specified by this repository. The dev container provides
all the tools needed (Node.js, Yarn, Python, Docker, etc.). The only requirement
for the host is to have Docker installed. See [how to develop inside the dev
container].

If you prefer to develop on the host, please install these additional
requirements:

- [pyenv] >=2.2
- [pipenv]

Install Python virtualenvs:

    yarn python

Lint the projects.

    yarn lint

Build the projects.

    yarn build

Test the projects.

    yarn test

Start the Challenge Registry.

    yarn start

Seed the API DB with sample Challenge data.

    yarn seed-db

In your browser, open http://localhost:4200.

## Contributing

See [CONTRIBUTING.md](.github/CONTRIBUTING.md).

<!-- Links -->

[cheat sheet]: ./docs/cheat-sheet.md
[Docker]: https://docs.docker.com/get-docker/
[Node.js]: https://nodejs.org/en/
[Yarn]: https://yarnpkg.com/
[pyenv]: https://github.com/pyenv/pyenv
[pipenv]: https://pypi.org/project/pipenv/
[development container]: https://code.visualstudio.com/docs/remote/containers
[how to develop inside the dev container]: docs/devcontainer.md