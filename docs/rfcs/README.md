# RFCs (Request for Comments)

This directory contains proposals for significant changes to the system that require review and feedback before implementation.

## Process

1. **Proposal**: Create a new RFC document in this directory with format `NNNN-product-brief-title.md`
2. **Review**: Open a PR for team review and discussion
3. **Decision**: RFC is either approved, rejected, or needs revision
4. **Implementation**: Approved RFCs are moved to `docs/architecture/` and work begins
5. **Archive**: Rejected RFCs can be moved to `docs/rfcs/rejected/` with decision notes

## Active RFCs

| RFC                                                        | Title                           | Product  | Status       | Author           | Date       |
| ---------------------------------------------------------- | ------------------------------- | -------- | ------------ | ---------------- | ---------- |
| [0001](./0001-bixarena-leaderboard-snapshot-automation.md) | Leaderboard Snapshot Automation | BixArena | Under Review | Thomas Schaffter | 2026-01-09 |

## Template

See [template.md](./template.md) for the RFC template.

## Guidelines

- Use clear, descriptive titles
- Include motivation, proposed solution, alternatives considered
- Focus on "why" and "what", not just "how"
- Keep implementation details at appropriate level (not too granular)
- Include timeline and success criteria
