# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is a multi-language Nx monorepo for Sage Bionetworks projects, including TypeScript, Java, Python, and R. It contains multiple applications and libraries related to scientific research, data visualization, and biomedical informatics. The codebase uses Angular, React, Spring Boot, and FastAPI.

## Environment

This project runs inside a pre-built devcontainer (`ghcr.io/sage-bionetworks/sage-monorepo-devcontainer`). All dependencies (Node.js, Java, Python, databases, tools) are pre-installed — no initial setup is required when starting a new session.

## Common Commands

### Nx (TypeScript/Angular/React)

```bash
nx test [project-name]          # run tests for a project
nx lint [project-name]          # lint a project
nx build [project-name]         # build a project
nx affected --target=test       # run tests only for affected projects
nx run-many --target=build      # build all projects
```

### Serving an App (Dev Server)

Most apps require the full stack (databases, APIs, etc.) running before the Angular dev server is useful. The typical workflow:

```bash
# 1. Check if the stack is already running
docker ps

# 2. If not running, start the full stack in detached Docker containers
nx serve-detach <project-name>   # e.g. nx serve-detach model-ad

# 3. Remove the app container so the dev server can take its place
docker rm -f <project-name>-app  # e.g. docker rm -f model-ad-app

# 4. Start the Angular dev server for that app
nx serve <project-name>-app      # e.g. nx serve model-ad-app
```

Skip step 2 if Docker containers for the stack are already running.

### Gradle (Java/Spring Boot)

```bash
# Run from repo root — use project path notation
./gradlew :project-name:test --no-daemon --console=plain
./gradlew :project-name:test --tests "*MyTestClass*" --stacktrace --no-daemon --console=plain
./gradlew :project-name:testUnit --no-daemon --console=plain
./gradlew :project-name:testIntegration --no-daemon --console=plain
```

### Python

```bash
uv sync                   # install dependencies
uv run pytest             # run tests
uv run ruff check         # lint
```

## Architecture

### Structure

- `apps/` — standalone applications (agora, openchallenges, model-ad, bixarena, amp-als, etc.)
- `libs/` — shared libraries organized by scope (e.g., `libs/agora/`, `libs/openchallenges/`)
- `tools/` — build utilities and custom Nx executors
- `gradle/libs.versions.toml` — single source of truth for all Java dependency versions
- `buildSrc/` — Gradle convention plugins applied automatically by project location

### Main Products

| Product            | Description                           | Stack                            |
| ------------------ | ------------------------------------- | -------------------------------- |
| **agora**          | Alzheimer's disease evidence explorer | Angular, Node.js, MongoDB        |
| **openchallenges** | Scientific challenge platform         | Angular, Spring Boot, PostgreSQL |
| **model-ad**       | AD model data platform                | Angular, Spring Boot             |
| **bixarena**       | Battle arena game platform            | Angular, Spring Boot, Python     |

### OpenAPI-First Workflow

API contracts are defined first in OpenAPI specs. Server stubs and client SDKs (Angular, Java, Python) are generated from these specs. Implement business logic in the generated skeletons — do not hand-edit generated files.

After modifying an API spec, regenerate all dependent clients for that scope:

```bash
nx run-many -t=generate -p=<scope>-*   # e.g. -p=model-ad-*
```

### Dependency Constraints

Module boundaries are enforced via ESLint (`@nx/enforce-module-boundaries`). Scoped libraries can only depend on same-scope or lower-scope libraries:

- `scope:agora` → can use `agora`, `explorers`, `shared`
- `scope:model-ad` → can use `model-ad`, `explorers`, `shared`
- `scope:explorers` → can use `explorers`, `shared`
- `scope:shared` → can only use `shared`

### Gradle Convention Plugins

Projects automatically receive convention plugins based on their path in `buildSrc/`:

- `apps/*/` → `sage.spring-boot-application`
- Java libs → `sage.java-library` or `sage.spring-boot-library`

## Import Conventions

Always use the `@sagebionetworks/` path prefix defined in `tsconfig.base.json`:

```typescript
// CORRECT
import { Component } from '@sagebionetworks/agora/ui';

// INCORRECT
import { Component } from '../../../libs/agora/ui';
```

## Project Configuration

Each project has a `project.json` defining targets (build, test, lint, serve, integration-test) and tags:

- `type:feature|ui|util|service`
- `scope:agora|openchallenges|model-ad|bixarena|explorers|shared`
- `language:typescript|java|python`

Always refer to `package.json` for exact dependency versions when looking up API docs.

## Code Quality

- Write self-documenting code with meaningful names
- Avoid unnecessary comments; use docstrings/Javadoc for public APIs only
