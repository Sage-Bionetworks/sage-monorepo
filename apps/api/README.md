# Challenge Registry API

## Requirements

- [pipenv]

## Usage

Prepare the development environment.

    nx prepare api

Generate the api sources using OpenAPI generator.

    nx generate-sources api

Start the MongoDB instance using Docker.

    nx start-db api

Stop the MongoDB instance.

    nx stop-db api

Start the api in development mode.

    nx serve api

<!-- Links -->

[pipenv]: https://pipenv.pypa.io/en/latest/
