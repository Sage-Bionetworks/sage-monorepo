#!/usr/bin/env bash

pyenv install --skip-existing 3.9.2
pyenv local 3.9.2
poetry env use 3.9.2
poetry run pip install "cython<3.0.0"
poetry run pip install --no-build-isolation pyyaml==5.4.1
poetry install --with dev