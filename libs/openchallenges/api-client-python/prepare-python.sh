#!/usr/bin/env bash

pyenv install --skip-existing 3.9.2
pyenv local 3.9.2
poetry env use 3.9.2
poetry install