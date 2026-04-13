# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

`libs/explorers/` is a collection of Angular libraries that provide reusable UI components, services, and utilities for Sage Bionetworks data exploration tools.

## Library Map

| Nx Project                  | Path               | Purpose                                                                                      |
| --------------------------- | ------------------ | -------------------------------------------------------------------------------------------- |
| `explorers-comparison-tool` | `comparison-tool/` | Root feature component — see its own `CLAUDE.md`                                             |
| `explorers-charts`          | `charts/`          | Core chart utilities and models (framework-agnostic)                                         |
| `explorers-charts-angular`  | `charts-angular/`  | Angular wrappers for charts; has Storybook (port 4400)                                       |
| `explorers-ui`              | `ui/`              | Shared presentational components (header, footer, hero, search input, download utils)        |
| `explorers-util`            | `util/`            | Lower-level utilities (loading containers, tooltips, modals, SVG icons, wiki component)      |
| `explorers-shared`          | `shared/`          | Route definitions and shared page components (error, not-found, terms-of-service, wiki-hero) |
| `explorers-models`          | `models/`          | Pure TypeScript interfaces — no runtime dependencies                                         |
| `explorers-services`        | `services/`        | Angular services for state management                                                        |
| `explorers-constants`       | `constants/`       | App-wide constants, injection tokens, debounce durations, toast messages                     |
| `explorers-testing`         | `testing/`         | Test utilities: mock data, MSW handlers, provider factories                                  |
| `explorers-config`          | `config/`          | Runtime configuration management                                                             |
| `explorers-sentry`          | `sentry/`          | Sentry error tracking integration                                                            |
| `explorers-themes`          | `themes/`          | SCSS theme variables (no public TS API)                                                      |
| `explorers-styles`          | `styles/`          | Shared SCSS styles                                                                           |
| `explorers-storybook`       | `storybook/`       | Storybook host (port 4402)                                                                   |

## Commands

```bash
nx test explorers-<lib-name>               # run unit tests for a lib
nx lint explorers-<lib-name>               # lint a lib
nx start explorers-storybook       # serve explorers Storybook (port 4402)
nx run explorers-charts-angular:storybook  # serve charts Storybook (port 4400)
```

## Testing Conventions

Tests use `@testing-library/angular`. The `testing` lib provides:

- MSW handlers (`handlers/`) for mocking HTTP requests
- Provider factories (`providers/`) for injection token setup
- Mock data and e2e fixtures

## Dependency Layers

Libraries depend only downward:

```
comparison-tool / charts-angular / ui / util / shared   (feature/UI)
         ↓
      services / constants                              (state)
         ↓
        models                                          (types)
         ↓
 testing / config / sentry / themes / styles / assets   (infrastructure)
```

All imports use `@sagebionetworks/explorers/<lib-name>` path aliases. Never use relative paths across library boundaries.
