---
paths:
  - '**'
---

# Jira / Atlassian MCP

Jira ticket lookups are powered by the official Atlassian plugin (`atlassian@claude-plugins-official`), enabled project-wide via `.claude/settings.json`. Tools are exposed under `mcp__plugin_atlassian_atlassian__*`.

## First-time setup

The plugin auto-installs/enables when the repo is opened in Claude Code, but each developer must authenticate once:

1. Run `/plugin` and click `atlassian`.
2. A browser window opens — complete the Atlassian OAuth flow.
3. The token caches in `~/.claude/`, which the devcontainer symlinks to `${workspaceFolder}/.claude-user/`, so auth survives container rebuilds.

If a tool call returns an auth error later, re-run `/plugin` to re-authenticate. Do not fall back to scraping or guessing ticket contents.

## Cloud ID

All Atlassian tools require a `cloudId`, which is a UUID — **not** a site URL. Sage's value:

```
beb5b1db-90d5-4034-877f-56ab70fb086f
```

(Site: `sagebionetworks.jira.com`.) If the value drifts, call `mcp__plugin_atlassian_atlassian__getAccessibleAtlassianResources` (no params) to look it up. Passing the URL as `cloudId` returns 404.

## Always read related context

A single ticket rarely tells the whole story. When fetching a ticket for any non-trivial purpose (PR descriptions, reviews, planning, status reports), also resolve:

- **Sub-tasks** — child issues that decompose the work.
- **Linked issues** — every link, regardless of type.
- **Parent / epic** — the story or epic the ticket rolls up to, plus that parent's own description and acceptance criteria.

This is what lets a skill speak to _why_ a change exists, not just _what_ changed. Use `searchJiraIssuesUsingJql` (e.g. `parent = SMR-772` or `issue in linkedIssues("SMR-772")`) to pull these in one call rather than chasing them serially.

## Common tools

Always pass `responseContentFormat: "markdown"` for readable output.

```
# Fetch a single issue
mcp__plugin_atlassian_atlassian__getJiraIssue(
  cloudId="beb5b1db-90d5-4034-877f-56ab70fb086f",
  issueIdOrKey="SMR-772",
  responseContentFormat="markdown"
)

# Search with JQL (e.g. all sub-tasks of an epic)
mcp__plugin_atlassian_atlassian__searchJiraIssuesUsingJql(
  cloudId="beb5b1db-90d5-4034-877f-56ab70fb086f",
  jql="parent = AG-1925",
  responseContentFormat="markdown"
)
```

## Ticket-ID prefixes

Examples seen in this repo: `SMR`, `AG`, `MG`, `QTL`. Not exhaustive — new projects can introduce their own prefixes, so prefer pattern matching over hard-coded lists when scanning branch names or PR titles.
