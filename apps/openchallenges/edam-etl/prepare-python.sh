#!/usr/bin/env bash

PYTHON_VERSION="3.12.2"

pyenv install --skip-existing $PYTHON_VERSION

# Initializing pyenv again solves an issue encountered by GitHub action where the version of Python
# installed above is not detected.
eval "$(pyenv init -)"

pyenv local $PYTHON_VERSION
poetry env use $PYTHON_VERSION
poetry install
# poetry install --with prod,dev