# Developing inside a Container

## Overview

This document describes how to configure and develop in the dev container
specified by this repository. The goal is to provide developers with the same
development environment that is mainly isolated from their host. This method
promotes reproducibility and remove the need to install dependencies on the host
such as Node.js or Python. For additional information on VS Code development
containers, see [Developing inside a Container].

## Requirements

- [Docker]
- [VS Code] and the extension [Remote - Containers]

## Quick Start

Open the workspace folder in VS Code after cloning it. VS Code should invite you
to open the folder in the dev container specified by this repository.
Alternatively:

- Press `F1` or click on `Help` > `Show All Commands`
- Enter `Remote-Containers: Open Folder in Container...`

That's it! VS Code should now have open the workspace folder in the dev
container as indicated by the following green button displayed in the
bottom-left corner of VS Code.

<img src="images/vscode-dev-container.png" height="26">

To reopen the workspace folder locally, click on the above green button and
select `Reopen Folder Locally` or `Reopen Folder in WSL` when developing on
Windows with WSL.

## Sharing Git credentials with the container

To run `git` commands that require authentication inside the dev container,
please follow the instructions described in [Sharing Git credentials with your
container].

If this repository has been closed using an SSH key, follow the instructions to
share your host SSH keys with the dev container. The command below should list
the same keys when executed on the host and inside the container.

    ssh-add -l

[Signing commits requires a GPG key]. Follow the above instructions to share the
host GPG keys with the dev container. The command below should now list the same
keys when executed on the host and inside the container.

    gpg --list-keys

<!-- Links -->

[Developing inside a Container]: https://code.visualstudio.com/docs/remote/containers
[Docker]: https://docs.docker.com/get-docker/
[VS Code]: https://code.visualstudio.com/
[Remote - Containers]: https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers
[Sharing Git credentials with your container]: https://code.visualstudio.com/docs/remote/containers#_sharing-git-credentials-with-your-container
[Signing commits requires a GPG key]: https://docs.github.com/en/authentication/managing-commit-signature-verification/adding-a-new-gpg-key-to-your-github-account