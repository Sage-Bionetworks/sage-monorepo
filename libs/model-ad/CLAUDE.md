# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

`libs/model-ad/` is a collection of Angular libraries for the Model-AD data platform, a Sage Bionetworks tool for exploring Alzheimer's disease model data.

## Commands

```bash
nx test model-ad-<lib-name>   # run unit tests for a lib
nx lint model-ad-<lib-name>   # lint a lib
```

For serving the Model-AD app, see the "Serving an App" section in the root `CLAUDE.md` — the full stack must be running first.

## Testing Conventions

Mock data and test fixtures come from `@sagebionetworks/model-ad/testing`.

## Library Map

| Nx Project                                         | Path                                       | Purpose                                                                                          |
| -------------------------------------------------- | ------------------------------------------ | ------------------------------------------------------------------------------------------------ |
| `model-ad-api-description`                         | `api-description/`                         | OpenAPI specs (source of truth for the API)                                                      |
| `model-ad-api-client-angular`                      | `api-client-angular/`                      | **Generated** Angular HTTP client — do not hand-edit                                             |
| `model-ad-config`                                  | `config/`                                  | Route paths, colors, help URLs, boxplot styles                                                   |
| `model-ad-ui`                                      | `ui/`                                      | Shared presentational components (BoxplotComponent, BoxplotsGridComponent, SearchInputComponent) |
| `model-ad-util`                                    | `util/`                                    | Header and footer navigation link definitions                                                    |
| `model-ad-home`                                    | `home/`                                    | Landing page with stats and search                                                               |
| `model-ad-gene-details`                            | `gene-details/`                            | Gene details page with boxplots and RNA data                                                     |
| `model-ad-model-details`                           | `model-details/`                           | Model details page with omics, biomarkers, pathology panels                                      |
| `model-ad-differential-expression-comparison-tool` | `differential-expression-comparison-tool/` | Differential expression heatmap comparison tool                                                  |
| `model-ad-disease-correlation-comparison-tool`     | `disease-correlation-comparison-tool/`     | Disease correlation heatmap comparison tool                                                      |
| `model-ad-model-overview-comparison-tool`          | `model-overview-comparison-tool/`          | Model overview comparison tool                                                                   |
| `model-ad-services`                                | `services/`                                | Angular services (currently minimal)                                                             |
| `model-ad-styles`                                  | `styles/`                                  | Shared SCSS variables and mixins                                                                 |
| `model-ad-testing`                                 | `testing/`                                 | Mock data and test fixtures                                                                      |

## Dependency Layers

Libraries depend only downward:

```
Feature/route components (home, gene-details, model-details, *-comparison-tool)
         ↓
      ui / util
         ↓
        config
         ↓
  api-client-angular  (generated from api-description)
         ↓
  testing / styles / services  (infrastructure)
```

All imports use `@sagebionetworks/model-ad/<lib-name>` path aliases. Feature components also import heavily from `@sagebionetworks/explorers/*` for shared comparison tool infrastructure.

## OpenAPI Workflow

API contracts live in `api-description/`. The Angular HTTP client in `api-client-angular/` is fully generated -- never edit it directly.

After modifying any OpenAPI schema, always regenerate **all** generated clients -- not just `model-ad-api-description`. The `generate` target on client projects depends on `model-ad-api-description:build` automatically, so a single command handles the full chain:

```bash
# After any OpenAPI schema change, regenerate everything
nx run-many -t=generate -p=model-ad-*

# Bundle the OpenAPI specs only (rarely needed standalone)
nx build model-ad-api-description
```

`redocly.yaml` defines four APIs (`api-public`, `api-service`, `api-next-public`, `api-next-service`) filtered by `x-audience` annotations in the YAML specs.

## Comparison Tool Pattern

The three `*-comparison-tool` libraries all follow the same pattern: they wrap `ComparisonToolComponent` from `@sagebionetworks/explorers/comparison-tool` and provide it with a `ComparisonToolConfig` and `ComparisonToolViewConfig`. Each has its own service that fetches data via the generated API client and transforms it into the config the explorers component expects.
