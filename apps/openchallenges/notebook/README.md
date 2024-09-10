# OpenChallenges Notebook

## Overview

This project includes a collection of Jupyter notebooks that interact with OpenChallenges (OC) REST
API and other related APIs.

## Update Python version

1. Update the version of Python in
   - `.python-version`
   - `prepare-python.sh`
   - `pyproject.toml`
2. Run `nx prepare openchallenges-notebook`

## Generate challenge headlines

```
cd apps/openchallenges/notebook
poetry run python src/challenge_headline/generate_challenge_headlines.py
```
