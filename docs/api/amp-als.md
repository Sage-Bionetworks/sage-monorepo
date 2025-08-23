# AMP-ALS API

**Version:** 1.0.0

Discover, explore, and contribute to AMP-ALS datasets.

## Servers

- **Server**: `http://localhost/v1`

## API Endpoints

This API provides 3 endpoints:

### Dataset

- **GET** `/datasets`
  List datasets

- **GET** `/datasets/{datasetId}`
  Get a dataset
  
  Returns the dataset specified

### HealthCheck

- **GET** `/healthcheck`
  Get health check information
  
  Get information about the health of the service

## Interactive Documentation

For detailed API documentation with interactive examples, see:

- [amp-als API Docs](https://sage-bionetworks.github.io/sage-monorepo/apps/amp-als/api-docs/)

## OpenAPI Specification

- [OpenAPI Spec (YAML)](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/libs/amp-als/api-description/openapi/openapi.yaml)

---
*This documentation was automatically generated from the OpenAPI specification.*
*Last updated: 2025-08-23T22:04:00.355Z*
