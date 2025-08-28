# OpenChallenges API

**Version:** 1.0.0

Discover, explore, and contribute to open biomedical challenges.

## Servers

- **Server**: `https://openchallenges.io/api/v1`

## API Endpoints

This API provides 17 endpoints:

### Authentication

- **POST** `/auth/login`
  User login

  Authenticate user and return JWT token

### API Key

- **GET** `/auth/api-keys`
  List API keys

  Get all API keys for the authenticated user

- **POST** `/auth/api-keys`
  Create API key

  Generate a new API key for the authenticated user

- **DELETE** `/auth/api-keys/{keyId}`
  Delete API key

  Revoke an API key

### Challenge

- **GET** `/challenges`
  List challenges

- **POST** `/challenges`
  Create a challenge

  Create a challenge with the specified details

- **GET** `/challenges/{challengeId}`
  Get a challenge

  Returns the challenge specified

- **PUT** `/challenges/{challengeId}`
  Update an existing challenge

  Updates an existing challenge.

- **DELETE** `/challenges/{challengeId}`
  Delete a challenge

  Deletes a challenge by its unique ID.

- **GET** `/challenges/{challengeId}/json-ld`
  Get a challenge in JSON-LD format

  Returns the challenge specified in JSON-LD format

### Challenge Contribution

- **GET** `/challenges/{challengeId}/contributions`
  List challenge contributions

- **POST** `/challenges/{challengeId}/contributions`
  Create a new contribution for a challenge

  Creates a new contribution record associated with a challenge ID.

- **DELETE** `/challenges/{challengeId}/contributions/{organizationId}/role/{role}`
  Delete a specific challenge contribution

  Delete a specific challenge contribution.

### Challenge Analytics

- **GET** `/challenge-analytics/challenges-per-year`
  Get the number of challenges tracked per year

  Returns the number of challenges tracked per year

### Challenge Platform

- **GET** `/challenge-platforms`
  List challenge platforms

- **POST** `/challenge-platforms`
  Create a challenge platform

  Create a challenge platform with the specified ID

- **GET** `/challenge-platforms/{challengePlatformId}`
  Get a challenge platform

  Returns the challenge platform identified by its unique ID

- **PUT** `/challenge-platforms/{challengePlatformId}`
  Update an existing challenge platform

  Updates an existing challenge platform.

- **DELETE** `/challenge-platforms/{challengePlatformId}`
  Delete a challenge platform

  Deletes a challenge platform by its unique ID. This action is irreversible.

### Edam Concept

- **GET** `/edam-concepts`
  List EDAM concepts

### Image

- **GET** `/images`
  Get an image

  Returns the image specified.

### Organization

- **GET** `/organizations`
  List organizations

- **POST** `/organizations`
  Create an organization

  Create an organization with the specified account name

- **GET** `/organizations/{org}`
  Get an organization

  Returns the organization identified by its login or ID.

- **PUT** `/organizations/{org}`
  Update an existing organization

  Updates an existing organization.

- **DELETE** `/organizations/{org}`
  Delete an organization

  Deletes the organization specified by its login or ID.

## Authentication

- **apiBearerAuth**: http - API key obtained from /auth/login endpoint

## Interactive Documentation

For detailed API documentation with interactive examples, see:

- [openchallenges API Docs](https://sage-bionetworks.github.io/sage-monorepo/apps/openchallenges/api-docs/)

## OpenAPI Specification

- [OpenAPI Spec (YAML)](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/libs/openchallenges/api-description/openapi/openapi.yaml)

---

_This documentation was automatically generated from the OpenAPI specification._
_Last updated: 2025-08-23T22:04:00.344Z_
