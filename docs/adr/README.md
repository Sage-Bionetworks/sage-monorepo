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

## ADRs

| ADR | Title | Status | Date |
| --- | ----- | ------ | ---- |
| -   | -     | -      | -    |

## Template

See [template.md](./template.md) for the ADR template.

## Submitting an ADR

See the [Documentation Submission Workflow](../submission-workflow.md) guide for detailed step-by-step instructions on the two-PR process.

### Quick Overview

1. **Create**: Copy template, fill in context and decision
2. **Publish**: Submit PR #1 to publish to docs site
3. **Review**: Submit PR #2 for team feedback
4. **Accept**: Update status when consensus reached
5. **Supersede**: If decision changes, create new ADR referencing the old one

## Related Documentation

- **RFCs**: See [RFCs](../rfcs/README.md) for proposals under review
- **Architecture**: See [Architecture Plans](../architecture/README.md) for approved designs

## References

- [ADR GitHub Organization](https://adr.github.io/)
- [Documenting Architecture Decisions](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
- [AWS ADR Examples](https://docs.aws.amazon.com/prescriptive-guidance/latest/architectural-decision-records/appendix.html)
