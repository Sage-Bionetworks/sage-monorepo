#!/usr/bin/env bash

# The script must be run from its project directory.

PYTHON_VERSION=$(cat ".python-version")

pyenv install --skip-existing $PYTHON_VERSION

# Initializing pyenv again solves an issue encountered by GitHub action where the version of Python
# installed above is not detected.
eval "$(pyenv init -)"

pyenv local $PYTHON_VERSION
poetry env use $PYTHON_VERSION
poetry install --with prod,dev

sudo apt-get update
sudo apt-get install -y libmariadb-dev
pip install packaging
