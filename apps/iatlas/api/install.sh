#!/usr/bin/env bash

PYTHON_VERSION=$(cat ".python-version")

pyenv install --skip-existing $PYTHON_VERSION

# Initializing pyenv again solves an issue encountered by GitHub action where the version of Python
# installed above is not detected.
eval "$(pyenv init -)"

pyenv local $PYTHON_VERSION
poetry env use $(pyenv which python)
poetry install --with dev