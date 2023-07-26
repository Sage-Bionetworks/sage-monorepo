#!/usr/bin/env bash

PYTHON_VERSION="3.10.0"

pyenv install --skip-existing $PYTHON_VERSION

# Initializing pyenv again solves an issue encountered by GitHub action where the version of Python
# installed above is not detected.
eval "$(pyenv init -)"


pyenv local $PYTHON_VERSION
poetry env use $PYTHON_VERSION
poetry run pip install "cython<3.0.0"
poetry run pip install --no-build-isolation pyyaml==5.4.1
poetry install --with prod,dev