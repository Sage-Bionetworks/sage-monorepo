# Shared Storybook

A unified Storybook instance that aggregates stories from all projects in the monorepo using Storybook Composition.

## Architecture

This storybook uses **Storybook Composition** to provide a single unified navigation experience while serving stories from multiple independent storybooks:

- **Main Storybook** (port 4400): The composition host that provides unified navigation
- **Agora Storybook** (port 4401): Child storybook for Agora domain stories
- **Explorers Storybook** (port 4402): Child storybook for Explorers domain stories

## Usage Guide

| Scenario                                  | Use                                                              | Why                                                                          |
| ----------------------------------------- | ---------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| **Active development on a single domain** | Domain-specific storybook (`nx storybook agora-storybook`)       | Faster startup and hot-reload, no iframe overhead, focused on your work      |
| **Cross-domain component review**         | Composite dev mode (`nx storybook storybook`)                    | Unified navigation to browse all stories, but slower due to iframe rendering |
| **Production documentation**              | Static composite build (`nx build-static-composition storybook`) | Single deployable artifact with all stories, optimized for viewing           |
| **Testing production build locally**      | Static server (`nx static-storybook storybook`)                  | Verify production behavior without deployment                                |

## Development

### Running the Composition (Recommended)

```bash
nx storybook storybook
```

This runs the composition development server via `start-composition.sh`, which:

1. Starts all three storybook instances (main, agora, explorers)
2. Provides unified navigation at http://localhost:4400
3. Hot-reloads changes from all domains

### Running Standalone (Single Storybook Only)

```bash
nx storybook-dev storybook
```

This runs only the main storybook without composition. Useful for debugging the main storybook configuration in isolation.

## Building Static Storybook

### Build for Production

```bash
nx build-static-composition storybook
```

This builds all three storybooks and assembles them into a single static site:

1. Builds the main storybook to `dist/storybook/storybook/`
2. Builds agora storybook to `dist/storybook/storybook/agora/`
3. Builds explorers storybook to `dist/storybook/storybook/explorers/`
4. Copies theme assets from child storybooks to the main output

Output will be in `dist/storybook/storybook/`

### Serve the Static Build

```bash
nx static-storybook storybook
```

Builds (if needed) and serves the static composition at http://localhost:8080

This is useful for testing the production build locally.
