---
paths:
  - '**'
---

# Jira / Atlassian MCP

Jira ticket lookups are powered by the official Atlassian remote MCP server, configured project-wide in [`.mcp.json`](../../.mcp.json) at the repo root. Tools are exposed under `mcp__atlassian__*`.

## First-time setup

The MCP server loads automatically when the repo is opened, but each developer does two one-time things:

1. **Approve the project MCP** — on first open, Claude Code prompts you to allow the project's `.mcp.json`. Approve it.
2. **Authenticate** — call any atlassian tool (or run `/mcp` and select atlassian) and complete the Atlassian OAuth flow in the browser. The token caches in `~/.claude/`, which the devcontainer symlinks to `${workspaceFolder}/.claude-user/`, so auth survives container rebuilds.

If a tool call returns an auth error later, re-run `/mcp` to re-authenticate. Do not fall back to scraping or guessing ticket contents.

## Cloud ID

All Atlassian tools require a `cloudId`, which is a UUID — **not** a site URL. Sage's value:

```
beb5b1db-90d5-4034-877f-56ab70fb086f
```

(Site: `sagebionetworks.jira.com`.) If the value drifts, call `mcp__atlassian__getAccessibleAtlassianResources` (no params) to look it up. Passing the URL as `cloudId` returns 404.

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
mcp__atlassian__getJiraIssue(
  cloudId="beb5b1db-90d5-4034-877f-56ab70fb086f",
  issueIdOrKey="SMR-772",
  responseContentFormat="markdown"
)

# Search with JQL (e.g. all sub-tasks of an epic)
mcp__atlassian__searchJiraIssuesUsingJql(
  cloudId="beb5b1db-90d5-4034-877f-56ab70fb086f",
  jql="parent = AG-1925",
  responseContentFormat="markdown"
)
```

## Ticket-ID prefixes

Examples seen in this repo: `SMR`, `AG`, `MG`, `QTL`. Not exhaustive — new projects can introduce their own prefixes, so prefer pattern matching over hard-coded lists when scanning branch names or PR titles.
