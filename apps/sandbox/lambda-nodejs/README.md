# Sandbox Lambda Node.js

## Build the project

```console
nx build sandbox-lambda-nodejs
```

## Build the Docker image of the Lambda function

```console
nx build-image sandbox-lambda-nodejs
```

## Start the Lambda function locally with Docker Compose

Starts the Lambda function in the foreground, allowing you to view logs and interact with it
directly.

```console
nx serve sandbox-lambda-nodejs
```

Starts the Lambda function in detached mode, running it in the background. This is useful if you
want to continue using the terminal for other tasks while the function runs.

```console
nx serve-detach sandbox-lambda-nodejs
```

## Invoke the Lambda function locally

To invoke the Lambda function after starting it locally, use the following command:

```console
nx run sandbox-lambda-nodejs:invoke --event <path-to-json-file>
```

Replace `<path-to-json-file>` with the path to your JSON file containing the event payload relative
to the location of the project folder. For example, if your event payload is stored in a file
located at `events/event.json` relative to the project folder:

```console
nx run sandbox-lambda-nodejs:invoke --event events/event.json
```
