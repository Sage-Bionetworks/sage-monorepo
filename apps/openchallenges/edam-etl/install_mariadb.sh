#!/bin/bash
set -e

# Update package list and install MariaDB Connector/C development files
sudo apt-get update
sudo apt-get install -y libmariadb-dev

# Ensure packaging module is installed
pip install packaging

# Install mariadb Python package with PEP 517 disabled
pip install --no-cache-dir --no-use-pep517 mariadb==1.1.10
