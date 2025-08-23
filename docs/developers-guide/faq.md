# Frequently Asked Questions

## Getting Started

### What is the Sage Monorepo?

The Sage Monorepo is a comprehensive development platform for building biomedical research applications. It provides shared components, modern tooling, and standardized development practices across multiple programming languages and frameworks.

### How do I get started?

Check out our [Quick Start Guide](../getting-started/local-dev.md) to set up your local development environment. For a deeper understanding, explore our [Architecture Overview](../getting-started/what-is-nx.md).

### What technologies are supported?

We support:
- **Frontend**: TypeScript, Angular, React
- **Backend**: Java (Spring Boot), Python (Flask), R (Shiny)
- **Databases**: PostgreSQL, MongoDB
- **Infrastructure**: Docker, Kubernetes, AWS

## Development

### How do I create a new project?

Follow our technology-specific tutorials:
- [Angular App](../tutorials/angular/add-app.md)
- [Java REST API](../tutorials/java/add-rest-api.md)
- [Python REST API](../tutorials/python/add-rest-api.md)
- [Docker Project](../tutorials/docker/new-project.md)

### How do I share code between projects?

Use our library creation guides:
- [Angular Library](../tutorials/angular/add-library.md)
- [Java Library](../tutorials/java/add-library.md)

### Can I develop remotely?

Yes! We support remote development through:
- [Dev Containers](../getting-started/what-is-devcontainer.md)
- [Remote Host Development](../developers-guide/developing-on-a-remote-host.md)

## Platform & Services

### What services are available?

See our [Service Catalog](../reference/services.md) for a complete list of all 145 projects, or explore our main products:
- [Agora](../reference/agora.md)
- [OpenChallenges](../reference/oc-microservices.md)
- [API References](../reference/api/synapse.md)

### How do I access the APIs?

All our APIs are documented in the [API section](../reference/api/synapse.md). Each service provides OpenAPI specifications and client libraries.

### Where can I find API documentation?

API documentation is auto-generated and available for:
- [Synapse API](../reference/api/synapse.md)
- [OpenChallenges API](../reference/api/openchallenges.md)
- [Model-AD API](../reference/api/model-ad.md)
- [BixArena API](../reference/api/bixarena.md)
- [AMP-ALS API](../reference/api/amp-als.md)
- [Agora API](../reference/api/agora.md)

## Contributing

### How can I contribute?

We welcome contributions! Check out our [Contributing Guidelines](../contributions/overview.md) to get started. You can:
- Report bugs using our [Bug Report Template](../contributions/bug-report.md)
- Request features using our [Feature Request Guidelines](../contributions/guidelines.md)
- Submit pull requests with improvements

### How do I report issues?

Use our [GitHub Issues](https://github.com/Sage-Bionetworks/sage-monorepo/issues) page. Please use the appropriate template:
- Bug reports
- Feature requests
- Documentation improvements

### Can I collaborate with multiple authors on commits?

Yes! See our guide on [Creating Multi-author Commits](../developers-guide/creating-a-commit-with-multiple-authors.md).

## Troubleshooting

### My local setup isn't working

Common solutions:
1. Ensure you have the required dependencies installed
2. Check that your environment variables are set correctly
3. Verify your Docker setup if using containers
4. Review the [local development guide](../getting-started/local-dev.md)

### Build or test failures

1. Clear your cache: `nx reset`
2. Reinstall dependencies: `pnpm install`
3. Check for any environment-specific requirements
4. Review recent changes that might affect your project

### API connection issues

1. Verify your API endpoints and credentials
2. Check network connectivity
3. Review the specific API documentation
4. Ensure you're using the correct API version

## Updates & News

### How do I stay updated?

- Follow our [Updates page](../news.md) for the latest announcements
- Subscribe to our [Blog](../blog/index.md) for technical insights
- Watch our GitHub repository for releases

### Where can I find release notes?

Check our [Updates](../news.md) page for version-specific release notes and feature announcements.

## Still Need Help?

If you can't find the answer to your question:

1. Search our [GitHub Issues](https://github.com/Sage-Bionetworks/sage-monorepo/issues)
2. Create a new issue with the appropriate template
3. Contact our development team through GitHub

---

*This FAQ is continuously updated. If you think a question should be added, please [let us know](https://github.com/Sage-Bionetworks/sage-monorepo/issues/new?assignees=&labels=type%3A+docs&projects=&template=3-documentation.yml&title=%5BDocs%5D+FAQ+Request)!*
