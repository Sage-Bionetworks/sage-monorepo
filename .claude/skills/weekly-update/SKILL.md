---
name: weekly-update
description: Generates standardized weekly updates for the Sage-Bionetworks/sage-monorepo project, summarizing merged PRs, contributor activity, and project progress. Use this skill whenever someone asks for a weekly update, weekly summary, digest of merged PRs, progress report, standup notes, or anything that sounds like a periodic status update for stakeholders — even if they don't explicitly say "weekly update". Trigger on phrases like "what got done this week", "summarize the PRs", "create our weekly digest", "weekly standup summary", or "what did the team ship".
---

# Weekly Update Generator

## Purpose

Generate a clear, stakeholder-friendly weekly update for `Sage-Bionetworks/sage-monorepo`. The update summarizes merged PRs, highlights contributor work, and previews upcoming focus areas for both technical and non-technical readers.

## Workflow

### 1. Determine Date Range

If the user didn't provide a date range, ask — or default to the past 7 days from today's date. Format the range as "Month Day-Day, Year" (e.g., "April 3-10, 2026"). When spanning two months, write "March 28 – April 3, 2026".

### 2. Fetch Merged PRs

Get all PRs merged in the date range from `Sage-Bionetworks/sage-monorepo`.

**Using gh CLI (preferred):**

```bash
gh pr list \
  --repo Sage-Bionetworks/sage-monorepo \
  --state merged \
  --search "merged:>=YYYY-MM-DD merged:<=YYYY-MM-DD" \
  --limit 100 \
  --json number,title,author,mergedAt,body,labels
```

**Using GitHub MCP tools (fallback):**
Use `mcp__github__search_issues` with query `repo:Sage-Bionetworks/sage-monorepo is:pr is:merged merged:>=YYYY-MM-DD` to find merged PRs, then `mcp__github__get_pull_request` for details on each.

**If neither is available:** Stop and tell the user clearly:

> "I need either the `gh` CLI (authenticated) or a GitHub MCP server to fetch PR data. Neither appears to be available in this environment. Please authenticate `gh` with `gh auth login` and try again."
>
> Do not attempt to reconstruct PR history from local git files — that approach produces incomplete data and attribution errors.

For each PR, capture:

- **Number** and **Title**
- **Author**: exact GitHub username from `pr.author.login` (use as `@username`). This is the PR's primary author — do not use commit co-authors for attribution.
- **Scope**: the part in parentheses from the conventional commit prefix, e.g., `feat(model-ad)` → `model-ad`. Multiple scopes = note them all.
- **Type**: the prefix keyword — `feat`, `fix`, `chore`, `refactor`, `docs`, `test`, `build`, `ci`
- **Summary**: what the change does in 1-2 sentences, from the PR title and body

### 3. Identify Contributors

Count PRs per contributor. Check commit messages for `Co-authored-by:` lines to surface co-authors. List contributors by PR count (descending).

### 4. Categorize Changes

**→ Project Updates** (user-facing work): Features, bug fixes, and UI changes for specific product areas. If the scope is a product name (`model-ad`, `agora`, `bixarena`, `explorers`), it goes here.

**→ Technical Improvements** (infrastructure/tooling): Changes to build systems, CI/CD, shared libraries, Angular modernization, developer tooling, dependency upgrades, or repo-wide automation. Scopes like `monorepo`, `synapse`, `ci`, `build`, or generic framework-level scopes.

The deciding question: _Would a non-technical product stakeholder care about this change directly?_ If yes → Project Updates. If no → Technical Improvements. When a PR spans both (e.g., a shared library update that enables a product feature), follow the primary scope in the commit message.

### 5. Research "Coming Next"

Check what's in flight to write a concrete forward-looking section:

```bash
# Open PRs (what's actively being worked on)
gh pr list --repo Sage-Bionetworks/sage-monorepo --state open --limit 20 --json number,title,author,labels

# Open issues with milestones (upcoming priorities)
gh issue list --repo Sage-Bionetworks/sage-monorepo --state open --limit 20 --json number,title,labels,milestone
```

Use these to identify active initiatives and upcoming features. If the signals are thin, generalize from this week's themes.

### 6. Write the Update

Follow this template exactly:

---

## Update Template

```markdown
# Sage-Bionetworks/sage-monorepo Weekly Update

[Date Range]

## 🌟 Highlights

[2-3 sentences. Lead with the biggest themes — what shipped, what improved, how many PRs and contributors. Accessible to non-technical readers.]

## 📊 Project Updates

### [Project Name]

[One sentence framing this project's activity this week.]

- **[Change Title]**: [What it does and why it matters] (@author)
- **[Change Title]**: [Description] (@author1, @author2)

[Only include projects with merged PRs this week.]

## 🛠️ Technical Improvements

### [Category]

- **[Improvement Title]**: [Description and benefit] (@author)

[Common categories: Infrastructure Enhancements, Angular Modernization, Developer Experience, Build System Improvements, Testing Infrastructure, CI/CD Improvements]

## 👥 Contributors

This week's progress was made possible by the following contributors:

- @username1 - X PRs
- @username2 - Y PRs

[Sorted by PR count descending. Include everyone with a merged PR.]

## 🔮 Coming Next

[1-2 paragraphs. Base this on open PRs and issues. Be specific about active focus areas rather than vague generalities.]
```

---

## Content Guidelines

- **Highlights**: 2-3 sentences max. Mention total PR count and contributor count.
- **Bullets**: Bold the title, 1-2 sentence description, always include `@username` attribution.
- **Don't invent contributor handles** — use the exact GitHub username from the PR data.
- **Merge related PRs** into a single bullet when they're part of the same initiative (e.g., several migration PRs for the same library), rather than one bullet per PR.
- **Skip empty sections** — if there were no bug fixes this week, omit that subsection.
- **Verify PR counts** before writing the Contributors section — count carefully.

## Quality Checklist

Before finalizing:

- [ ] All merged PRs in the date range are accounted for
- [ ] PR counts per contributor are accurate (verified by counting)
- [ ] Every bullet has `@username` attribution using real GitHub usernames
- [ ] Section assignment (Project Updates vs Technical Improvements) follows the categorization rule
- [ ] Descriptions are concise and accessible to non-technical readers
- [ ] "Coming Next" reflects real signals from open PRs/issues, not pure speculation
- [ ] Emoji headers match the template
- [ ] Related PRs are grouped rather than listed individually
