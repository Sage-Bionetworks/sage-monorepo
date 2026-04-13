# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is an Angular standalone component library within the Sage monorepo that provides an interactive data comparison visualization tool. It renders comparative data in a tabular format with heatmap cells, filtering, column selection, pinning, and CSV/image export. It is consumed by `agora` and `model-ad` comparison tool libraries.

## Commands

```bash
nx test explorers-comparison-tool          # run unit tests
nx test explorers-comparison-tool --testFile=path/to/spec.ts  # run a single test file
nx lint explorers-comparison-tool          # lint TypeScript and HTML
nx lint explorers-comparison-tool --fix    # auto-fix lint issues
```

Coverage is output to `coverage/libs/explorers/comparison-tool`.

## Architecture

### Component Hierarchy

`ComparisonToolComponent` is the root and composes:

- `ComparisonToolHeaderComponent` — title and action buttons
- `ComparisonToolControlsComponent` — search input, category selectors, column selector, significance threshold, displayed-results count
- `ComparisonToolTableComponent` — renders two `BaseTableComponent` instances (pinned rows + unpinned rows), each backed by PrimeNG table
- `ComparisonToolFilterPanelComponent` — slide-in side panel for advanced filtering
- `ComparisonToolFilterListComponent` — active filter chips/badges
- `HeatmapDetailsPanelComponent` — modal showing detailed heatmap cell info
- `ComparisonToolFooterComponent` — paginator and help links
  - `HelpLinksComponent` — legend panel and visualization overview panel (also a public export)

### State Management

State is managed via **Angular Signals** in three services (sourced from `@sagebionetworks/explorers/services`):

| Service                       | Responsibility                                                             |
| ----------------------------- | -------------------------------------------------------------------------- |
| `ComparisonToolService`       | Core state: data, pinned rows, selected columns, view config, lazy loading |
| `ComparisonToolFilterService` | Filter state: search text, selected filter options, significance threshold |
| `ComparisonToolHelperService` | Data transformation: CSV export, filename generation                       |

Components use `computed()` signals for derived/read state and call service methods for mutations. Search input debounces via RxJS `Subject` + `debounceTime()` with `takeUntilDestroyed()` cleanup.

### Data Models

Key interfaces from `@sagebionetworks/explorers/models`:

- `ComparisonToolConfig` — column definitions, filter options, dropdown config
- `ComparisonToolViewConfig` — title, tooltips, legend config, action buttons
- `HeatmapCircleData` — `{ value: number, adj_p_val: number }` for heatmap cells
- `ComparisonToolFilterOption` — filter option with `selected` boolean state

### Testing Conventions

Bootstrap tests with `provideComparisonToolService()` and `provideComparisonToolFilterService()` from `@sagebionetworks/explorers/services`.

### Public API

Only two exports from `src/index.ts`:

```typescript
export * from './lib/comparison-tool.component';
export { HelpLinksComponent } from './lib/help-links/help-links.component';
```
