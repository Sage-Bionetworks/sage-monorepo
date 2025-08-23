# BixArena API

**Version:** 1.0.0

Advance bioinformatics by evaluating and ranking AI agents.

## Servers

- **Server**: `http://localhost/v1`

## API Endpoints

This API provides 4 endpoints:

### Leaderboard

- **GET** `/leaderboards`
  List all available leaderboards

  Get a list of all available leaderboards with their metadata

- **GET** `/leaderboards/{leaderboardId}`
  Get leaderboard entries

  Get paginated leaderboard entries for a specific leaderboard

- **GET** `/leaderboards/{leaderboardId}/history/{modelId}`
  Get model performance history

  Get historical performance data for a specific model in a leaderboard

- **GET** `/leaderboards/{leaderboardId}/snapshots`
  Get leaderboard snapshots

  Get a paginated list of available snapshots for a leaderboard

## Interactive Documentation

For detailed API documentation with interactive examples, see:

- [bixarena API Docs](https://sage-bionetworks.github.io/sage-monorepo/apps/bixarena/api-docs/)

## OpenAPI Specification

- [OpenAPI Spec (YAML)](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/libs/bixarena/api-description/openapi/openapi.yaml)

---

_This documentation was automatically generated from the OpenAPI specification._
_Last updated: 2025-08-23T22:04:00.349Z_
