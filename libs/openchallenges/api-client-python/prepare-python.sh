#!/usr/bin/env bash

PYTHON_VERSION="3.10.14"

pyenv install --skip-existing $PYTHON_VERSION
pyenv local $PYTHON_VERSION
poetry env use $PYTHON_VERSION
poetry install --with dev,test