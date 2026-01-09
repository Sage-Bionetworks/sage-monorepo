# Architecture Decision Records (ADRs)

This directory contains lightweight records of architectural decisions made during development.

## What is an ADR?

An Architecture Decision Record (ADR) captures a single architectural decision and its context. ADRs are:

- **Immutable**: Once accepted, they are not modified (superseded by new ADRs instead)
- **Focused**: Each ADR covers one decision
- **Concise**: Brief format focusing on decision and rationale

## ADR vs RFC

- **ADR**: Records a decision already made or being made (lightweight, focused)
- **RFC**: Proposes a comprehensive solution for review (detailed, exploratory)

Use ADRs for specific technical decisions. Use RFCs for major features or system changes.

## Format

ADRs use the format:

- `NNNN-title-of-decision.md` (e.g., `0001-use-docker-for-lambda.md`)
- Numbered sequentially
- Brief title in kebab-case

## Active ADRs

| ADR | Title | Status | Date |
| --- | ----- | ------ | ---- |
| -   | -     | -      | -    |

## Template

See [template.md](./template.md) for the ADR template.

## Example Workflow

1. **Create**: Copy template, fill in context and decision
2. **Review**: Submit PR for team feedback
3. **Accept**: Merge when consensus reached
4. **Supersede**: If decision changes, create new ADR referencing the old one

## References

- [ADR GitHub Organization](https://adr.github.io/)
- [Documenting Architecture Decisions](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
