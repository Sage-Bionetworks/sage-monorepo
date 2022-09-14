#!/bin/bash

openapi-generator-cli generate \
  -g spring \
  -o server \
  -i docs/openapi.yaml