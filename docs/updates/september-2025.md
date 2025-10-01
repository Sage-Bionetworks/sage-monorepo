# September 2025

_Published on October 1, 2025_

September delivered broad progress across the Sage monorepo with **[88 pull requests](#pull-requests-merged)** merged. Key momentum centered on Synapse API automation, Model-AD user experience refinements, OpenChallenges platform evolution, BixArena feature expansion, and cross-repository developer tooling improvements. Major contributors included [@tschaffter](https://github.com/tschaffter) and the broader application teams across Model-AD, OpenChallenges, BixArena, Explorers, Synapse, and Agora.

## Summary

- **Total Pull Requests**: 88 merged PRs
- **Key Focus**: Synapse API workflow automation, Model-AD search & visualization enhancements, OpenChallenges API and infra consolidation
- **Major Projects**: Model-AD UX improvements, OpenChallenges gateway & image service, BixArena gateway + auth, Synapse automated spec & client generation

## Technical Architecture Overview

### Platform evolution

The month advanced multi-application cohesion through centralized API gateway work (OpenChallenges) and shared version/data services spanning projects. Several refactors removed deprecated naming patterns and normalized TypeScript aliasing and OpenAPI tag conventions across services.

### Developer experience

Tooling modernization included Gradle 9 adoption, ES2024 target alignment, structured SOPs for dependency upgrades, pnpm stability improvements, and refinement of automated workflows (e.g., Synapse update pipeline hardening). These changes reduce friction in maintaining a large polyglot monorepo.

### API modernization initiative

Multiple services (Agora, Model-AD, AMP-ALS, Synapse, Explorers) received standardized kebab-case client generation and harmonized OpenAPI tagging. OpenChallenges advanced toward a unified client/CLI experience while the image service and authentication layers became more zero‑config and centralized.

### User interface enhancements

Model-AD saw major iterative UX gains: improved search interaction, enhanced boxplot rendering and styling, contextual scrolling, tab persistence, placeholder quality, tooltip affordances, and accessibility refinement. BixArena added UI components for prompt examples, leaderboard integration through API fetching, and authenticated user workflows.

### Infrastructure & automation

Centralization of Caddy images, expanded `extra_hosts` Docker compose configuration, and reliability improvements in automated Synapse API regeneration strengthened operational foundations. Workflow resilience upgrades addressed quoting, env propagation, and reproducible commit behavior inside dev containers.

## Pull Requests Merged

### Platform infrastructure

- [#3504](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3504): refactor: remove "-angular" suffix from TypeScript aliases
- [#3474](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3474): feat: centralize all Caddy images and update to Caddy 2.10.2 - Take 2 (SMR-433)
- [#3473](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3473): chore: batch patch/minor Java dependencies update (SMR-468)
- [#3469](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3469): chore: replace OpenAPI extension `x-audience` by `x-internal`
- [#3468](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3468): chore(sage-monorepo): remove the deprecated shiny-base Docker project
- [#3437](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3437): chore(sage-monorepo): use chat.tools.terminal.autoApprove instead (SMR-388)

### Developer experience

- [#3502](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3502): chore(deps): upgrade to Gradle 9 (SMR-494)
- [#3495](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3495): chore(deps): update dependencies with nx migrate latest and add SOP for updating packages (SMR-469)
- [#3494](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3494): chore(deps): adopt pnpm minimumReleaseAge (SMR-492)
- [#3501](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3501): chore(deps): update testing and storybook dependencies (SMR-469)
- [#3507](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3507): chore(deps): update dependencies with security vulnerabilities (SMR-469)
- [#3476](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3476): chore(deps): update all es targets to `es2024` (SMR-284)
- [#3467](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3467): fix(explorers): skip nx cloud for e2e tests (SMR-463)

### OpenChallenges platform evolution

- [#3508](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3508): refactor(openchallenges): remove the `openchallenges-app-config-data` Java library (SMR-462)
- [#3492](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3492): refactor(openchallenges): update app properties of the MCP server (SMR-479)
- [#3491](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3491): feat(openchallenges): prototype unified client and CLI for OpenChallenges (SMR-493)
- [#3490](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3490): fix(openchallenges): support null value for `challenge.platform` (SMR-490)
- [#3489](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3489): chore(openchallenges): remove unused Terraform infra project
- [#3487](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3487): feat(openchallenges): set allowlist used by Thumbor's HTTP loader (SMR-478)
- [#3486](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3486): feat(openchallenges): make the image service zero-config (SMR-478)
- [#3485](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3485): fix(openchallenges): hotfix org login, replace `org.acronym` by `org.shortName` and adopt `x-sage-internal` (SMR-480, SMR-489)
- [#3480](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3480): feat(openchallenges): switch default Thumbor loader to `thumbor.loaders.http_loader` (SMR-477)
- [#3477](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3477): fix(openchallenges): add missing Nx task `openchallenges-app:create-config` (SMR-459)
- [#3466](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3466): chore(openchallenges): migrate from sse to streamable http transport
- [#3459](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3459): feat: add Docker Compose `extra_hosts` to OC, Agora and Model-AD apexes (SMR-384)
- [#3438](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3438): feat(openchallenges): centralize authentication (JWT tokens & API keys) in the API gateway (JWT tokens & API keys) in the API gateway (SMR-444, SMR-445, SMR-446)

### Synapse API & automation

- [#3526](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3526): chore(bixarena): switch bixarena-app task names to serve different versions
- [#3523](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3523): fix(synapse): use alternative method to run the dev container to update Synapse API (SMR-435)
- [#3521](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3521): feat(synapse): simplify the workflow that update the Synapse API
- [#3520](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3520): fix(synapse): specify base branch in workflow that updates the API
- [#3519](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3519): refactor(synapse): create commit inside the dev container in the update workflow
- [#3518](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3518): refactor(synapse): rewrite the Synapse update workflow
- [#3517](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3517): fix(synapse): pass env var `GITHUB_TOKEN` to the dev container in the workflow that updates Synapse API
- [#3515](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3515): fix(synapse): simplify PR description in the workflow that updates the API description and clients
- [#3514](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3514): fix(synapse): fix issue because of missing GitHub env var inside the dev container
- [#3513](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3513): fix(synapse): fix nested single quotes in GitHub workflow that updates Synapse API (SMR-435)
- [#3512](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3512): fix(synapse): fix GitHub workflow that updates Synapse API - Take 4 (SMR-435)
- [#3511](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3511): fix(synapse): fix GitHub workflow that updates Synapse API - Take 3 (SMR-435)
- [#3510](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3510): fix(synapse): fix GitHub workflow that updates Synapse API - Take 2 (SMR-435)
- [#3509](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3509): fix(synapse): fix GitHub workflow that updates Synapse API (SMR-435)

### BixArena features

- [#3526](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3526): chore(bixarena): switch bixarena-app task names to serve different versions
- [#3505](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3505): feat(bixarena): add BixArena API gateway (SMR-504)
- [#3498](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3498): feat(bixarena): create ui component to display the prompt examples on battle page (SMR-495)
- [#3482](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3482): feat(bixarena): add user page and support synapse login (SMR-474)
- [#3475](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3475): feat(bixarena): fetch leaderboard data using the API server - Take 2 (SMR-410)

### Explorers & shared visualization

- [#3506](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3506): fix(explorers): handle paginated response from GitHub tags service (MG-418)
- [#3499](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3499): feat(explorers): extend search-input to allow subtext beneath result text
- [#3488](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3488): feat(explorers): update site version in model-ad footer, create shared service to manage data version and site version (MG-373)
- [#3478](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3478): fix(explorers): fix csrApiUrl for explorers to use extra_host feature (SMR-473)
- [#3461](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3461): chore(explorers): move shared-typescript-charts and shared-typescript-charts-angular to explorers (MG-406)
- [#3460](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3460): chore(explorers): update boxplot chart to use axis tooltips built into echarts 5.6.0+ (MG-270)
- [#3436](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3436): chore(explorers): generate Angular API client files in kebab case (MG-383, AG-1851)

### Model-AD user interface enhancements

- [#3452](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3452): feat(model-ad): create api description and route for disease_correlation (MG-237)
- [#3484](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3484): fix(model-ad): ensure scroll to section after same-document navigation (MG-413)
- [#3481](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3481): fix(model-ad): clean up section link fragments (MG-414)
- [#3483](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3483): fix(model-ad): copying section links shouldn't change the current URL (MG-412)
- [#3479](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3479): fix(model-ad): conditionally display omics tab, fallback to default tab when invalid or disabled tab in URL (MG-282, MG-415)
- [#3472](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3472): fix(model-ad): remove frontend search query sanitization in favor of backend query escaping
- [#3470](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3470): feat(model-ad): conditionally display omics cards (MG-278)
- [#3471](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3471): fix(model-ad): update 'no results' message in search input (MG-409)
- [#3465](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3465): fix(model-ad): wrap boxplots grid to enable complete image downloads (MG-407)
- [#3463](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3463): feat(model-ad): align boxplot style with design (MG-298)
- [#3464](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3464): chore(model-ad): bump model-ad-data to mv85 and align e2e tests with available data (MG-398)
- [#3462](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3462): feat(model-ad): allow search results to be selected with mouse or keyboard (MG-361)
- [#3456](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3456): fix(model-ad): unstick the toc (MG-403)
- [#3458](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3458): chore: remove empty `implicitDependencies` in `project.json` files (SMR-362)
- [#3455](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3455): chore(model-ad): update API paths and files to use kebab case (MG-405)
- [#3453](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3453): feat(model-ad): matched control(s) value(s) in hero link to JAX (MG-397)
- [#3450](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3450): feat(model-ad): update search tile wording (MG-366)
- [#3449](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3449): fix(model-ad): update hero image (MG-303)
- [#3451](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3451): fix(model-ad): style terms of service content (MG-296)
- [#3445](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3445): fix(model-ad): reset model details variables when route is updated to ensure default tab is loaded for each model (MG-394)
- [#3446](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3446): feat(model-ad): highlight search query in search results, modernize search input to use signals, fix display of long search results (MG-360)
- [#3447](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3447): fix(model-ad): move allen institute card to resources (MG-396)
- [#3448](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3448): feat(model-ad): scroll to panel content when tab specified in url, delay scroll when changing between tabs to give content time to load (MG-390)
- [#3442](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3442): fix(model-ad): allow model details boxplots container to stretch wider than the section (MG-330)
- [#3433](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3433): fix(model-ad): fix bug preventing display of all boxplots (MG-387, MG-392)
- [#3435](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3435): feat(model-ad): align style of "no data" boxplot placeholder with design, fix bug where legend is not displayed when last boxplot doesn't have data (MG-317)
- [#3434](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3434): chore(model-ad): bump model-ad-data to mv84 and align models with updated data structure (MG-362)

### Agora & AMP-ALS API consistency

- [#3443](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3443): chore(agora): remove @ from Agora openapi file and directory names (AG-1844, SMR-336)
- [#3440](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3440): chore(agora): update Agora OpenAPI tags to use spaces and singular form (SMR-338)
- [#3454](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3454): chore(amp-als): generate Angular API client files in kebab case for AMP-ALS (SMR-416)
- [#3457](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3457): chore(amp-als): remove x-java annotations from AMP-ALS OpenAPI description (SMR-385)

### Cross-project API client generation & tagging

- [#3441](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3441): chore(synapse): generate Angular API client files in kebab case (SMR-415)
- [#3439](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3439): chore(model-ad): update Model-AD OpenAPI tags to use spaces and singular form (SMR-337)
- [#3436](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3436): chore(explorers): generate Angular API client files in kebab case (MG-383, AG-1851)

### Documentation & governance

- [#3493](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3493): docs(sage-monorepo): redesign the main README (SMR-496)
- [#3431](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3431): docs(sage-monorepo): update nav structure of the docs site and add first monthly updates - Take 2 (SMR-418, SMR-419)

## Community Impact

September's work strengthened the platform's reliability, interoperability, and user-facing polish. Automated Synapse API regeneration reduces maintenance overhead and accelerates delivery of the latest schema to downstream clients. Model-AD enhancements continue to improve scientific data exploration fidelity and usability. OpenChallenges platform improvements move closer to a unified client ecosystem that lowers onboarding friction. Consistent API naming and client generation across projects lay groundwork for broader tooling reuse and documentation clarity.

## Thank You

Last but certainly not least, a big **_Thank You_** to the contributors of the Sage monorepo.

- [@tschaffter](https://github.com/tschaffter)

---

_We continue to build an open, interoperable research platform that empowers communities to accelerate discovery. [Join us](https://github.com/Sage-Bionetworks/sage-monorepo) and help shape the next wave of scientific tooling._
