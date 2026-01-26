# Architecture Documentation

This directory contains architecture plans, design documents, and system diagrams for implementation or in active development.

## Process

Architecture documents typically originate from approved RFCs in `docs/rfcs/`. Once an RFC is approved, a detailed architecture plan is created here as a new document that references the source RFC (the RFC remains in `docs/rfcs/` as historical record).

## Architecture Documents

| Document                                                                                       | Status      | Date       | Description                                           |
| ---------------------------------------------------------------------------------------------- | ----------- | ---------- | ----------------------------------------------------- |
| [Python FastAPI Microservice with JWT Authentication](./python-ai-service-integration-plan.md) | Implemented | 2025-12-01 | JWT authentication integration with Python AI service |

## Architecture Diagrams

Diagrams are stored in the [docs/architecture/diagrams/](diagrams/README.md) subdirectory.

| Diagram                                                                   | Description                 | Last Updated |
| ------------------------------------------------------------------------- | --------------------------- | ------------ |
| [BixArena Architecture](./diagrams/bixarena-architecture.gif)             | Current system architecture | 2026-01-26   |
| [OpenChallenges Architecture](./diagrams/openchallenges-architecture.gif) | Current system architecture | 2026-01-26   |

## Directory Structure

```
architecture/
├── README.md                           # This file
├── diagrams/                          # Architecture diagrams (*.gif, *.png, *.svg, *.html)
│   ├── bixarena-architecture.gif
│   └── bixarena-architecture-v1.gif
└── *.md                               # Architecture documents
```

## Template

See [template.md](./template.md) for the architecture plan template.

## Submitting an Architecture Plan

See the [Documentation Submission Workflow](../submission-workflow.md) guide for detailed step-by-step instructions on the two-PR process.

## Guidelines

- Documents in this directory are for implementation (source RFC was approved)
- Use past or present tense (not future/proposal tense)
- Keep documents updated as implementation evolves
- Reference related ADRs and RFCs for context
- Store diagrams in the `docs/architecture/diagrams/` subdirectory (supports `.gif`, `.png`, `.svg`, `.html` with Mermaid)

## Document Status Definitions

- **Active**: Architecture plan is available and ready for implementation or in progress
- **Implemented**: Completed and deployed
- **Superseded**: Replaced by newer design (archive or add note)

## Related Documentation

- See [RFCs](../rfcs/README.md) for proposals under review
- See [ADRs](../adr/README.md) for specific architectural decisions
