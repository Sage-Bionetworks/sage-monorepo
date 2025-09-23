<h1 align="center">Sage Monorepo</h1>
<p align="center"><em>Build biomedical research products faster with a shared, language‑agnostic platform.</em></p>
<p align="center">
  <a href="https://sage-bionetworks.github.io/sage-monorepo/">Documentation Site</a> ·
  <a href="CONTRIBUTING.md">Contributing</a> ·
  <a href="https://github.com/Sage-Bionetworks/sage-monorepo/issues/new/choose">Issues</a>
</p>
<p align="center">
  <img src="https://img.shields.io/github/actions/workflow/status/Sage-Bionetworks/sage-monorepo/ci.yml?branch=main&label=CI/CD&logo=github&style=for-the-badge" alt="CI Status" />
  <img src="https://img.shields.io/github/license/Sage-Bionetworks/sage-monorepo.svg?style=for-the-badge&label=License" alt="License" />
</p>
<p align="center"><strong>If this repo helps your work, please ⭐ it!</strong></p>

---

## What Is This?

The Sage Monorepo is a unified Nx + polyglot workspace where we design, build, test, document, and ship multiple biomedical research products. It hosts web applications, REST & GraphQL APIs, microservices, data / ETL jobs, and infrastructure assets across TypeScript, Java, Python, and R.

## Architecture & Stack (High‑Level)

- **Monorepo Orchestration:** Nx (task graph, caching, consistent tooling)
- **Frontend:** Angular (standalone components, signals), some React utilities
- **Backend / Services:** Spring Boot (Java), Python services (Flask / scripts), R components, Node.js utilities
- **Data & Storage:** PostgreSQL, OpenSearch, MongoDB (select services), object storage (S3-compatible), static assets
- **Edge & Networking:** Caddy as reverse proxy, containerized local stacks via Docker & devcontainers
- **API Contract First:** OpenAPI specifications drive server stubs & client SDKs (Angular, Java, Python, TypeScript)
- **Automation:** GitHub Actions CI/CD, code generation, quality & security checks

The workspace is intentionally language agnostic: teams pick the best tool for a service while sharing conventions (naming, project tags, testing, documentation, release flows).

## OpenAPI‑First Workflow

1. Author or refine the product OpenAPI spec (lives under `libs/*/api-description` or app-specific folders).
2. Validate & lint specs (Redocly rules + CI).
3. Generate server stubs (e.g. Spring Boot) and typed client libraries (Angular, Java, Python) via OpenAPI Generator.
4. Implement business logic inside generated skeletons; keep contracts authoritative in the spec.
5. Publish API docs (GitHub Pages) & SDKs for reuse across products.

Benefits: earlier alignment, fewer breaking changes, instant multi-language clients, consistent error & auth models, and searchable, navigable API surfaces.

## Product Portfolio

| Product                  | Description                                                              | Production                                | Docs / API                                                                                                                                                      |
| ------------------------ | ------------------------------------------------------------------------ | ----------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Agora**                | Multi-omics evidence explorer for Alzheimer’s disease target discovery   | https://agora.adknowledgeportal.org/genes | [Docs](https://sage-bionetworks.github.io/sage-monorepo/products/agora/) · [API](https://sage-bionetworks.github.io/sage-monorepo/api/agora/)                   |
| **AMP-ALS**              | Collaboration platform for the Accelerating Medicines Partnership in ALS | https://www.amp-als.org/                  | [API](https://sage-bionetworks.github.io/sage-monorepo/api/amp-als/)                                                                                            |
| **BixArena**             | Biomedical data analysis & challenge competition platform                | https://bixarena.synapse.org/             | [API](https://sage-bionetworks.github.io/sage-monorepo/api/bixarena/)                                                                                           |
| **Model-AD**             | Resources & comparative tools for Alzheimer’s disease models             | https://www.model-ad.org/                 | [API](https://sage-bionetworks.github.io/sage-monorepo/api/model-ad/)                                                                                           |
| **OpenChallenges**       | Cloud-native platform for scientific & citizen science challenges        | https://openchallenges.io/home            | [Docs](https://sage-bionetworks.github.io/sage-monorepo/products/openchallenges/) · [API](https://sage-bionetworks.github.io/sage-monorepo/api/openchallenges/) |
| **Synapse (evaluation)** | Data sharing & analysis platform integration / client work               | https://www.synapse.org/                  | [API](https://sage-bionetworks.github.io/sage-monorepo/api/synapse/)                                                                                            |

➡ More services & internal components: see the [Service Catalog](https://sage-bionetworks.github.io/sage-monorepo/products/services/).

## Guiding Values

We invest in developer experience so contributors spend more time on creative scientific solutions and less time on boilerplate:

- **Reuse over Rebuild:** Shared UI, styles, API clients, config, and infra scripts
- **Consistency:** Standard project tags, linting, formatting, test harnesses
- **Traceability:** Specs + generated clients keep contracts transparent
- **Velocity with Safety:** Cached builds, typed APIs, automated checks
- **Onboarding Simplicity:** One dev environment spins up everything you need

## Dev Environment (Dev Containers)

The repository ships a reproducible [Dev Container](https://containers.dev/) configuration including Node.js, Java toolchains, Python/R environments, Docker CLI, database tooling, and language servers. Open locally in VS Code or in a remote environment (Codespaces / cloud VM) for identical behavior.

Open directly in a dev container:

[![Open in Dev Containers](https://img.shields.io/static/v1?label=Dev%20Container&message=Open&color=blue&logo=visualstudiocode&style=for-the-badge)](https://vscode.dev/redirect?url=vscode://ms-vscode-remote.remote-containers/cloneInVolume?url=https://github.com/Sage-Bionetworks/sage-monorepo)

## Contributing

Read the [Contributing Guide](CONTRIBUTING.md) and browse issues tagged [help wanted](https://github.com/Sage-Bionetworks/sage-monorepo/labels/help%20wanted) or [good first issue](https://github.com/Sage-Bionetworks/sage-monorepo/labels/good%20first%20issue). Please follow our [Code of Conduct](CODE_OF_CONDUCT.md).

## License

Released under the [Apache License 2.0](LICENSE.txt).

---

Enjoying the approach? Share feedback, open issues, or star the repo ⭐
