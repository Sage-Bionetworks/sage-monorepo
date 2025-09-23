<h1 align="center">Sage Monorepo</h1>

<p align="center">
  <br>
  <img src="docs/images/angular-cli-logo.png" alt="Angular CLI logo" width="100px" height="108px"/>
  <br><br>
  <h3 align="center">
    A development environment for building robust apps faster.
  </h3>
  <br>
</p>

<p align="center">
  <a href="https://sage-bionetworks.github.io/sage-monorepo/">Docs Site</a> ·
  <a href="https://sage-bionetworks.github.io/sage-monorepo/contributions/overview/">Contributing Guidelines</a> ·
  <a href="https://github.com/Sage-Bionetworks/sage-monorepo/issues/new/choose">Submit an Issue</a>
  <br><br>
  <img src="https://img.shields.io/github/actions/workflow/status/Sage-Bionetworks/sage-monorepo/ci.yml?branch=main&color=007acc&labelColor=555555&style=for-the-badge&logo=github&label=CI/CD" alt="CI/CD"/>
  <img src="https://img.shields.io/github/license/Sage-Bionetworks/sage-monorepo.svg?color=007acc&labelColor=555555&style=for-the-badge&logo=github" alt="License"/>
</p>

---

## About the Monorepo

The Sage Monorepo is a polyglot workspace where we design, build, and ship multiple biomedical products. It hosts web apps, REST APIs, microservices, and databases across TypeScript, Java, Python, and R.

It is designed to be language-agnostic, with reusable components and shared infrastructure that allow teams to focus on creative solutions instead of boilerplate setup.

## Architecture & Stack

- **Languages:** TypeScript, Java, Python, R
- **Frameworks:** Angular, Spring Boot, Python services, Node.js utilities
- **Infrastructure Components:** PostgreSQL, OpenSearch, Caddy (reverse proxy), containerized stacks
- **Monorepo tooling:** Nx for orchestration and task graph caching
- **Automation:** GitHub Actions for CI/CD, code quality, and security checks

## OpenAPI-First Workflow

We follow an API contract-first approach:

1. Define the product API with OpenAPI specifications.
2. Generate server stubs and client SDKs (Angular, Java, Python, TypeScript).
3. Implement product logic inside generated skeletons.

Benefits:

- Shared, consistent contracts across products
- Fewer breaking changes & faster alignment
- Multi-language clients and documentation out of the box

## Products

| Product            | Description                                          | Link                                          |
| ------------------ | ---------------------------------------------------- | --------------------------------------------- |
| **Agora**          | Evidence explorer for Alzheimer’s research           | [Visit](https://agora.adknowledgeportal.org/) |
| **OpenChallenges** | Platform for scientific & citizen science challenges | [Visit](https://openchallenges.io/)           |
| **MODEL-AD**       | Resources for Alzheimer’s disease model data         | [Visit](https://model-ad.org/)                |

➡ More services and details: see the [Docs Site](https://sage-bionetworks.github.io/sage-monorepo/).

## Guiding Values

- **Developer Experience:** Shared tooling, cached builds, reproducible workflows
- **Reuse over Rebuild:** Shared UI, API clients, configs, infra scripts
- **Focus on Science:** More time on creative problem-solving, less on setup

## Dev Environment

The monorepo ships with a [Dev Container](https://containers.dev/) configuration including Node.js, Java, Python, R, and database tooling.

- Works locally with VS Code Dev Containers or in GitHub Codespaces
- Provides a reproducible setup for contributors and CI/CD
- Makes onboarding and remote development seamless

[![Open in Dev Containers](https://img.shields.io/static/v1?label=Dev%20Container&message=Open&color=blue&logo=visualstudiocode&style=for-the-badge)](https://vscode.dev/redirect?url=vscode://ms-vscode-remote.remote-containers/cloneInVolume?url=https://github.com/Sage-Bionetworks/sage-monorepo)

## Contributing

If you like the Sage Monorepo, its architecture, approach, or the projects it brings together (such
as Agora, OpenChallenges, and MODEL-AD), please consider giving it a star ⭐ and check out the
[contributing guide](CONTRIBUTING.md) to get involved.
