# Request for Comments (RFCs)

This directory contains proposals for significant changes to the system that require review and feedback before implementation.

## RFC Lifecycle

RFCs follow a structured review process to ensure proposals are well-vetted before implementation:

1. **Proposed**: Author creates RFC and publishes to docs site (PR #1)
2. **Under Review**: Author opens feedback PR for team discussion (PR #2)
3. **Approved**: RFC accepted, detailed architecture plan created in `docs/architecture/`
4. **Rejected**: RFC declined with documented rationale
5. **Implemented**: Architecture plan executed, RFC remains in `docs/rfcs/` as historical record

### Key Principles

- **Two-PR Workflow**: First PR publishes RFC to docs site, second PR collects feedback
- **RFCs Stay Put**: Approved RFCs remain in `docs/rfcs/` permanently; architecture plans are created as NEW files in `docs/architecture/` that reference the source RFC
- **Centralized Feedback**: All discussion happens in PR #2 for easy reference
- **Lightweight Proposals**: RFCs focus on "why" and "what", detailed "how" comes in architecture plan after approval

See [Detailed Submission Workflow](#detailed-submission-workflow) below for step-by-step instructions.

## Active RFCs

| RFC                                                        | Title                                    | Status       | Author           | Date       |
| ---------------------------------------------------------- | ---------------------------------------- | ------------ | ---------------- | ---------- |
| [0001](./0001-bixarena-leaderboard-snapshot-automation.md) | BixArena Leaderboard Snapshot Automation | Under Review | Thomas Schaffter | 2026-01-09 |

## Template

See [template.md](./template.md) for the RFC template.

## Guidelines

- Use clear, descriptive titles
- Include motivation, proposed solution, alternatives considered
- Focus on "why" and "what", not just "how"
- Keep implementation details at appropriate level (not too granular)
- Include timeline and success criteria

---

## Detailed Submission Workflow

### Step 1: Publish RFC to Docs Site (PR #1)

This PR adds your RFC to the documentation site for easy reading with rendered diagrams.

1. **Choose an RFC number**: Check the [Active RFCs](#active-rfcs) table to find the next available number
2. **Create branch** using a descriptive name (not the RFC number to avoid conflicts):

   ```bash
   git switch -c docs/rfc-brief-description
   ```

3. **Create RFC file** from [template.md](./template.md): `NNNN-product-brief-title.md`
4. **Add to README**: Insert row in [Active RFCs](#active-rfcs) table with status "Proposed"
5. **Commit and push**:

   ```bash
   git add docs/rfcs/NNNN-product-brief-title.md
   git add docs/rfcs/README.md
   git commit -m "docs: Add RFC-NNNN for [brief description]"
   git push origin docs/rfc-brief-description
   ```

6. **Open PR #1** using this template (copy-paste into PR description):

   ```markdown
   ## RFC Publication

   This PR publishes RFC-NNNN to the documentation site.

   **Title**: [Full RFC title]
   **Author**: @[your-github-username]
   **Status**: Proposed

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

7. **Quick review**: Maintainer verifies compliance (template followed, no sensitive info) and merges

### Step 2: Collect Feedback (PR #2)

This PR allows reviewers to provide feedback in a centralized location.

1. **After PR #1 merges**, update your local main branch:

   ```bash
   git switch main
   git pull origin main
   ```

2. **Create feedback branch** from updated main:

   ```bash
   git switch -c rfc-NNNN-feedback
   ```

3. **Add discussion link** to RFC frontmatter:

   ```yaml
   ---
   status: Under Review
   discussion: https://github.com/Sage-Bionetworks/sage-monorepo/pull/XXXX
   ---
   ```

4. **Commit and push**:

   ```bash
   git add docs/rfcs/NNNN-product-brief-title.md
   git commit -m "RFC-NNNN: Add discussion field for feedback tracking"
   git push origin rfc-NNNN-feedback
   ```

5. **Open PR #2** using this template (copy-paste into PR description):

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

6. **Incorporate feedback**: Address comments by updating the RFC file in this branch
7. **Decision**: Once consensus is reached, close PR #2 and update RFC status:
   - **Approved**: Update status to "Approved"
   - **Rejected**: Update status to "Rejected" with documented rationale
   - **Needs Revision**: Continue iterating in PR #2 until resolved

### Why Two PRs?

- **PR #1** gets the RFC published quickly so it's readable on the docs site with rendered Mermaid diagrams
- **PR #2** centralizes all feedback in one place and allows easy iteration
- **Avoids conflicts** with RFC numbering by using descriptive branch names
- **Clear separation** between publishing (compliance check) and review (technical feedback)
