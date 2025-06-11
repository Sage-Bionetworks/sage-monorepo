# Copilot Instructions for Sage Monorepo

## Project Overview

This is a multi-language `nx` monorepo for Sage Bionetworks projects and includes projects in TypeScript, Java, Python, and R.
It contains multiple applications and libraries related to scientific research, data visualization, and biomedical informatics.
The codebase uses Angular, React, and other modern web technologies.

## Monorepo Structure

The repository uses the standard Nx structure:

- `apps/` contains standalone applications.
- `libs/` contains shared libraries and components.
- `tools/` contains utility scripts.
- `docs/` contains documentation files.

## Import Conventions

The monorepo uses path mapping defined in `tsconfig.base.json`. Always use the `@sagebionetworks/` prefix for imports:

```typescript
// CORRECT
import { Component } from '@sagebionetworks/agora/ui';

// INCORRECT
import { Component } from '../../../libs/agora/ui';
```

## Project Configuration

Each project has a `project.json` file with:

- Project type, source root, and tags
- Build, test, lint, etc. configurations
- Tags convention: `type:feature|ui|util`, `scope:project-name`, `language:typescript|python|etc`

Always refer to `package.json` for the exact versions of dependencies when researching API documentation.

## Nx Commands

- Run tests: `nx test [project-name]`
- Run lint: `nx lint [project-name]`

## Angular Development Standards

When working with Angular code:

- Read [Angular LLM documentation](instructions/angular/llms.md). Use the provided links if you need to look up Angular concepts or APIs.
- Always use standalone components over NgModules
- Use signals for reactive state management
- Implement control flow with `@if`, `@for` instead of `*ngIf`, `*ngFor`
- Write tests with `@testing-library/angular` and `@testing-library/jest-dom`
- Focus tests on user behavior rather than implementation details
- Write self-documenting code with meaningful names
- Avoid unnecessary comments
