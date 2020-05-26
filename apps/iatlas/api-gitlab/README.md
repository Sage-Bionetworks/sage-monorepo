# iAtlas API

A GraphQL API that serves data from the iAtlas Data Database. This is built in Python and developed and deployed in Docker.

## Dependencies

- [Docker Desktop](https://www.docker.com/products/docker-desktop) (`docker`)
- [Visual Studio Code](https://code.visualstudio.com/) (`code`) - this is optional, but sure makes everything a lot easier.

## Development

The first time you checkout the project, run the following command to build the docker image, start the container, and start the API:

```bash
./start.sh
```

This will build the Docker image and run the container. Once the container is created, the Flask server will be started. Then a command prompt should open from within the container (looks like: `bash-5.0#`).

The GrapiQL playground interface should open automatically in your browser.

**Note:** If you get _'Version in "./docker-compose.yml" is unsupported.'_, please update your version of Docker Desktop.

**Optional:** If you choose to use VS Code, you can use the [Remote-Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) extension to develop from within the container itself. Using this approach, you don't need to install Python or any dependencies (besides Docker and VS Code itself) as everything is already installed inside the container. There is a volume mapped to your user .ssh folder so that your ssh keys are available inside the container as well as your user .gitconfig file. The user folder inside the container is also mapped to a volume so that it persists between starts and stops of the container. This means you may create a .bash_profile or similar for yourself within the container and it will persist between container starts and stops.

To exit the container's command prompt, type `exit` and enter. This will bring you back to your local command prompt.

The following command will stop the server and container:

```bash
./stop.sh
```

Restart the container with the following command:

```bash
./start.sh
```

If there are changes made to the container or image, first, stop the container `./stop.sh`, then rebuild it and restarted it with `./start.sh -b build`.

Now head on over to
[http://localhost:5000/graphiql](http://localhost:5000/graphiql)
and run some queries!
