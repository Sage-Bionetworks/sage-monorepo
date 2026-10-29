# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

`libs/explorers/` is a collection of Angular libraries that provide reusable UI components, services, and utilities for Sage Bionetworks data exploration tools.

## Library Map

| Nx Project                  | Path               | Purpose                                                                                                                                     |
| --------------------------- | ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------- |
| `explorers-comparison-tool` | `comparison-tool/` | Root feature component — see its own `CLAUDE.md`                                                                                            |
| `explorers-charts`          | `charts/`          | Core chart utilities and models (framework-agnostic)                                                                                        |
| `explorers-charts-angular`  | `charts-angular/`  | Angular wrappers for charts; has Storybook (port 4400)                                                                                      |
| `explorers-ui`              | `ui/`              | Shared presentational components (header, footer, hero, search input, download utils)                                                       |
| `explorers-util`            | `util/`            | Lower-level utilities (loading containers, tooltips, modals, SVG icons, wiki component)                                                     |
| `explorers-shared`          | `shared/`          | Route definitions and shared page components (error, not-found, terms-of-service, wiki-hero)                                                |
| `explorers-models`          | `models/`          | Pure TypeScript interfaces — no runtime dependencies                                                                                        |
| `explorers-services`        | `services/`        | Angular services for state management                                                                                                       |
| `explorers-constants`       | `constants/`       | App-wide constants, injection tokens, debounce durations, and user-facing copy (toast messages, comparison tool label and tooltip builders) |
| `explorers-testing`         | `testing/`         | Test utilities: mock data, MSW handlers, provider factories                                                                                 |
| `explorers-config`          | `config/`          | Runtime configuration management; app server Express handlers in `@sagebionetworks/explorers/config/server`                                 |
| `explorers-sentry`          | `sentry/`          | Sentry error tracking integration                                                                                                           |
| `explorers-themes`          | `themes/`          | SCSS theme variables (no public TS API)                                                                                                     |
| `explorers-styles`          | `styles/`          | Shared SCSS styles                                                                                                                          |
| `explorers-storybook`       | `storybook/`       | Storybook host (port 4402)                                                                                                                  |

## Commands

```bash
nx test explorers-<lib-name>               # run unit tests for a lib
nx lint explorers-<lib-name>               # lint a lib
nx start explorers-storybook       # serve explorers Storybook (port 4402)
nx start explorers-charts-angular          # serve charts Storybook (port 4400)
```

## Testing Conventions

Tests use `@testing-library/angular`. The `testing` lib provides:

- MSW handlers (`handlers/`) for mocking HTTP requests
- Provider factories (`providers/`) for injection token setup
- Mock data and e2e fixtures

## Charts Libraries

`explorers-charts` and `explorers-charts-angular` use **eCharts** for chart rendering. eCharts is the preferred library -- use D3 only when eCharts cannot support the required functionality.

- `explorers-charts` — framework-agnostic eCharts logic: chart classes (`BoxplotChart`, `LegendChart`), themes, models, and utilities
- `explorers-charts-angular` — Angular directives wrapping the chart classes (`BoxplotDirective`, `LegendDirective`); has its own Storybook (port 4400)

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

## Comparison Tool Navigation and Service Lifetime

The comparison tool writes all of its state to the URL with `replaceUrl`, and header links into an open tool replace too (`COMPARISON_TOOL_HEADER_LINK_OPTIONS`), so no change within a CT adds a history entry. Back/forward never lands on another state of the same CT. It leaves the CT, which destroys the component. Don't flag history-navigation races inside the CT.

Leaving the CT does not destroy `ComparisonToolService`. Each CT provides it in its route's `providers` (e.g. `differential-expression-comparison-tool.routes.ts`), and Angular keeps a route's injector alive after navigating away, so the service and its state survive and are reused when the user returns. Anything that must not outlive a visit, such as a pin edit held for confirmation, has to be reset in `disconnect()`, which each product's CT component calls in `ngOnDestroy`.

## Backend and Database

The explorers products use MongoDB locally but Amazon DocumentDB in the deployed stack. Any MongoDB query or aggregation change must be verified for DocumentDB compatibility -- not all MongoDB features are supported. To check the current DocumentDB engine version for a product, ask the user first -- they'll usually know. If not, consult the relevant infra repo:

- agora: https://github.com/Sage-Bionetworks-IT/agora-infra-v3/
- model-ad: https://github.com/Sage-Bionetworks-IT/modeladexplorer-infra
- qtl: TBD (infra repo pending)

No external products outside this repo depend on the explorers APIs, so breaking API changes are acceptable as long as all explorer products within this repo continue to work.

## HTTP Error Handling

`httpErrorInterceptor` (`libs/explorers/util/`) turns every failed request into an `HttpRequestError` and re-throws it, but it doesn't report to Sentry or show the error overlay: only the call site knows what it was doing and how serious the failure is. So any code that catches a failed API request must:

- **Report it** with `reportHttpError(logger, operation, { error, level })` from `@sagebionetworks/explorers/services`. `operation` names what the call site was doing and must stay fixed, since Sentry groups each call site's failures by it. `level` is `HttpErrorReportLevelEnum.Error` for a failure that breaks the page, `Warn` for one the page recovers from (a fallback value, a redirect).
- **Show the overlay** when the failure leaves the page without its content. `ErrorOverlayService.reportAndShow(logger, operation, { error, level })` reports and shows in one call; use `showRequestError(error)` on its own only when showing the overlay depends on something else, as in the wiki component.

A failure no call site catches reaches `ExplorersErrorHandler`, which does both. A failure a call site catches without reporting is sent as an `UNREPORTED_HTTP_ERROR_MESSAGE` warning a tick later. That warning means a call site is missing its `reportHttpError` call; fix the call site rather than silencing the warning. Report from the catch itself (a `catchError`, or the `catch` of an awaited request), not from a later timer, or the warning fires first.

Requests made through an `HttpClient` built on `HttpBackend` (such as the app version check) bypass all interceptors, so none of this applies to them.

## Storybook

For components that don't call our own backend APIs (third-party service calls like Synapse wiki fetches are fine): add a `.stories.ts` file for new components; for changes to existing components, adjust the existing story if the new behavior fits naturally, or add a new story variant for sufficiently distinct behavior. When it's unclear whether a new story is warranted, ask the user. See `libs/explorers/util/src/lib/tooltip-button/tooltip-button.component.stories.ts` for the canonical pattern.
