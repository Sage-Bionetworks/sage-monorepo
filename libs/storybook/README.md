# Shared Storybook

A unified Storybook instance that aggregates stories from all projects in the monorepo.

## Running Storybook

```bash
nx storybook storybook
```

This will start the Storybook dev server on http://localhost:4400

## Building Storybook

```bash
nx build-storybook storybook
```

Output will be in `dist/storybook/storybook/`

## Adding Stories from a New Library

When you add Storybook stories to a library that isn't already tracked by this shared Storybook:

**IMPORTANT:** You must add the library's project name to the `implicitDependencies` array in `libs/storybook/project.json`.

This ensures Nx properly invalidates the Storybook cache when that library changes.

### Example

If you add stories to `agora-teams`:

1. Add story files in `libs/agora/teams/src/lib/**/*.stories.ts`
2. Update `libs/storybook/project.json`:
   ```json
   "implicitDependencies": [
     "agora-about",
     "agora-teams",  // <-- Add this
     ...
   ]
   ```

### Why is this needed?

Nx cannot automatically detect the dependencies from glob patterns in `.storybook/main.ts`, so we must explicitly declare them. Without this, Nx's cache may serve stale Storybook builds when your library changes.

## Theme Switching

This Storybook supports switching between different project themes (Explorers, Agora, Model-AD) via the theme dropdown in the Storybook toolbar.
