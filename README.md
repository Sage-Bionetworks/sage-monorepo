# Challenge

[![GitHub CI](https://img.shields.io/github/workflow/status/Sage-Bionetworks/challenge-registry/CI.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&logo=github)](https://github.com/Sage-Bionetworks/challenge-registry/actions)
[![GitHub License](https://img.shields.io/github/license/Sage-Bionetworks/challenge-registry.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&logo=github)](https://github.com/Sage-Bionetworks/challenge-registry/blob/main/LICENSE)
<!-- [![Coverage Status](https://img.shields.io/coveralls/github/Sage-Bionetworks/challenge-registry.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&label=coverage&logo=Coveralls)](https://coveralls.io/github/Sage-Bionetworks/challenge-registry?branch=main) -->

## Introduction

This [Nx monorepo](https://nx.dev/) includes the codebase of the Challenge ecosystem.

## Projects

The list of all the applications and libraries developed is available [here](docs/projects.md).

## Requirements

- [Docker Engine] version **18.06.0** or newer (including [Compose V2][compose-v2])
- [Visual Studio Code] version **1.68.1** or newer

## Usage

### Opening the workspace in VS Code

Click on this badge to open the workspace in VS Code using our development container.

[![Open in Remote - Containers](https://img.shields.io/static/v1?label=Remote%20-%20Containers&message=Open&color=blue&logo=visualstudiocode&style=for-the-badge)](https://vscode.dev/redirect?url=vscode://ms-vscode-remote.remote-containers/cloneInVolume?url=https://github.com/Sage-Bionetworks/challenge-registry "Open in VS Code Remote - Containers")

If you plan to contribute to this project, please create a fork and use its URL for cloning. For
more information on contributing and/or our Forking Workflow approach, see
[CONTRIBUTING.md](.github/CONTRIBUTING.md).

    git clone --filter=blob:none <repo url>

Then open your fork repo inside our devcontainer using these instructions:

- [Developing inside a Container](./docs/devcontainer.md)
- [Develop on a remote host](./docs/develop-on-a-remote-host.md) (optional)

### Preparing the workspace

- Run `challenge-install` to install workspace tools like `nx` and `jest`.
- Run `challenge-nx-cloud-help` to start configuring Nx Cloud (optional).

### Preparing the databases

- Run `nx import-dev-data challenge-registry-keycloak` to seed Keycloak database.

> **Warning** Keycloak must not be running when executing this command.

### Starting the Challenge Registry

    nx serve challenge-registry-app

You can access the Challenge Registry on http://localhost:4200.

## Documentation

Checkout the folder [docs](./docs) to learn more about this monorepo.

## License

[Apache License 2.0]

## Core Team

- [Thomas Schaffter](https://github.com/tschaffter)
- [Verena Chung](https://github.com/vpchung)
- [Rongrong Chai](https://github.com/rrchai)

<!-- <a href="https://github.com/Sage-Bionetworks/challenge-registry/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Sage-Bionetworks/challenge-registry" width="20%"/>
</a> -->

<!-- Links -->

[Docker Engine]: https://docs.docker.com/get-docker/
[compose-v2]: https://docs.docker.com/compose/cli-command/
[Visual Studio Code]: https://code.visualstudio.com/
[Apache License 2.0]: https://github.com/Sage-Bionetworks/challenge-registry/blob/main/LICENSE.txt
