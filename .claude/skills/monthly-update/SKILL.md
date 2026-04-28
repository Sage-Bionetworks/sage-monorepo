---
name: monthly-update
description: Generates monthly update pages for the Sage monorepo documentation site, aggregating all pull requests merged to `main` during a given month into a structured, user-friendly page. Use this skill whenever someone asks for a monthly update, monthly summary, monthly changelog, or wants to create/regenerate the docs page for a specific month — even if they just say "generate the update for March" or "add April to the docs". Trigger on phrases like "monthly update", "monthly summary", "create the update for [month]", "add [month] to the docs", or "regenerate [month-year].md".
---

# Monthly Update Generator

## Purpose

Produce a complete monthly update page for `docs/updates/[month-year].md`, add it to `mkdocs.yml`, and update `docs/updates/index.md`. The page aggregates every PR merged to `main` during the target month into a readable narrative for both technical and non-technical audiences.

## Workflow

### 1. Confirm the target month

If the user didn't specify a month, ask. You need `YYYY-MM` to build the date range query.

### 2. Fetch merged PRs

Use the `gh` CLI (preferred) or GitHub MCP tools (fallback):

**gh CLI:**

```bash
gh pr list \
  --repo Sage-Bionetworks/sage-monorepo \
  --state merged \
  --search "merged:>=YYYY-MM-01 merged:<=YYYY-MM-31" \
  --limit 200 \
  --json number,title,author,mergedAt,body,labels,url
```

**GitHub MCP fallback:**

```
mcp__github__search_issues with query:
  repo:Sage-Bionetworks/sage-monorepo is:pr is:merged merged:YYYY-MM-01..YYYY-MM-31
```

Then use `mcp__github__get_pull_request` for details on each.

**If neither is available**, stop and tell the user:

> "I need either the `gh` CLI (authenticated) or a GitHub MCP server. Please run `gh auth login` and try again."

Capture for each PR: number, title, URL, author login, body (for ticket numbers).

### 3. Extract ticket numbers

Scan each PR title for ticket patterns like `SMR-xxx`, `MG-xxx`, `AG-xxx`. Include them in the PR line if present.

### 4. Identify contributors

Collect unique `author.login` values across all PRs. These go in the Thank You section.

### 5. Categorize PRs

Group PRs into logical categories based on conventional commit scopes and titles. Common categories:

- **Authentication & gateway** — auth flows, API gateways, reverse proxy
- **[Product name]** — product-specific features, fixes, and data work (agora, model-ad, bixarena, amp-als, explorers, qtl)
- **OpenAPI governance** — spec changes, client generation, tag/path normalization
- **Developer environment** — dev container, toolchain, dependency upgrades
- **Infrastructure & composability** — Docker Compose, Caddy, CI/CD pipelines
- **Automation & generated artifacts** — bot-generated PRs, automated updates
- **Documentation** — docs site, README, governance

The goal is logical groupings that tell a coherent story. Don't force every PR into a predefined bucket — create categories that fit the actual work done that month.

### 6. Write the monthly update page

Follow the template below exactly, including section order and capitalization rules.

### 7. Update navigation and index

- Add the new page to `mkdocs.yml` under the `Updates` nav in reverse chronological order (newest first)
- Add an entry to `docs/updates/index.md` in reverse chronological order

### 8. Final formatting check

- All GitHub usernames linked: `[@username](https://github.com/username)`
- Main headers (`##`) use exact capitalization from the template
- Subsection headers (`###`) and category headers use sentence case
- No PR counts in category titles
- Separator line `---` between Thank You and closing statement
- No duplicate sections

---

## Page Template

```markdown
# [Month] [Year]

_Published on [Month] [Day], [Year]_

[2-3 sentence intro. Lead with what the month accomplished at a high level. Mention total PR count as **[X pull requests](#pull-requests-merged)** and name primary contributors as [@username](https://github.com/username) links.]

## Summary

- **Total Pull Requests**: X merged PRs
- **Key Focus**: [1-2 sentence summary of main themes]
- **Major Projects**: [Comma-separated list of significant initiatives]

## Technical Architecture Overview

### [Subsection title in sentence case]

[2-3 sentences explaining the "why" and "what" of key technical decisions. Accessible to non-technical readers. Keep paragraphs short.]

### [Another subsection]

[Repeat for each major architectural theme. Aim for 3-6 subsections total.]

## Pull Requests Merged

### [Category title in sentence case]

- [#NUMBER](URL): PR title (TICKET-NUMBER)
- [#NUMBER](URL): PR title (TICKET-NUMBER)

### [Another category]

- [#NUMBER](URL): PR title

[Repeat for all categories. Every merged PR must appear exactly once.]

## Community Impact

[2-3 sentences on broader significance — what these changes mean for the research community, platform users, or contributors.]

## Thank You

Last but certainly not least, a big **_Thank You_** to the contributors of the Sage monorepo.

- [@username](https://github.com/username)
- [@username](https://github.com/username)

---

_[Closing inspirational sentence in italics. Include [Join us](https://github.com/Sage-Bionetworks/sage-monorepo) link.]_
```

---

## Formatting Rules

### PR lines

```
- [#NUMBER](https://github.com/Sage-Bionetworks/sage-monorepo/pull/NUMBER): PR title as-is from GitHub (TICKET-NUMBER)
```

- Use the PR title verbatim — no paraphrasing or added descriptions
- Include ticket numbers when they appear in the title (e.g., `SMR-xxx`, `MG-xxx`)
- Omit ticket suffix if none present

### Header capitalization

| Level                            | Rule                                                    | Example                              |
| -------------------------------- | ------------------------------------------------------- | ------------------------------------ |
| `#` (title)                      | "Month Year" — no "Update" suffix                       | `# September 2025`                   |
| `##` (main sections)             | Exact caps from template                                | `## Technical Architecture Overview` |
| `###` (subsections & categories) | Sentence case — only first word + proper nouns/acronyms | `### API modernization initiative`   |

### mkdocs.yml entry

```yaml
- Updates:
    - Overview: updates/index.md
    - September 2025: updates/september-2025.md # newest first
    - July 2025: updates/july-2025.md
```

### index.md entry

```markdown
- **[September 2025](september-2025.md)** - Brief description of key highlights
```

---

## Regenerating an Existing File

When rewriting an existing monthly update:

1. Replace the entire file content from start to finish
2. Ensure only one version of each section exists
3. Follow the same workflow above — fetch fresh PR data rather than relying on the existing file

---

## Quality Checklist

Before finishing:

- [ ] Every merged PR in the date range is listed exactly once
- [ ] No category title contains a PR count
- [ ] All GitHub usernames are linked using `[@username](https://github.com/username)`
- [ ] `mkdocs.yml` updated with new entry in correct chronological position
- [ ] `docs/updates/index.md` updated with new entry in correct chronological position
- [ ] Separator `---` present between Thank You and closing statement
- [ ] File saved to `docs/updates/[month-lowercase]-[year].md`
