# iAtlas API Testing

[BACK TO MAIN README](../README.md)

The app uses [Pytest](https://docs.pytest.org/) for testing. It implement the [pytest-xdist](https://pypi.org/project/pytest-xdist/) plugin for running test in parallel and on multiple cores.

Coverage is generated using the [pytest-cov](https://pypi.org/project/pytest-cov/) plugin.

The [`.coveragerc`](./.coveragerc) file is used to configure the coverage generation.

Additional assets for the coverage generation (ie css, images, etc) are in the [`coverage_assets/`](./coverage_assets/) folder.

To run a test module simple run:

```bash
pytest path/to/the/test_file.py -n auto
```

An individual test may be run in the same manner with:

```bash
pytest path/to/the/test_file.py::name_of_test_function -n auto
```

To generate coverage html run:

```bash
pytest --cov --cov-report html -n auto
```

The `-n auto` at the end of each command is for running on multiple cores. `auto` will automatically determine the number of cores to use. Otherwise, one may specify the number explicitly.

## Example Queries

See: [README.md](./../example_queries/README.md) in the [`example_queries`](./../example_queries/) folder
