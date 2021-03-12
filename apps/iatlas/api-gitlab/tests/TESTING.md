# iAtlas API Testing

[BACK TO MAIN README](../README.md)

The app uses [Pytest](https://docs.pytest.org/) for testing. It implement the [pytest-xdist](https://pypi.org/project/pytest-xdist/) plugin for running test in parallel and on multiple cores.

Coverage is generated using the [pytest-cov](https://pypi.org/project/pytest-cov/) plugin.

The [`.coveragerc`](./.coveragerc) file is used to configure the coverage generation.

Additional assets for the coverage generation (ie css, images, etc) are in the [`coverage_assets/`](./coverage_assets/) folder.

**NOTE:** If running tests outside of the container and the app hasn't been started yet, the environment variables won't have been set yet. To set the environment variables run the following in the same terminal as the tests before executing the tests at the root of the project folder. (Please note the dot(`.`) at the very beginning of the command. This will "source" the script.):

```sh
. set_env_variables.sh
```

To run a test module simple run:

```sh
pytest path/to/the/test_file.py -n auto
```

An individual test may be run in the same manner with:

```sh
pytest path/to/the/test_file.py::name_of_test_function -n auto
```

To generate coverage html run:

```sh
pytest --cov --cov-report html -n auto
```

The `-n auto` at the end of each command is for running on multiple cores. `auto` will automatically determine the number of cores to use. Otherwise, one may specify the number explicitly.

## Example Queries

See: [README.md](./../example_queries/README.md) in the [`example_queries`](./../example_queries/) folder
