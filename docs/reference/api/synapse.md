# Synapse REST API

**Version:** 0.1.0

## Servers

- **Server**: `http://localhost/api/v1`

## API Endpoints

This API provides 1 endpoints:

### Challenge

- **GET** `/challenge`
  List the Challenges for which the given participant is registered.
  
  List the Challenges for which the given participant is registered.
To be in the returned list the caller must have READ permission on the
project associated with the Challenge.


## Interactive Documentation

For detailed API documentation with interactive examples, see:

- [synapse API Docs](https://sage-bionetworks.github.io/sage-monorepo/apps/synapse/api-docs/)

## OpenAPI Specification

- [OpenAPI Spec (YAML)](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/libs/synapse/api-description/openapi/openapi.yaml)

---
*This documentation was automatically generated from the OpenAPI specification.*
*Last updated: 2025-08-23T22:04:00.336Z*
