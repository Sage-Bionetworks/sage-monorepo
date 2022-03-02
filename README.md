# Challenge Registry

[![GitHub CI](https://img.shields.io/github/workflow/status/Sage-Bionetworks/challenge-registry/CI.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&logo=github)](https://github.com/Sage-Bionetworks/challenge-registry/actions)
[![GitHub License](https://img.shields.io/github/license/Sage-Bionetworks/challenge-registry.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&logo=github)](https://github.com/Sage-Bionetworks/challenge-registry/blob/main/LICENSE)

## Introduction

This monorepo includes the codebase of the Challenge Registry.

## Workspace, Projects and Targets

This workspace was generated using [Nx](https://nx.dev).

The table shown below lists the main *projects* included with this workspace.
Each project is managed using multiple *targets* such as `build`, `test` and
`serve`. One way to execute a target is by using the command `nx <target>
<project>`.

|                  | prepare | lint | lint-fix | build | test | serve | e2e | docker |
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

### Running with Docker

Clone this repository.

    git clone --depth 1 https://github.com/Sage-Bionetworks/challenge-registry.git

Source `dev-env.sh`.

    . dev-env.sh

Prepare the development environment.

    challenge-registry-prepare

Build the Docker images.

    yarn docker

Start the Challenge Registry.

    docker compose up -d

Seed the API DB with sample Challenge data.

    nx build db-cli && yarn seed-db

In your browser, open http://localhost.

## Development

Additional dependencies required:

- [pipenv]

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
[pipenv]: https://pypi.org/project/pipenv/
