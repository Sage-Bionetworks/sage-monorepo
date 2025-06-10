# Copilot Instructions for Sage Monorepo

## Project Overview

This is a TypeScript-based monorepo for Sage Bionetworks projects. It contains multiple applications and libraries related to scientific research, data visualization, and biomedical informatics. The codebase uses Angular, React, and other modern web technologies.

## Monorepo Structure

- **apps/**: Application projects (agora, model-ad, openchallenges, etc.)
- **libs/**: Library projects (shared code, components, services)
- **tools/**: Utility scripts and tools for the monorepo
- **docs/**: Documentation

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

## Build and Test Commands

- Run tests: `nx test [project-name]`
- Run lint: `nx lint [project-name]`

## Angular Development Standards

When working with Angular code:

- Read [Angular LLM documentation](instructions/angular/llms.md). Use the provided links if you need to look up Angular concepts or APIs.
- Always use standalone components over NgModules
- Use signals for reactive state management
- Implement control flow with `@if`, `@for` instead of `*ngIf`, `*ngFor`
- Write tests with `@testing-library/angular` and `@testing-library/jest-dom`
- Prefer tests that focus on what the user can see and do. Avoid testing how the component is implemented internally
- Your code should be self-documenting. Avoid unnecessary comments. Use meaningful variable and function names.
