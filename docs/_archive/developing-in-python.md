# Developing in Python

## Devcontainer Python version

The devcontainer used to contribute to this monorepo comes with one Python environment. The version
of Python provided by this environment is as of May 19, 2023:

```console
$ python --version
Python 3.9.2
```

## Managing the Python environment of a project

By default, creating a new Python project in this monorepo with Poetry will use the Python version
provided by the devcontainer. However, for various reasons, this Python version may not be
compatible with the Python requirement of a given project.

Let's take the project `schematic-api` as an example. The dependencies of this project are managed
with Poetry. The project is currently configured to use Python 3.9.2. However, we now need to update
the version of Python used by this project to 3.11.0 to solve a recent issue.

First, we need to move to the project folder:

```console
mv apps/schematic/api
```

Install the Python env `3.11.0` with `pyenv`:

```console
pyenv install 3.11.0
```

Activate the new environment:

```console
pyenv local 3.11.0
```

Check that the new env is active:

```console
$ pyenv versions
  system
* 3.11.0 (set by /workspaces/sage-monorepo/apps/schematic/api/.python-version)
```

Update the Python version specified in the project files `pyproject.toml`:

```console
poetry env use 3.11.0
```

Update the Python version specified in the lock file:

```console
poetry lock --no-update
```

Build the project with the new Python env:

```console
nx build schematic-api
```