# Dev Container

## Overview

This document describes how to configure and develop in the dev container
specified by this repository. The goal is to provide developers with the same
development environment that is mainly isolated from their host. This method
promotes reproducibility and remove the need to install dependencies on the host
such as Node.js or Python. For additional information on VS Code development
containers, see [Developing inside a Container].

## Requirements

- [VS Code]
- VS Code extensions
  - [Remote - Containers]

## Quick Start

Open the workspace folder in VS Code after cloning it. VS Code should invite you
to open the folder in the dev container specified by this repository.
Alternatively:

- Press `F1` or click on `Help` > `Show All Commands`
- Enter `Remote-Containers: Open Folder in Container...`

That's it! VS Code should now have open the workspace folder in the dev
container as indicated by the following green button displayed in the
bottom-left corner of VS Code.

![](images/vscode-dev-container.png | width=200)


<!-- Links -->

[Developing inside a Container]: https://code.visualstudio.com/docs/remote/containers
[VS Code]: https://code.visualstudio.com/
[Remote - Containers]: https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers