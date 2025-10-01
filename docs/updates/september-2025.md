# September 2025

_Published on October 1, 2025_

September advanced platform coherence, developer reliability, and scientific user experience across the Sage monorepo with **[96 pull requests](#pull-requests-merged)** merged. Highlights included centralized authentication and gateway evolution, resilient Synapse workflow hardening, Model-AD data+UX refinements, systematic OpenAPI normalization, and stronger local/compose infrastructure. Primary contributors included [@tschaffter](https://github.com/tschaffter), [@hallieswan](https://github.com/hallieswan), [@rrchai](https://github.com/rrchai), [@sagely1](https://github.com/sagely1), and automation support from [@github-actions[bot]](https://github.com/github-actions%5Bbot%5D).

## Summary

- **Total Pull Requests**: 96 merged PRs
- **Key Focus**: Gateway & auth consolidation, Synapse workflow resilience, Model-AD data/model version alignment & UX, OpenAPI governance, infrastructure composability
- **Major Projects**: OpenChallenges authorization redesign, Model-AD mv84→mv85 transition, Synapse automated update pipeline, cross-project OpenAPI/tag normalization, Caddy & Docker compose unification

## Technical Architecture Overview

### Platform evolution

Authentication moved toward centralized mediation via the OpenChallenges API gateway while BixArena added its own gateway foundation. Caddy image consolidation and consistent reverse-proxy patterns reduced divergence and simplified operational updates.

### Developer experience

Deterministic environments improved through Gradle 9 adoption, ES target unification, dependency hygiene, and pruning unused config (implicit dependencies, deprecated libraries). A refreshed, security‑scanned dev container ships a fully pinned multi‑language toolchain so every contributor builds and tests with the same versions—eliminating "works on my machine" drift and shortening onboarding/setup time. Together these changes decrease friction and accelerate iterative feature delivery.

### API modernization initiative

Kebab-case path/file conventions, tag singularization, vendor extension standardization (`x-audience`→`x-internal`), and removal of deprecated annotations drove a cleaner, more discoverable OpenAPI surface across Agora, Model-AD, AMP-ALS, Synapse, and Explorers—laying groundwork for consistent client generation.

### User interface enhancements

Model-AD received iterative accessibility and interaction upgrades (keyboard navigation, highlight, scrolling behavior, adaptive layouts, improved placeholders, legend visibility) plus new data-driven conditional panels. Cross‑project search and visualization components were tuned for clarity and responsiveness.

### Data & schema lifecycle

Successive data bumps (mv84, mv85), new comparative disease correlation endpoint, and alignment of omics card logic modernized scientific insights delivery while tightening frontend–backend semantic parity.

### Workflow & automation resilience

The Synapse update workflow underwent multi-stage stabilization culminating in reliable automated artifact regeneration, while broader CI triggers and concurrency controls prevented redundant runs. It now executes a once‑per‑day scheduled check of the official upstream Synapse OpenAPI description; if a content diff is detected it regenerates the monorepo's OpenAPI‑derived client artifacts (including the Angular client) and raises a PR, otherwise it terminates as a no‑op. The generated Angular client, for example, is leveraged by Agora and Model-AD to interact with Synapse Wikis, keeping downstream integrations current without manual intervention.

## Pull Requests Merged

### Authentication & gateway centralization

- [#3438](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3438): feat(openchallenges): centralize authentication (JWT tokens & API keys) in the API gateway (SMR-444, SMR-445, SMR-446)
- [#3505](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3505): feat(bixarena): add BixArena API gateway (SMR-504)
- [#3485](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3485): fix(openchallenges): hotfix org login, replace `org.acronym` by `org.shortName` and adopt `x-sage-internal` (SMR-480, SMR-489)

### Workflow & CI resilience

- [#3534](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3534): chore: update trigger for Lint PR workflow and CI workflow (SMR-507)
- [#3532](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3532): chore(synapse): disable Nx Cloud in the Synapse API workflow
- [#3528](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3528): fix(synapse): set `COREPACK_ENABLE_DOWNLOAD_PROMPT` in Synapse API workflow (SMR-507)
- [#3527](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3527): fix(synapse): update the Synapse API workflow to create PR that triggers downstream workflows (SMR-507)
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
- [#3467](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3467): fix(explorers): skip nx cloud for e2e tests (SMR-463)

### OpenAPI governance & consistency

- [#3469](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3469): chore: replace OpenAPI extension `x-audience` by `x-internal` (SMR-465)
- [#3457](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3457): chore(amp-als): remove x-java annotations from AMP-ALS OpenAPI description (SMR-385)
- [#3455](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3455): chore(model-ad): update API paths and files to use kebab case (MG-405)
- [#3454](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3454): chore(amp-als): generate Angular API client files in kebab case for AMP-ALS (SMR-416)
- [#3443](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3443): chore(agora): remove @ from Agora openapi file and directory names (AG-1844, SMR-336)
- [#3441](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3441): chore(synapse): generate Angular API client files in kebab case (SMR-415)
- [#3440](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3440): chore(agora): update Agora OpenAPI tags to use spaces and singular form (SMR-338)
- [#3439](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3439): chore(model-ad): update Model-AD OpenAPI tags to use spaces and singular form (SMR-337)
- [#3436](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3436): chore(explorers): generate Angular API client files in kebab case (MG-383, AG-1851)

### UI/UX & accessibility refinement

- [#3484](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3484): fix(model-ad): ensure scroll to section after same-document navigation (MG-413)
- [#3483](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3483): fix(model-ad): copying section links shouldn't change the current URL (MG-412)
- [#3481](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3481): fix(model-ad): clean up section link fragments (MG-414)
- [#3479](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3479): fix(model-ad): conditionally display omics tab, fallback to default tab when invalid or disabled tab in URL (MG-282, MG-415)
- [#3471](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3471): fix(model-ad): update 'no results' message in search input (MG-409)
- [#3465](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3465): fix(model-ad): wrap boxplots grid to enable complete image downloads (MG-407)
- [#3463](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3463): feat(model-ad): align boxplot style with design (MG-298)
- [#3462](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3462): feat(model-ad): allow search results to be selected with mouse or keyboard (MG-361)
- [#3460](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3460): chore(explorers): update boxplot chart to use axis tooltips built into echarts 5.6.0+ (MG-270)
- [#3456](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3456): fix(model-ad): unstick the toc (MG-403)
- [#3453](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3453): feat(model-ad): matched control(s) value(s) in hero link to JAX (MG-397)
- [#3450](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3450): feat(model-ad): update search tile wording (MG-366)
- [#3449](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3449): fix(model-ad): update hero image (MG-303)
- [#3451](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3451): fix(model-ad): style terms of service content (MG-296)
- [#3448](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3448): feat(model-ad): scroll to panel content when tab specified in url, delay scroll when changing between tabs to give content time to load (MG-390)
- [#3447](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3447): fix(model-ad): move allen institute card to resources (MG-396)
- [#3446](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3446): feat(model-ad): highlight search query in search results, modernize search input to use signals, fix display of long search results (MG-360)
- [#3445](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3445): fix(model-ad): reset model details variables when route is updated to ensure default tab is loaded for each model (MG-394)
- [#3444](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3444): feat(model-ad): share link tooltip and copy behavior improvements (MG-399)
- [#3442](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3442): fix(model-ad): allow model details boxplots container to stretch wider than the section (MG-330)
- [#3435](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3435): feat(model-ad): align style of "no data" boxplot placeholder with design, fix bug where legend is not displayed when last boxplot doesn't have data (MG-317)
- [#3433](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3433): fix(model-ad): fix bug preventing display of all boxplots (MG-387, MG-392)

### Data & schema lifecycle evolution

- [#3464](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3464): chore(model-ad): bump model-ad-data to mv85 and align e2e tests with available data (MG-398)
- [#3434](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3434): chore(model-ad): bump model-ad-data to mv84 and align models with updated data structure (MG-362)
- [#3470](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3470): feat(model-ad): conditionally display omics cards (MG-278)
- [#3452](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3452): feat(model-ad): create api description and route for disease_correlation (MG-237)
- [#3472](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3472): fix(model-ad): remove frontend search query sanitization in favor of backend query escaping (MG-408)
- [#3488](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3488): feat(explorers): update site version in model-ad footer, create shared service to manage data version and site version (MG-373)

### Developer environment determinism

- [#3531](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3531): chore(deps): remove `packageManager` from `package.json` (SMR-507)
- [#3530](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3530): chore: activate the dev container `sha-db91d6849b048b6ef69472cdeb80caad262b3e68` (SMR-497)
- [#3526](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3526): chore(bixarena): switch bixarena-app task names to serve different versions
- [#3504](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3504): refactor: remove "-angular" suffix from TypeScript aliases
- [#3502](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3502): chore(deps): upgrade to Gradle 9 (SMR-494)
- [#3495](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3495): chore(deps): update dependencies with nx migrate latest and add SOP for updating packages (SMR-469)
- [#3494](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3494): chore(deps): adopt pnpm minimumReleaseAge (SMR-492)
- [#3501](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3501): chore(deps): update testing and storybook dependencies (SMR-469)
- [#3507](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3507): chore(deps): update dependencies with security vulnerabilities (SMR-469)
- [#3476](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3476): chore(deps): update all es targets to `es2024` (SMR-284)
- [#3458](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3458): chore: remove empty `implicitDependencies` in `project.json` files (SMR-362)
- [#3437](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3437): chore(sage-monorepo): use chat.tools.terminal.autoApprove instead (SMR-388)

### Infrastructure & local composability

- [#3474](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3474): feat: centralize all Caddy images and update to Caddy 2.10.2 - Take 2 (SMR-433)
- [#3459](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3459): feat: add Docker Compose `extra_hosts` to OC, Agora and Model-AD apexes (SMR-384)
- [#3466](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3466): chore(openchallenges): migrate from sse to streamable http transport
- [#3468](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3468): chore(sage-monorepo): remove the deprecated shiny-base Docker project
- [#3486](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3486): feat(openchallenges): make the image service zero-config (SMR-478)
- [#3487](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3487): feat(openchallenges): set allowlist used by Thumbor's HTTP loader (SMR-478)
- [#3480](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3480): feat(openchallenges): switch default Thumbor loader to `thumbor.loaders.http_loader` (SMR-477)
- [#3492](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3492): refactor(openchallenges): update app properties of the MCP server (SMR-479)
- [#3489](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3489): chore(openchallenges): remove unused Terraform infra project
- [#3508](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3508): refactor(openchallenges): remove the `openchallenges-app-config-data` Java library (SMR-462)

### Security scope taxonomy & authorization redesign

_No direct scope taxonomy–only PRs merged this month (authorization semantics were bundled with gateway consolidation above)._

### Automation & generated artifacts

- [#3535](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3535): chore(synapse): update Synapse API description and generated artifacts

### Documentation framework foundation

- [#3473](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3473): chore: batch patch/minor Java dependencies update (SMR-468)
- [#3493](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3493): docs(sage-monorepo): redesign the main README (SMR-496)
- [#3431](https://github.com/Sage-Bionetworks/sage-monorepo/pull/3431): docs(sage-monorepo): update nav structure of the docs site and add first monthly updates - Take 2 (SMR-418, SMR-419)

## Community Impact

Consistent OpenAPI governance and automated regeneration reduce integration friction for downstream tools and clients. Centralized authentication plus emerging gateway patterns strengthen security posture while simplifying service composition. Model-AD’s refined interaction patterns and data alignment accelerate hypothesis exploration. Resilient Synapse workflows and deterministic dev environments shrink feedback cycles, and unified Caddy + Docker compose strategies make local multi-application orchestration more intuitive for contributors.

## Thank You

Last but certainly not least, a big **_Thank You_** to the contributors of the Sage monorepo.

- [@tschaffter](https://github.com/tschaffter)
- [@hallieswan](https://github.com/hallieswan)
- [@rrchai](https://github.com/rrchai)
- [@sagely1](https://github.com/sagely1)
- [@github-actions[bot]](https://github.com/github-actions%5Bbot%5D)

---

_We continue to build an open, interoperable research platform that empowers communities to accelerate discovery. [Join us](https://github.com/Sage-Bionetworks/sage-monorepo) and help shape the next wave of scientific tooling._
