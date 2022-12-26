# Challenge Registry API

## Requirements

- [pipenv]

## Usage

Prepare the development environment.

    nx prepare api

Check the code style. The second command will attempts to fix the code style.

    nx lint api
    nx lint-fix api

Generate the api sources using OpenAPI generator.

    nx generate-sources api

Start the MongoDB instance using Docker.

    nx start-db api

Stop the MongoDB instance.

    nx stop-db api

Start the api in watch mode.

    nx serve api

## Select Python environment in VS Code

This project uses [pipenv] to manage the Python and packages versions. You can
install the pipenv environment using one of the following commands.

- From the workspace root folder:

    ```console
    nx prepare api
    ```

- From this project folder:

    ```console
    pipenv install --dev
    pipenv run pip install click==8.0.3 # fix black dependency issue
    ```

In VS Code, select the "PipEnv" environment created.

<div style='float: center'>
  <img style='width: 600px' src="docs/images/vscode-select-pipenv.png"></img>
</div>

<!-- Links -->

[pipenv]: https://pipenv.pypa.io/en/latest/
