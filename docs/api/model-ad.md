# Model-AD API

**Version:** 1.0.0

## Servers

- **Server**: `http://localhost/v1`

## API Endpoints

This API provides 4 endpoints:

### Dataversion

- **GET** `/dataversion`
  Get dataversion

### Models

- **GET** `/models/{name}`
  Get details for a specific model

  Retrieve detailed information for a specific model by its name

### ComparisonToolConfig

- **GET** `/comparison-tool-config`
  Get Comparison Tool configuration

  Retrieve the Comparison Tool configuration schema for the Model-AD application

### ModelOverview

- **GET** `/comparison-tools/model-overview`
  Get model overview for comparison tools

  Returns a list of model overview objects for use in comparison tools.

## Interactive Documentation

For detailed API documentation with interactive examples, see:

- [model-ad API Docs](https://sage-bionetworks.github.io/sage-monorepo/apps/model-ad/api-docs/)

## OpenAPI Specification

- [OpenAPI Spec (YAML)](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/libs/model-ad/api-description/openapi/openapi.yaml)

---

_This documentation was automatically generated from the OpenAPI specification._
_Last updated: 2025-08-23T22:04:00.347Z_
