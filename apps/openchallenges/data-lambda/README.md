# OpenChallenges Data Lambda

## Build the project

```console
nx build openchallenges-data-lambda
```

## Build the Docker image of the Lambda function

```console
nx build-image openchallenges-data-lambda
```

## Update .env with credentials to utilize Google Sheets API

Before running the Lambda function locally (see next section), update the `.env` file and replace
all "UPDATE_ME" values with real credentials.

Failing to update `.env` will result in the following output during invocation:

```console
{
  "statusCode": 401,
  "body": {
    "message": "Private key not found in the credentials file. Please try again."
  }
}
```

## Start the Lambda function locally with Docker Compose

Starts the Lambda function in the foreground, allowing you to view logs and interact with it
directly.

```console
nx serve openchallenges-data-lambda
```

Starts the Lambda function in detached mode, running it in the background. This is useful if you
want to continue using the terminal for other tasks while the function runs.

```console
nx serve-detach openchallenges-data-lambda
```

## Invoke the Lambda function locally

To invoke the Lambda function after starting it locally, use the following command:

```console
nx run openchallenges-data-lambda:invoke --event <path-to-json-file>
```

Replace `<path-to-json-file>` with the path to your JSON file containing the event payload relative
to the location of the project folder. For example, if your event payload is stored in a file
located at `events/event.json` relative to the project folder:

```console
nx run openchallenges-data-lambda:invoke --event events/event.json
```
