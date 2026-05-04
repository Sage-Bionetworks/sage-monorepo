---
name: pr-description
description: >
  Generates a PR title and description body following this repository's conventions.
  Use this skill whenever the user asks to create, write, generate, or update a PR description,
  or when working with an active pull request that needs documentation. Also use this when the
  user says things like "write up this PR", "draft the PR", "prepare the PR", or "describe these changes".
---

# PR Description Generator

Generate a PR title and a markdown description body that a contributor can paste directly into
a new GitHub pull request. Save the output to a single file in `tmp/pr-description/` at the
repo root (this directory is gitignored).

## Output File

Write one file: `tmp/pr-description/<ticket-id>.md`, where `<ticket-id>` is the Jira ticket
found in Step 1 (from the branch name or commit messages). For example, if the branch is
`model-ad/MG-893-cutoff-button-alignment`, the output file is `tmp/pr-description/MG-893.md`.

The file starts with the PR title as an H1 heading, followed by the description body:

```markdown
# feat(model-ad): fix alignment of significance controls (MG-893)

## Description

...
```

Tell the user where the file is after writing it.

---

## Step 1 — Identify the Jira Ticket

The ticket ID appears in one of these places (check in order):

1. The current git branch name — pattern is usually `scope/TICKET-ID-slug`
   (e.g., `model-ad/MG-897-revert-debug-logging` → ticket `MG-897`)
2. Recent commit messages on the current branch (not on `main`)
3. The user may state it directly

Common ticket prefixes in this repo: **AG**, **MG**, **QTL**, **SMR**.

### Fetching ticket details from Jira

This skill expects a Jira MCP server (e.g., `mcp-jira-cloud`) to be configured. Use the
MCP tools to fetch the ticket. Pull:

- Summary, description, and acceptance criteria
- All **sub-tasks** (fetch them individually to get their full details)
- All **linked/related issues**

Acceptance criteria are especially valuable — they feed directly into the Testing section.

**If the Jira request fails due to an authentication or authorization error (401, 403, or
similar), stop immediately and ask the user to authenticate. Do not continue generating the
PR description until Jira access is confirmed.** Non-auth errors (404, network timeout) are
fine to work around — just note that the ticket couldn't be fetched and proceed with what you
have.

If no Jira MCP server is configured, skip the fetch and note in the output that Jira details
could not be retrieved. Use whatever context is available from the branch name, commit
messages, and code changes.

---

## Step 2 — Analyze the Changes

Look at all commits on the current branch that are not on `main`:

```bash
git log main..HEAD --oneline
git diff main...HEAD --stat
```

Read the changed files to understand what was done. Categorize changes by:

- **Type** and **scope** — see the allowed values in `/.github/workflows/lint-pr.yml`
- **Technology stack** — Java/Spring Boot, TypeScript/Angular, Python, etc.
- **Breaking changes** — API modifications, schema changes
- **Visual changes** — UI component additions/modifications, styling updates, layout changes

---

## Step 3 — Generate the PR Title

The title must pass the `lint-pr` workflow (semantic PR format):

```
type(scope): lowercase subject (TICKET-IDs)
```

Rules (from `/.github/workflows/lint-pr.yml`):

- **type**: one of `build`, `chore`, `ci`, `docs`, `feat`, `fix`, `infra`, `perf`, `refactor`, `revert`, `style`, `test`
- **scope** (optional): one of the scopes defined in `lint-pr.yml`
- **subject**: must NOT start with an uppercase letter
- Append the Jira ticket ID(s) in parentheses at the end, e.g., `(AG-2068)` or `(AG-2049, AG-2067)`

Match the style of recent merged PRs. Check a few with:

```bash
git log main --oneline -10
```

Examples from this repo:

- `feat(agora): add drug details summary tab (AG-2068)`
- `fix(model-ad): fix alignment of significance controls (MG-893)`
- `chore(deps): bump docker/metadata-action from 5.6.1 to 6.0.0`

---

## Step 4 — Generate the Description Body

Use this exact template. Do not add sections or change headings — this matches the
repository's `.github/pull_request_template.md`.

```markdown
## Description

[2-4 sentences explaining WHY these changes were made and the business/technical motivation.
Focus on the objective and benefits, not a list of what changed.]

## Related Issue

[Link to the Jira ticket. Include sub-tasks and related issues if they exist.]

- [TICKET-ID](https://sagebionetworks.jira.com/browse/TICKET-ID)
  - Sub-task: [TICKET-ID](url) — summary
  - Related: [TICKET-ID](url) — summary

## Changelog

- [High-level change — imperative voice, e.g., "Add feature X"]
- [Another high-level change]

## Testing

[Steps a human reviewer can follow to verify this PR works correctly.
Do NOT include unit test or e2e test commands — CI handles those.]

- [ ] Step a reviewer can perform manually
- [ ] Another verification step

### Screenshots

[If the PR includes visual/UI changes, suggest specific screenshots to capture.]

| Before                                       | After                                       |
| -------------------------------------------- | ------------------------------------------- |
| [describe what to screenshot before merging] | [describe what to screenshot after merging] |
```

---

## Content Guidelines

### Description

- Lead with the business or technical objective — why was this work done?
- Mention the Jira ticket context if it adds clarity
- Keep it to 2-4 sentences

### Related Issue

- Always link the Jira ticket with a full URL: `[MG-893](https://sagebionetworks.jira.com/browse/MG-893)`
- List sub-tasks and related issues underneath, indented, with their summaries
- If acceptance criteria were found, you do not need to reproduce them here — they inform the Testing section instead
- Omit this section only if there is genuinely no related ticket

### Changelog

- Use **flat, single-level bullets only** — no nested items
- Write in imperative voice: "Add", "Remove", "Update", "Fix"
- Keep items coarse-grained — group related file-level changes into one bullet
- Order from most to least important
- Aim for **3–7 bullets**; this is not an exhaustive file list

### Testing

This section is for **human reviewers**, not CI. Think about what a reviewer should manually
verify to gain confidence this PR is correct.

- Use a **task-list format** (`- [ ]`) so reviewers can check items off
- Pull from Jira acceptance criteria when available — translate them into concrete reviewer steps
- Focus on functional verification: navigation flows, data correctness, edge cases
- Include API verification steps if relevant (e.g., "call GET /api/x and confirm the response includes field Y")
- **Do NOT suggest running unit tests, e2e tests, or lint commands** — CI covers those
- Aim for **3–6 actionable steps**

### Screenshots

- Only include this sub-section when the PR has **visual or UI changes**
- Suggest **specific screens or components** to screenshot, not generic "take a screenshot"
- Use the before/after table format from the template
- Be precise: "Screenshot the gene details page header at /genes/APOE" is better than "Screenshot the page"
- If the change is purely backend or config, omit the Screenshots sub-section entirely

---

## Final Checklist (internal — do not include in output)

Before writing the files, verify:

- Title passes lint-pr format (type, scope, lowercase subject, ticket ID)
- Description explains the "why", not just the "what"
- Related Issue links are full URLs
- Changelog is 3–7 flat bullets in imperative voice
- Testing has actionable human-verification steps (no CI commands)
- Screenshots sub-section is present only for visual changes, with specific guidance
- Jira acceptance criteria are reflected in Testing steps where applicable
- Output is saved to `tmp/pr-description/TICKET-ID.md`
