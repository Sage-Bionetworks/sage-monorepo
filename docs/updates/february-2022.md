# February 2022

February 2022 represents the foundational month for the sage-monorepo project, marking the transition to a comprehensive Nx monorepo architecture. This period saw the establishment of core infrastructure, development tooling, and the initial application and library ecosystem that would support future development across multiple technology stacks.

During this month, **[28 pull requests](#all-pull-requests-merged)** were successfully merged, establishing the essential infrastructure and initial applications for the monorepo. The primary contributor was @tschaffter, who led the architectural design and implementation of the workspace foundation.

## Technical architecture overview

February 2022 was a pivotal month focused on establishing robust architectural foundations and development infrastructure. The team implemented a sophisticated Nx monorepo structure designed to support multiple applications and shared libraries across different technology stacks while maintaining code quality and development efficiency.

### Monorepo workspace establishment

The development team established a comprehensive Nx workspace architecture designed to support multiple applications and shared libraries across TypeScript, Angular, React, and Web Components technologies. This architectural foundation enables efficient code sharing, dependency management, and coordinated development across different technology stacks. The workspace structure provides clear separation of concerns between applications, libraries, and shared resources while maintaining consistent development patterns and build processes.

### Containerization and deployment infrastructure

A comprehensive containerization strategy was implemented using Docker to ensure consistent deployment across different environments. Each application received its own Docker configuration with optimized build processes, enabling independent deployment and scaling capabilities. The team established Docker Compose orchestration for local development, simplifying the setup process for new contributors and ensuring environment consistency across the development team.

### Development tooling and quality assurance

Significant investment was made in development tooling and quality assurance infrastructure to maintain high code quality standards. This included implementing pre-commit hooks with lint-staged configurations, continuous integration pipelines with GitHub Actions, and comprehensive linting and testing frameworks. The team also established cross-platform development support using tools like shx to accommodate different development environments and ensure consistent behavior across operating systems.

## All pull requests merged

### Workspace foundation and initial setup

- [#1](https://github.com/Sage-Bionetworks/sage-monorepo/pull/1): Scafold workspace

### Platform infrastructure

- [#6](https://github.com/Sage-Bionetworks/sage-monorepo/pull/6): Add CI workflow
- [#8](https://github.com/Sage-Bionetworks/sage-monorepo/pull/8): Fix CI workflow
- [#10](https://github.com/Sage-Bionetworks/sage-monorepo/pull/10): Replace npm commands by yarn in CI workflow
- [#12](https://github.com/Sage-Bionetworks/sage-monorepo/pull/12): Add targets `build` and `test`
- [#21](https://github.com/Sage-Bionetworks/sage-monorepo/pull/21): Add pre-commit hook
- [#38](https://github.com/Sage-Bionetworks/sage-monorepo/pull/38): Use bash -c in package.json to call sourced functions
- [#39](https://github.com/Sage-Bionetworks/sage-monorepo/pull/39): Set yarn min version required
- [#46](https://github.com/Sage-Bionetworks/sage-monorepo/pull/46): Run cp command with shx

### Libraries and shared components

- [#2](https://github.com/Sage-Bionetworks/sage-monorepo/pull/2): Add lib api-docs
- [#7](https://github.com/Sage-Bionetworks/sage-monorepo/pull/7): Configure target `lint` and `lint-fix` for existing apps and libs
- [#28](https://github.com/Sage-Bionetworks/sage-monorepo/pull/28): Show how to use a Web Components UI library in Angular and React
- [#30](https://github.com/Sage-Bionetworks/sage-monorepo/pull/30): Import sage-angular lib
- [#31](https://github.com/Sage-Bionetworks/sage-monorepo/pull/31): Add Web Components (WC) component example

### Applications

- [#4](https://github.com/Sage-Bionetworks/sage-monorepo/pull/4): Add API app
- [#5](https://github.com/Sage-Bionetworks/sage-monorepo/pull/5): Add db-cli
- [#16](https://github.com/Sage-Bionetworks/sage-monorepo/pull/16): Add project `api-db`
- [#20](https://github.com/Sage-Bionetworks/sage-monorepo/pull/20): Create project `web-app`
- [#25](https://github.com/Sage-Bionetworks/sage-monorepo/pull/25): Dockerize the projects `api` and `api-db`
- [#33](https://github.com/Sage-Bionetworks/sage-monorepo/pull/33): Integrate `api-angular` into `web-app`
- [#35](https://github.com/Sage-Bionetworks/sage-monorepo/pull/35): Dockerize `web-app`

### Documentation

- [#13](https://github.com/Sage-Bionetworks/sage-monorepo/pull/13): Move initial README generated by Nx to docs/nx.md
- [#40](https://github.com/Sage-Bionetworks/sage-monorepo/pull/40): Document how to start the web app with Docker
- [#43](https://github.com/Sage-Bionetworks/sage-monorepo/pull/43): Add docs/legacy.md
- [#44](https://github.com/Sage-Bionetworks/sage-monorepo/pull/44): Add Code Of Conduct and Contributing Guide
- [#48](https://github.com/Sage-Bionetworks/sage-monorepo/pull/48): Add documentation
- [#49](https://github.com/Sage-Bionetworks/sage-monorepo/pull/49): Fix doc on how to start with Docker
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
