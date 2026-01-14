# Request for Comments (RFCs)

This directory contains proposals for significant changes to the system that require review and feedback before implementation.

## RFC Lifecycle

RFCs follow a structured review process to ensure proposals are well-vetted before implementation:

1. **Open for Review**: Author creates RFC, publishes to docs site (PR #1), and opens feedback PR (PR #2)
2. **Approved**: RFC accepted, detailed architecture plan created in `docs/architecture/`
3. **Rejected**: RFC declined with documented rationale

Once approved, the RFC remains in `docs/rfcs/` as an immutable historical record. Implementation progress is tracked in the corresponding architecture plan in `docs/architecture/`.

### Key Principles

- **Two-PR Workflow**: First PR publishes RFC to docs site, second PR collects feedback
- **RFCs Stay Put**: Approved RFCs remain in `docs/rfcs/` permanently; architecture plans are created as NEW files in `docs/architecture/` that reference the source RFC
- **Centralized Feedback**: All discussion happens in PR #2 for easy reference
- **Lightweight Proposals**: RFCs focus on "why" and "what", detailed "how" comes in architecture plan after approval

### Submitting an RFC

See the [Documentation Submission Workflow](../submission-workflow.md) guide for detailed step-by-step instructions on the two-PR process.

## Active RFCs

| RFC                                                        | Title                                    | Status          | Author           | Date       |
| ---------------------------------------------------------- | ---------------------------------------- | --------------- | ---------------- | ---------- |
| [0001](./0001-bixarena-leaderboard-snapshot-automation.md) | BixArena Leaderboard Snapshot Automation | Open for Review | Thomas Schaffter | 2026-01-09 |

## Template

See [template.md](./template.md) for the RFC template.

## Guidelines

- Use clear, descriptive titles
- Include motivation, proposed solution, alternatives considered
- Focus on "why" and "what", not just "how"
- Keep implementation details at appropriate level (not too granular)
- Include timeline and success criteria
