# Documentation Submission Workflow

This guide describes the standard two-PR workflow for submitting technical documentation (RFCs, Architecture plans, and ADRs) to this repository.

## Overview

All technical documentation follows a **two-PR workflow** to ensure proposals are easily readable on the docs site while centralizing feedback:

1. **PR #1: Publish to Docs Site** - Quick merge to make the document readable with rendered diagrams
2. **PR #2: Collect Feedback** - Centralized location for team review and discussion

!!! info "Why Two PRs?"

    - **PR #1** gets the document published quickly so it's readable on the docs site with rendered Mermaid diagrams
    - **PR #2** centralizes all feedback in one place and allows easy iteration
    - **Clear separation** between publishing (compliance check) and review (technical feedback)

---

## Step 1: Publish Document to Docs Site (PR #1)

This PR adds your document to the documentation site for easy reading with rendered diagrams.

### 1.1 Choose Document Number

Check the appropriate page to find the next available number:

- **RFCs**: Check the RFCs table on the [RFCs page](./rfcs/README.md) for sequential numbering (e.g., `0002-bixarena-leaderboard-automation.md`)
- **Architecture**: Architecture plans don't use numbers (e.g., `bixarena-leaderboard-automation-plan.md`)
- **ADRs**: Check the ADRs table on the [ADRs page](./adr/README.md) for sequential numbering (e.g., `0001-use-docker-for-lambda.md`)

### 1.2 Create Branch

Create a branch from `main` using a **descriptive name** (not the document number to avoid conflicts):

```bash
# For RFCs
git switch -c docs/rfc-brief-description

# For Architecture plans
git switch -c docs/arch-brief-description

# For ADRs
git switch -c docs/adr-brief-description
```

### 1.3 Create Document File

Copy the appropriate template and fill it out:

| Document Type | Template                                                    | Filename Format               | Example                                                     |
| ------------- | ----------------------------------------------------------- | ----------------------------- | ----------------------------------------------------------- |
| RFC           | [docs/rfcs/template.md](./rfcs/template.md)                 | `NNNN-product-brief-title.md` | `docs/rfcs/0002-bixarena-leaderboard-automation.md`         |
| Architecture  | [docs/architecture/template.md](./architecture/template.md) | `product-brief-title-plan.md` | `docs/architecture/bixarena-leaderboard-automation-plan.md` |
| ADR           | [docs/adr/template.md](./adr/template.md)                   | `NNNN-brief-title.md`         | `docs/adr/0001-use-docker-for-lambda.md`                    |

### 1.4 Add to README

Add a row to the appropriate table in the directory's README.md with the **initial status**:

| Document Type | File to Update                | Initial Status    |
| ------------- | ----------------------------- | ----------------- |
| RFC           | `docs/rfcs/README.md`         | `Open for Review` |
| Architecture  | `docs/architecture/README.md` | `Active`          |
| ADR           | `docs/adr/README.md`          | `Open for Review` |

