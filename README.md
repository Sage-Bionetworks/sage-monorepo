# Challenge

[![GitHub CI](https://img.shields.io/github/workflow/status/Sage-Bionetworks/challenge-registry/CI.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&logo=github)](https://github.com/Sage-Bionetworks/challenge-registry/actions)
[![Coverage Status](https://img.shields.io/coveralls/github/Sage-Bionetworks/challenge-registry.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&label=coverage&logo=Coveralls)](https://coveralls.io/github/Sage-Bionetworks/challenge-registry?branch=main)
[![GitHub License](https://img.shields.io/github/license/Sage-Bionetworks/challenge-registry.svg?color=007acc&labelColor=555555&logoColor=ffffff&style=for-the-badge&logo=github)](https://github.com/Sage-Bionetworks/challenge-registry/blob/main/LICENSE)

## Overview

This [Nx monorepo](https://nx.dev/) includes the codebase of the Challenge
ecosystem.

TODO: Add brief description of the apps and services

## Requirements

- [Docker]
- [Visual Studio Code]

## Usage

### Opening the workspace in VS Code

Click on this badge to open the workspace in VS Code using our development
container.

[![Open in Remote - Containers](https://img.shields.io/static/v1?label=Remote%20-%20Containers&message=Open&color=blue&logo=visualstudiocode&style=for-the-badge)](https://vscode.dev/redirect?url=vscode://ms-vscode-remote.remote-containers/cloneInVolume?url=https://github.com/Sage-Bionetworks/challenge-registry "Open in VS Code Remote - Containers")

If you plan to contribute to this project, please create a fork and use its URL
for cloning. For more information on contributing and/or our Forking Workflow
approach, see [CONTRIBUTING.md](.github/CONTRIBUTING.md).

    git clone --depth 1 <repo url>

Then open your fork repo inside our devcontainer using these instructions:

- [Developing inside a Container](./docs/devcontainer.md)
- [Develop on a remote host](./docs/develop-on-a-remote-host.md) (optional)

### Preparing the workspace

- Run `challenge-install` to install workspace tools like `nx` and `jest`.
- Run `nx import-dev-data challenge-keycloak` to seed Keycloak database.
- Run `challenge-nx-cloud-help` to start configuring Nx Cloud (optional).

### Starting the Challenge Registry in watch mode

    challenge-registry-serve

You can access the Challenge Registry on http://localhost:4200.

## Documentation

Checkout the folder [docs](./docs) to learn more about this monorepo.

## License

[Apache License 2.0]

<!-- Links -->

[Docker]: https://docs.docker.com/get-docker/
[Visual Studio Code]: https://code.visualstudio.com/
[Apache License 2.0]: https://github.com/Sage-Bionetworks/challenge-registry/blob/main/LICENSE.txt
