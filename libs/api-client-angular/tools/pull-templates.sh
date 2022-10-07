#!/bin/bash

openapiGeneratorVersion="6.1.x"
destinationFolder="${PWD}/templates/v${openapiGeneratorVersion}"

mkdir -p "${destinationFolder}"

curl -O --output-dir "${destinationFolder}" \
  "https://raw.githubusercontent.com/OpenAPITools/openapi-generator/${openapiGeneratorVersion}/modules/openapi-generator/src/main/resources/typescript-angular/api.module.mustache"