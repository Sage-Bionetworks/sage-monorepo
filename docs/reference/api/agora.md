# Agora API

**Version:** 1.0.0

## Servers

- **Server**: `http://localhost/v1`

## API Endpoints

This API provides 11 endpoints:

### BioDomains

- **GET** `/biodomains`
  List BioDomains

- **GET** `/biodomains/{ensg}`
  Retrieve bioDomain for a given ENSG

  Get bioDomain

### Genes

- **GET** `/genes`
  Retrieve a list of genes or filter by Ensembl gene IDs

  This endpoint returns all genes or filters genes by Ensembl gene IDs if provided.

- **GET** `/genes/{ensg}`
  Get gene details by Ensembl Gene ID

- **GET** `/genes/search`
  Search Genes

- **GET** `/genes/comparison`
  Get comparison genes based on category and subcategory

- **GET** `/genes/nominated`
  Get nominated genes

  Retrieves a list of genes with nominations and relevant information.

### Dataversion

- **GET** `/dataversion`
  Get dataversion

### Distribution

- **GET** `/distribution`
  Get distribution data

### Teams

- **GET** `/teams`
  List Teams

- **GET** `/teamMembers/{name}/image`
  Get Team Member Image

## Interactive Documentation

For detailed API documentation with interactive examples, see:

- [agora API Docs](https://sage-bionetworks.github.io/sage-monorepo/apps/agora/api-docs/)

## OpenAPI Specification

- [OpenAPI Spec (YAML)](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/libs/agora/api-description/openapi/openapi.yaml)

---

_This documentation was automatically generated from the OpenAPI specification._
_Last updated: 2025-08-23T22:04:00.359Z_
