# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

`libs/agora/` is a collection of Angular libraries for Agora, a Sage Bionetworks tool for exploring Alzheimer's disease gene evidence.

## Commands

```bash
nx test agora-<lib-name>   # run unit tests for a lib
nx lint agora-<lib-name>   # lint a lib
```

For serving the Agora app, see the "Serving an App" section in the root `CLAUDE.md` — the full stack must be running first.

## Library Map

| Nx Project                                | Path                                 | Purpose                                                                                 |
| ----------------------------------------- | ------------------------------------ | --------------------------------------------------------------------------------------- |
| `agora-api-description`                   | `api-description/`                   | OpenAPI specs (source of truth for the API)                                             |
| `agora-api-client-angular`                | `api-client-angular/`                | **Generated** Angular HTTP client — do not hand-edit                                    |
| `agora-models`                            | `models/`                            | Pure TypeScript interfaces (genes, charts, scores, comparison, distribution, etc.)      |
| `agora-config`                            | `config/`                            | Route paths, branding colors, help URLs                                                 |
| `agora-services`                          | `services/`                          | `HelperService`: number formatting, color schemes, GCT selection, URL params            |
| `agora-util`                              | `util/`                              | Header/footer navigation links, search utilities, route constants                       |
| `agora-charts`                            | `charts/`                            | D3-based chart components (BoxPlot, Candlestick, BiodomainsChart, Network, Score, etc.) |
| `agora-ui`                                | `ui/`                                | Shared presentational components (SearchInputComponent)                                 |
| `agora-genes`                             | `genes/`                             | Gene details pages with evidence panels, bio-domains, nominations                       |
| `agora-home`                              | `home/`                              | Landing page                                                                            |
| `agora-teams`                             | `teams/`                             | Team/researcher listing and detail pages                                                |
| `agora-nomination-form`                   | `nomination-form/`                   | Gene nomination submission form                                                         |
| `agora-gene-comparison-tool`              | `gene-comparison-tool/`              | Side-by-side gene comparison                                                            |
| `agora-nominated-targets-comparison-tool` | `nominated-targets-comparison-tool/` | Nominated drug targets comparison                                                       |
| `agora-nominated-drugs-comparison-tool`   | `nominated-drugs-comparison-tool/`   | Nominated drugs comparison                                                              |
| `agora-testing`                           | `testing/`                           | Mock data and service stubs for unit tests                                              |
| `agora-styles`                            | `styles/`                            | Shared SCSS variables and styles                                                        |
| `agora-storybook`                         | `storybook/`                         | Storybook host (port 4401)                                                              |
| `agora-assets`                            | `assets/`                            | Static assets (fonts, images, favicon)                                                  |

## Dependency Layers

Libraries depend only downward:

```
Feature components (genes, home, teams, nomination-form, *-comparison-tool)
         ↓
   charts / ui
         ↓
   services / util / config
         ↓
  api-client-angular  (generated from api-description)
         ↓
  testing / styles / assets  (infrastructure)
```

All imports use `@sagebionetworks/agora/<lib-name>` path aliases. Feature components also import from `@sagebionetworks/explorers/*` for shared comparison tool infrastructure.

## OpenAPI Workflow

Same pattern as described in the root `CLAUDE.md`. API contracts live in `api-description/`; the Angular HTTP client in `api-client-angular/` is fully generated.

```bash
# Regenerate api-client-angular from the OpenAPI spec
nx run agora-api-client-angular:generate

# Regenerate all agora generated clients at once
nx run-many -t=generate -p=agora-*

# Bundle the OpenAPI specs
nx build agora-api-description
```

## Charts Library

`agora-charts` contains standalone D3-based components. Each chart manages its own SVG lifecycle (`createChart`/`destroyChart` methods) and accepts data via `@Input()`. Charts use `HelperService` from `agora-services` for color schemes. Test setup files mock `ResizeObserver` and `SVGElement.getBBox` to support D3 in jsdom.

## Testing Conventions

Tests use **Angular TestBed + ComponentFixture** (not `@testing-library/angular`). Mock data and service stubs come from `@sagebionetworks/agora/testing`.

## Code Style

This codebase predates the signals migration in the explorers/model-ad libraries. Existing code uses RxJS Observables and `*ngIf`/`*ngFor` — these are legacy patterns. New code should follow the modern Angular patterns in `.claude/rules/angular.md` (signals, `@if`/`@for`, `inject()`). Do not proactively migrate existing legacy code unless it is directly relevant to the current task.
