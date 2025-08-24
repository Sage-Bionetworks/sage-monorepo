# February 2022

_Published on March 1, 2022_

February marked the foundational month for the Sage Monorepo initiative, with the establishment of a comprehensive development ecosystem designed to streamline biomedical research software development. The team successfully merged [**28 pull requests**](#all-pull-requests-merged) across multiple projects, setting up the architectural framework that would support the future growth of Sage Bionetworks' research platform.

This month focused on establishing the core infrastructure, implementing best practices for code quality, and creating the foundation for collaborative development across multiple biomedical applications.

## Technical Architecture Overview

### Platform Foundation and Monorepo Structure

The team established a robust Nx-based monorepo architecture, providing a unified workspace for multiple biomedical applications and shared libraries. This approach enables efficient code sharing, consistent development practices, and streamlined CI/CD processes across all Sage Bionetworks projects.

The foundation includes comprehensive tooling for build automation, testing frameworks, and code quality enforcement through automated linting and pre-commit hooks.

### API-First Development Approach

A comprehensive API specification and client generation system was implemented to ensure consistent data access patterns across all applications. This includes OpenAPI specifications, automated Angular client generation, and proper API documentation infrastructure.

The API-first approach guarantees type safety and consistent interfaces between frontend applications and backend services.

### Containerization and Development Environment

Docker-based containerization was implemented for all major components, including API servers, database services, and web applications. This ensures consistent development environments and simplified deployment processes.

The containerization strategy supports both local development and production deployment scenarios with unified configuration management.

### Quality Assurance and Development Standards

Automated code quality enforcement was established through ESLint configuration, pre-commit hooks, and comprehensive CI/CD pipelines. The team implemented both pre-commit linting and pre-push testing to maintain code quality standards.

These practices ensure consistent code style and prevent regressions from entering the main branch.

### Cross-Platform Development Support

The platform was designed with cross-platform compatibility in mind, using shell-agnostic commands and cross-platform utilities like `shx` for file operations. This enables developers to work effectively on Windows, macOS, and Linux environments.

The team also established proper environment management with development scripts and configuration files.

## All Pull Requests Merged

### Platform Infrastructure

- [#1](https://github.com/Sage-Bionetworks/sage-monorepo/pull/1): Scafold workspace
- [#6](https://github.com/Sage-Bionetworks/sage-monorepo/pull/6): Add CI workflow
- [#7](https://github.com/Sage-Bionetworks/sage-monorepo/pull/7): Configure target `lint` and `lint-fix` for existing apps and libs
- [#8](https://github.com/Sage-Bionetworks/sage-monorepo/pull/8): Fix CI workflow
- [#10](https://github.com/Sage-Bionetworks/sage-monorepo/pull/10): Replace npm commands by yarn in CI workflow
- [#12](https://github.com/Sage-Bionetworks/sage-monorepo/pull/12): Add targets `build` and `test`
- [#21](https://github.com/Sage-Bionetworks/sage-monorepo/pull/21): Add pre-commit hook
- [#38](https://github.com/Sage-Bionetworks/sage-monorepo/pull/38): Use bash -c in package.json to call sourced functions
- [#39](https://github.com/Sage-Bionetworks/sage-monorepo/pull/39): Set yarn min version required
- [#46](https://github.com/Sage-Bionetworks/sage-monorepo/pull/46): Run cp command with shx

### Challenge Registry Application

- [#2](https://github.com/Sage-Bionetworks/sage-monorepo/pull/2): Add lib api-docs
- [#4](https://github.com/Sage-Bionetworks/sage-monorepo/pull/4): Add API app
- [#5](https://github.com/Sage-Bionetworks/sage-monorepo/pull/5): Add db-cli
- [#16](https://github.com/Sage-Bionetworks/sage-monorepo/pull/16): Add project `api-db`
- [#20](https://github.com/Sage-Bionetworks/sage-monorepo/pull/20): Create project `web-app`
- [#25](https://github.com/Sage-Bionetworks/sage-monorepo/pull/25): Dockerize the projects `api` and `api-db`
- [#28](https://github.com/Sage-Bionetworks/sage-monorepo/pull/28): Show how to use a Web Components UI library in Angular and React
- [#29](https://github.com/Sage-Bionetworks/sage-monorepo/pull/29): Import UI components from forked sage-angular library
- [#30](https://github.com/Sage-Bionetworks/sage-monorepo/pull/30): Import sage-angular lib
- [#31](https://github.com/Sage-Bionetworks/sage-monorepo/pull/31): Add Web Components (WC) component example
- [#33](https://github.com/Sage-Bionetworks/sage-monorepo/pull/33): Integrate `api-angular` into `web-app`
- [#35](https://github.com/Sage-Bionetworks/sage-monorepo/pull/35): Dockerize `web-app`

### Documentation and Developer Experience

- [#13](https://github.com/Sage-Bionetworks/sage-monorepo/pull/13): Move initial README generated by Nx to docs/nx.md
- [#40](https://github.com/Sage-Bionetworks/sage-monorepo/pull/40): Document how to start the web app with Docker
- [#43](https://github.com/Sage-Bionetworks/sage-monorepo/pull/43): Add docs/legacy.md
- [#44](https://github.com/Sage-Bionetworks/sage-monorepo/pull/44): Add Code Of Conduct and Contributing Guide
- [#48](https://github.com/Sage-Bionetworks/sage-monorepo/pull/48): Add documentation
- [#49](https://github.com/Sage-Bionetworks/sage-monorepo/pull/49): Fix doc on how to start with Docker

## Community Impact

February 2022 represents a pivotal moment in Sage Bionetworks' software development approach, transitioning from isolated application development to a unified, collaborative ecosystem. The establishment of this monorepo architecture demonstrates the organization's commitment to open science principles, code reusability, and efficient development practices.

This foundational work enables multiple research teams to collaborate more effectively, share common components, and maintain consistent quality standards across all biomedical software projects. The emphasis on comprehensive documentation and developer experience ensures that the platform remains accessible to both current team members and future contributors.

---

_This inaugural month sets the stage for revolutionizing biomedical research application development. The infrastructure established in February 2022 would support rapid development of multiple scientific applications throughout the year. [Join us](https://github.com/Sage-Bionetworks/sage-monorepo) in building the future of research software!_
