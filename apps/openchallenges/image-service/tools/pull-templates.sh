#!/bin/bash

openapiGeneratorVersion="v7.0.0"
destinationFolder="${PWD}/templates/${openapiGeneratorVersion}"

mkdir -p "${destinationFolder}"

curl -O --output-dir "${destinationFolder}" \
  "https://raw.githubusercontent.com/OpenAPITools/openapi-generator/${openapiGeneratorVersion}/modules/openapi-generator/src/main/resources/JavaSpring/pojo.mustache"