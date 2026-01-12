# Architecture Documentation

This directory contains approved architecture plans, design documents, and system diagrams that have been reviewed and are either implemented or in active development.

## Process

Architecture documents typically originate from approved RFCs in `docs/rfcs/`. Once an RFC is approved, it moves here and implementation begins.

## Active Architecture Documents

| Document                                                                                       | Status      | Date       | Description                                                              |
| ---------------------------------------------------------------------------------------------- | ----------- | ---------- | ------------------------------------------------------------------------ |
| [Python AI Service Integration](./python-ai-service-integration-plan.md)                       | Implemented | 2025-12    | JWT authentication integration with Python AI service                    |
| [BixArena Leaderboard Snapshot Automation](./bixarena-leaderboard-snapshot-automation-plan.md) | Approved    | 2026-01-09 | Automated daily leaderboard snapshot generation using Lambda/EventBridge |

## Architecture Diagrams

Diagrams are stored in the [diagrams/](diagrams/README.md) subdirectory.

| Diagram                                                             | Description                 | Last Updated |
| ------------------------------------------------------------------- | --------------------------- | ------------ |
| [BixArena Architecture](./diagrams/bixarena-architecture.gif)       | Current system architecture | -            |
| [BixArena Architecture v1](./diagrams/bixarena-architecture-v1.gif) | Previous version            | -            |

## Directory Structure

```
architecture/
├── README.md                           # This file
├── diagrams/                          # Architecture diagrams (*.gif, *.png, *.svg, *.html)
│   ├── bixarena-architecture.gif
│   └── bixarena-architecture-v1.gif
└── *.md                               # Architecture documents
```

## Guidelines

- Documents in this directory represent **approved** designs
- Use past or present tense (not future/proposal tense)
- Keep documents updated as implementation evolves
- Reference related ADRs and RFCs for context
- Store diagrams in the `diagrams/` subdirectory (supports .gif, .png, .svg, .html with Mermaid)

## Document Status Definitions

- **Proposed**: Under review (should be in `docs/rfcs/` instead)
- **Approved**: Reviewed and ready for implementation
- **In Progress**: Currently being implemented
- **Implemented**: Completed and deployed
- **Superseded**: Replaced by newer design (archive or add note)

## Related Documentation

- **RFCs**: See [../rfcs/](../rfcs/README.md) for proposals under review
- **ADRs**: See [../adr/](../adr/README.md) for specific architectural decisions
