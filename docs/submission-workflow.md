# Documentation Submission Workflow

This guide describes the standard two-PR workflow for submitting technical documentation (RFCs, Architecture plans, and ADRs) to this repository.

## Overview

All technical documentation follows a **two-PR workflow** to ensure proposals are easily readable on the docs site while centralizing feedback:

1. **PR #1: Publish to Docs Site** - Quick merge to make the document readable with rendered diagrams
2. **PR #2: Collect Feedback** - Centralized location for team review and discussion

## Why Two PRs?

- **PR #1** gets the document published quickly so it's readable on the docs site with rendered Mermaid diagrams
- **PR #2** centralizes all feedback in one place and allows easy iteration
- **Avoids conflicts** with numbering by using descriptive branch names
- **Clear separation** between publishing (compliance check) and review (technical feedback)

---

## Step 1: Publish Document to Docs Site (PR #1)

This PR adds your document to the documentation site for easy reading with rendered diagrams.

### 1.1 Choose Document Number

Check the appropriate README to find the next available number:

- **RFCs**: Check [docs/rfcs/README.md](./rfcs/README.md#active-rfcs) Active RFCs table
- **Architecture**: Check [docs/architecture/README.md](./architecture/README.md#active-architecture-documents) Active Architecture Documents table
- **ADRs**: Check [docs/adr/README.md](./adr/README.md#active-adrs) Active ADRs table

### 1.2 Create Branch

Create a branch using a **descriptive name** (not the document number to avoid conflicts):

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

| Document Type | Template                                                    | Filename Format               | Example                                   |
| ------------- | ----------------------------------------------------------- | ----------------------------- | ----------------------------------------- |
| RFC           | [docs/rfcs/template.md](./rfcs/template.md)                 | `NNNN-product-brief-title.md` | `0002-bixarena-leaderboard-automation.md` |
| Architecture  | [docs/architecture/template.md](./architecture/template.md) | `product-brief-title-plan.md` | `bixarena-leaderboard-automation-plan.md` |
| ADR           | [docs/adr/template.md](./adr/template.md)                   | `NNNN-brief-title.md`         | `0001-use-docker-for-lambda.md`           |

### 1.4 Add to README

Add a row to the appropriate table in the directory's README.md:

| Document Type | File to Update                | Status Value      |
| ------------- | ----------------------------- | ----------------- |
| RFC           | `docs/rfcs/README.md`         | `Open for Review` |
| Architecture  | `docs/architecture/README.md` | `Active`          |
| ADR           | `docs/adr/README.md`          | `Open for Review` |

### 1.5 Commit and Push

```bash
# For RFCs
git add docs/rfcs/NNNN-product-brief-title.md
git add docs/rfcs/README.md
git commit -m "docs: Add RFC-NNNN for [brief description]"
git push origin docs/rfc-brief-description

# For Architecture plans
git add docs/architecture/product-brief-title-plan.md
git add docs/architecture/README.md
git commit -m "docs: Add architecture plan for [brief description]"
git push origin docs/arch-brief-description

# For ADRs
git add docs/adr/NNNN-brief-title.md
git add docs/adr/README.md
git commit -m "docs: Add ADR-NNNN for [brief description]"
git push origin docs/adr-brief-description
```

### 1.6 Open PR #1

Open a pull request using the appropriate template below (copy-paste into PR description):

#### RFC Publication Template

```markdown
## RFC Publication

This PR publishes RFC-NNNN to the documentation site.

**Title**: [Full RFC title]
**Author**: @[your-github-username]
**Status**: Open for Review

## Checklist

- [ ] RFC follows template structure
- [ ] RFC number is sequential and unique
- [ ] Added to Active RFCs table in README
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

This PR publishes an architecture plan to the documentation site.

**Title**: [Full plan title]
**Author**: @[your-github-username]
**Related RFC**: [Link to RFC if applicable]

## Checklist

- [ ] Plan follows template structure
- [ ] Added to Active Architecture Documents table in README
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

This PR publishes ADR-NNNN to the documentation site.

**Title**: [Full ADR title]
**Author**: @[your-github-username]
**Status**: Open for Review

## Checklist

- [ ] ADR follows template structure
- [ ] ADR number is sequential and unique
- [ ] Added to Active ADRs table in README
- [ ] All sections completed (Status, Context, Decision, Consequences)
- [ ] No sensitive information included

## Next Steps

After this PR merges, I will open a second PR to collect feedback and discussion.

---

📄 [View rendered ADR on docs site after merge](https://sage-bionetworks.github.io/sage-monorepo/adr/NNNN-brief-title/)
```

### 1.7 Quick Review and Merge

A maintainer verifies compliance (template followed, no sensitive info) and merges the PR.

---

## Step 2: Collect Feedback (PR #2)

This PR allows reviewers to provide feedback in a centralized location.

### 2.1 Update Local Main Branch

After PR #1 merges, update your local main branch:

```bash
git switch main
git pull origin main
```

### 2.2 Create Feedback Branch

Create a new branch from the updated main:

```bash
# For RFCs
git switch -c rfc-NNNN-feedback

# For Architecture plans
git switch -c arch-[brief-name]-feedback

# For ADRs
git switch -c adr-NNNN-feedback
```

### 2.3 Add Discussion Link

Update the document's frontmatter to include the discussion link:

```yaml
---
discussion: https://github.com/Sage-Bionetworks/sage-monorepo/pull/XXXX # Add PR URL
---
```

**Note**: Status remains unchanged (already set to "Open for Review" or "Active" in PR #1).

**Status progression by document type:**

| Document Type | Status Progression                             |
| ------------- | ---------------------------------------------- |
| RFC           | Open for Review → Approved/Rejected            |
| Architecture  | Active → Implemented/Superseded                |
| ADR           | Open for Review → Accepted/Rejected/Superseded |

### 2.4 Commit and Push

```bash
# Add the updated document file
git add docs/[rfcs|architecture|adr]/[filename].md
git commit -m "[RFC-NNNN|Architecture|ADR-NNNN]: Add discussion link for feedback tracking"
git push origin [branch-name]
```

### 2.5 Open PR #2

Open a pull request using the appropriate template below (copy-paste into PR description):

#### RFC Feedback Template

```markdown
## RFC Feedback: [RFC Title]

**RFC**: RFC-NNNN - [Full title]
**Author**: @[your-github-username]
**Published**: [Link to docs site page]

## Purpose

This PR collects feedback and discussion on RFC-NNNN. Please review the [rendered RFC on the docs site]([link]) and provide your feedback through PR comments.

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

**Plan**: [Full title]
**Author**: @[your-github-username]
**Published**: [Link to docs site page]
**Related RFC**: [Link to RFC if applicable]

## Purpose

This PR collects feedback and discussion on the architecture plan. Please review the [rendered plan on the docs site]([link]) and provide your feedback through PR comments.

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

**ADR**: ADR-NNNN - [Full title]
**Author**: @[your-github-username]
**Published**: [Link to docs site page]

## Purpose

This PR collects feedback and discussion on ADR-NNNN. Please review the [rendered ADR on the docs site]([link]) and provide your feedback through PR comments.

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
git commit -m "[RFC-NNNN|Architecture|ADR-NNNN]: Address feedback - [brief description]"
git push origin [branch-name]
```

### 2.7 Final Decision

Once consensus is reached, update the document status and close PR #2:

#### For RFCs:

- **Approved**: Update status to "Approved", create detailed architecture plan in `docs/architecture/`
- **Rejected**: Update status to "Rejected" with documented rationale
- **Needs Revision**: Continue iterating in PR #2 until resolved

#### For Architecture Plans:

- **Approved**: Status remains "Active", begin implementation
- **Needs Changes**: Continue iterating in PR #2 until resolved
- **Superseded**: Update status to "Superseded" if replaced by newer plan

#### For ADRs:

- **Accepted**: Update status to "Accepted"
- **Rejected**: Update status to "Rejected" with documented rationale
- **Superseded**: Update status to "Superseded" and link to new ADR

---

## Quick Reference

### Branch Naming

| Document Type        | Branch Name Pattern           |
| -------------------- | ----------------------------- |
| RFC (PR #1)          | `docs/rfc-brief-description`  |
| RFC (PR #2)          | `rfc-NNNN-feedback`           |
| Architecture (PR #1) | `docs/arch-brief-description` |
| Architecture (PR #2) | `arch-[brief-name]-feedback`  |
| ADR (PR #1)          | `docs/adr-brief-description`  |
| ADR (PR #2)          | `adr-NNNN-feedback`           |

### Commit Message Patterns

| Action              | Pattern                                                                         |
| ------------------- | ------------------------------------------------------------------------------- |
| Add RFC             | `docs: Add RFC-NNNN for [brief description]`                                    |
| Add Architecture    | `docs: Add architecture plan for [brief description]`                           |
| Add ADR             | `docs: Add ADR-NNNN for [brief description]`                                    |
| Add discussion link | `[RFC-NNNN\|Architecture\|ADR-NNNN]: Add discussion link for feedback tracking` |
| Address feedback    | `[RFC-NNNN\|Architecture\|ADR-NNNN]: Address feedback - [brief description]`    |

### Status Values

| Document Type | Status Values                                  |
| ------------- | ---------------------------------------------- |
| RFC           | Open for Review → Approved/Rejected            |
| Architecture  | Active → Implemented/Superseded                |
| ADR           | Open for Review → Accepted/Rejected/Superseded |

---

## Tips

- **Use descriptive branch names** (not document numbers) to avoid conflicts when multiple people work simultaneously
- **Check README before choosing number** to ensure uniqueness
- **Keep PR #1 focused** on publishing only; save technical discussions for PR #2
- **Update status in README** when document reaches final state (Approved, Accepted, Rejected, etc.)
- **Link related documents** (e.g., link architecture plan back to source RFC)