!!! note

    These are the initial status values when first publishing. See [Status Values](#status-values) in Quick Reference for the full status progression.

### 1.5 Commit and Push

Add both the document file and updated README, then commit and push:

```bash
# Example for RFC
git add docs/rfcs/NNNN-product-brief-title.md docs/rfcs/README.md
git commit -m "docs: add RFC-NNNN for [brief description]"
git push origin docs/rfc-brief-description

# Adapt the paths and commit message for Architecture plans or ADRs
```

### 1.6 Open PR #1

Open a pull request using the appropriate template below (copy-paste into PR description):

#### RFC Publication Template

```markdown
## RFC Publication

This PR publishes RFC-NNNN: [Full RFC title] to the documentation site.

## Checklist

- [ ] RFC follows template structure
- [ ] RFC number is sequential and unique
- [ ] Added to the RFCs table in README
- [ ] All sections completed
- [ ] No sensitive information included

## Next Steps

After this PR merges, I will open a second PR to collect feedback and discussion.

---

📄 [View rendered RFC on docs site after merge](https://sage-bionetworks.github.io/sage-monorepo/rfcs/NNNN-product-brief-title/)
```

#### Architecture Plan Publication Template

```markdown
## Architecture Plan Publication

This PR publishes [Full plan title] to the documentation site.

**Related RFC**: [Link to RFC if applicable]

## Checklist

- [ ] Plan follows template structure
- [ ] Added to Architecture Documents table
- [ ] All sections completed
- [ ] Diagrams added to `architecture/diagrams/` if applicable
- [ ] No sensitive information included

## Next Steps

After this PR merges, I will open a second PR to collect feedback and discussion.

---

📄 [View rendered plan on docs site after merge](https://sage-bionetworks.github.io/sage-monorepo/architecture/product-brief-title-plan/)
```

#### ADR Publication Template

```markdown
## ADR Publication

This PR publishes ADR-NNNN: [Full ADR title] to the documentation site.

## Checklist

- [ ] ADR follows template structure
- [ ] ADR number is sequential and unique
- [ ] Added to ADRs table
- [ ] All sections completed (Status, Context, Decision, Consequences)
- [ ] No sensitive information included

## Next Steps

After this PR merges, I will open a second PR to collect feedback and discussion.

---

📄 [View rendered ADR on docs site after merge](https://sage-bionetworks.github.io/sage-monorepo/adr/NNNN-brief-title/)
```

### 1.7 Review and Merge

A maintainer verifies compliance (template followed, no sensitive info) and merges the PR.

---

## Step 2: Collect Feedback (PR #2)

This PR allows reviewers to provide feedback in a centralized location.

### 2.1 Create Feedback Branch

After PR #1 merges, create a new branch from `main`:

```bash
# For RFCs
git switch -c docs/rfc-NNNN-feedback

# For Architecture plans
git switch -c docs/arch-[brief-name]-feedback

# For ADRs
git switch -c docs/adr-NNNN-feedback
```

### 2.2 Make Initial Commit

To open a PR, you need at least one commit. Create a placeholder commit that you'll update later:

```bash
# Create empty commit to enable PR creation
git commit --allow-empty -m "docs: open feedback PR for [RFC-NNNN|architecture|ADR-NNNN]"
git push origin [branch-name]
```

### 2.3 Open PR #2

Open a pull request from your feedback branch to `main` and copy the PR URL. You'll need it in the next step.

### 2.4 Add Discussion Link

Update the document's frontmatter to include the PR #2 URL:

```yaml
---
discussion: https://github.com/Sage-Bionetworks/sage-monorepo/pull/XXXX # Add PR #2 URL
---
```

Commit and push this change:

```bash
git add docs/[rfcs|architecture|adr]/[filename].md
git commit -m "docs: add discussion link for feedback tracking"
git push origin [branch-name]
```

!!! note

    Status remains unchanged (already set to "Open for Review" or "Active" in PR #1).

### 2.5 Add PR Description

Update PR #2's description using the appropriate template below (copy-paste and fill in):

#### RFC Feedback Template

```markdown
## RFC Feedback: [RFC Title]

This PR collects feedback and discussion on RFC-NNNN. Please review the [rendered RFC on the docs site](https://sage-bionetworks.github.io/sage-monorepo/rfcs/NNNN-product-brief-title/) and provide your feedback through PR comments.

## Review Focus Areas

- [ ] **Motivation**: Is the problem clearly articulated?
- [ ] **Proposed Solution**: Does the approach make sense?
- [ ] **Alternatives**: Are other options adequately considered?
- [ ] **Success Criteria**: Are metrics measurable and appropriate?
- [ ] **Timeline**: Is the schedule realistic?
- [ ] **Open Questions**: Can you help answer unresolved questions?

## Reviewers

@[tag relevant team members]

## Discussion

Please add your comments, questions, and suggestions below or as inline comments on this PR.
```

#### Architecture Plan Feedback Template

```markdown
## Architecture Plan Feedback: [Plan Title]

**Related RFC**: [Link to RFC if applicable]

This PR collects feedback and discussion on the architecture plan. Please review the [rendered plan on the docs site](https://sage-bionetworks.github.io/sage-monorepo/architecture/product-brief-title-plan/) and provide your feedback through PR comments.

## Review Focus Areas

- [ ] **Technical Approach**: Is the architecture sound?
- [ ] **Implementation Details**: Are the implementation steps clear?
- [ ] **Dependencies**: Are all dependencies identified?
- [ ] **Security**: Are security considerations adequate?
- [ ] **Performance**: Are performance implications addressed?
- [ ] **Testing**: Is the testing strategy comprehensive?

## Reviewers

@[tag relevant team members]

## Discussion

Please add your comments, questions, and suggestions below or as inline comments on this PR.
```

#### ADR Feedback Template

```markdown
## ADR Feedback: [ADR Title]

This PR collects feedback and discussion on ADR-NNNN. Please review the [rendered ADR on the docs site](https://sage-bionetworks.github.io/sage-monorepo/adr/NNNN-brief-title/) and provide your feedback through PR comments.

## Review Focus Areas

- [ ] **Context**: Is the context clear and complete?
- [ ] **Decision**: Is the decision well-justified?
- [ ] **Alternatives**: Were alternatives adequately considered?
- [ ] **Consequences**: Are consequences (positive and negative) identified?
- [ ] **Impact**: Is the impact on existing systems understood?

## Reviewers

@[tag relevant team members]

## Discussion

Please add your comments, questions, and suggestions below or as inline comments on this PR.
```

### 2.6 Incorporate Feedback

Address comments by updating the document file in this branch. Push changes as needed:

```bash
git add docs/[rfcs|architecture|adr]/[filename].md
git commit -m "docs: address feedback - [brief description]"
git push origin [branch-name]
```

### 2.7 Final Decision

Once consensus is reached, update the document status and close PR #2:

#### For RFCs:

- **Approved**: Update status to "Approved" in the README. After merging, create a detailed architecture plan as a NEW document in `docs/architecture/`
- **Rejected**: Update status to "Rejected" with documented rationale in the README
- **Needs Revision**: Continue iterating in PR #2 until resolved

#### For Architecture Plans:

- **Ready for Implementation**: Status remains "Active", begin implementation. Keep the document updated as implementation evolves
- **Needs Changes**: Continue iterating in PR #2 until resolved
- **Superseded**: Update status to "Superseded" only if the architectural approach fundamentally changes and requires a completely new document. Link to the new plan

#### For ADRs:

- **Accepted**: Update status to "Accepted"
- **Rejected**: Update status to "Rejected" with documented rationale
- **Superseded**: Update status to "Superseded" and link to new ADR

---

## Quick Reference

### Branch Naming

| Document Type        | Branch Name Pattern               |
| -------------------- | --------------------------------- |
| RFC (PR #1)          | `docs/rfc-brief-description`      |
| RFC (PR #2)          | `docs/rfc-NNNN-feedback`          |
| Architecture (PR #1) | `docs/arch-brief-description`     |
| Architecture (PR #2) | `docs/arch-[brief-name]-feedback` |
| ADR (PR #1)          | `docs/adr-brief-description`      |
| ADR (PR #2)          | `docs/adr-NNNN-feedback`          |

### Commit Message Patterns

| Action              | Pattern                                                    |
| ------------------- | ---------------------------------------------------------- |
| Add RFC             | `docs: add RFC-NNNN for [brief description]`               |
| Add Architecture    | `docs: add architecture plan for [brief description]`      |
| Add ADR             | `docs: add ADR-NNNN for [brief description]`               |
| Add discussion link | `docs: add discussion link for feedback tracking`          |
| Address feedback    | `docs: address feedback - [brief description]`             |
| Open feedback PR    | `docs: open feedback PR for [RFC-NNNN\|architecture\|...]` |

### Status Values

| Document Type | Status Values                                  |
| ------------- | ---------------------------------------------- |
| RFC           | Open for Review → Approved/Rejected            |
| Architecture  | Active → Implemented/Superseded                |
| ADR           | Open for Review → Accepted/Rejected/Superseded |

---

## Tips

- **Use descriptive branch names for PR #1** (not document numbers for RFCs/ADRs) to avoid conflicts when multiple people work simultaneously. PR #2 uses the document number since it's already assigned
- **Check RFCs and ADRs tables** on their respective pages before choosing a number to ensure uniqueness
- **Keep PR #1 focused** on publishing only; save technical discussions for PR #2
- **Update status in document tables** when document reaches final state (Approved, Accepted, Rejected, etc.)
- **Link related documents** (e.g., link architecture plan back to source RFC)
