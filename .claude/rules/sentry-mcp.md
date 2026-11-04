---
paths:
  - '**'
---

# Sentry MCP

Sentry lookups are powered by the official Sentry remote MCP server, configured project-wide in [`.mcp.json`](../../.mcp.json) at the repo root. Tools are exposed under `mcp__sentry__*`.

## First-time setup

The MCP server loads automatically when the repo is opened, but each developer does two one-time things:

1. **Approve the project MCP** — on first open, Claude Code prompts you to allow the project's `.mcp.json`. Approve it.
2. **Authenticate** — run `/mcp`, select sentry, and complete the Sentry OAuth flow in the browser. The token caches in `~/.claude/`, which the devcontainer symlinks to `${workspaceFolder}/.claude-user/`, so auth survives container rebuilds.

A personal `sentry` server added with `claude mcp add` takes precedence over the project one. Remove it (`claude mcp remove sentry -s local`) so everyone uses the same org scope.

## Organization and projects

The server URL is scoped to the `sage-bionetworks` organization, so tools need no organization argument. Each explorer app has a frontend project and an API project:

| Product  | Frontend   | API            |
| -------- | ---------- | -------------- |
| Agora    | `agora`    | `agora-api`    |
| Model-AD | `model-ad` | `model-ad-api` |
| QTL      | `qtl`      | `qtl-api`      |

Filter by project in the query, e.g. `project:model-ad` or `project:[agora,model-ad]`.

## Reading the data

- **Environments** are `dev`, `stage` and `prod`. Always filter by one; dev and stage traffic is mostly the team.
- **Releases** are named `<app>@<version>+<short-sha>` for tagged builds (e.g. `model-ad@1.1.0-rc3+f592f69`) and `<app>@edge+sha-<short-sha>` for dev builds. An event's `release` tag is the build the browser tab was running, which can be older than the build the environment serves.
- **Retention is about 30 days.** Older events are gone, so capture any number you need (e.g. errors after a release) within a month.
- **There are no user counts.** The apps send no user data to Sentry, so `userCount` is always 0. Count events instead.

## Read-only by default

The server exposes write tools as well as read tools: resolving or assigning issues (`update_issue`), notes, issue links, alert rules, monitors, DSNs, projects and teams. Use them only when the user explicitly asks for that specific change, and confirm first, since they change the team's Sentry. Searching, counting and reading issue details are always fine.
