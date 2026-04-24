---
paths:
  - .github/.devcontainer/**
  - .devcontainer/**
---

# Dev Container

Full documentation: [`docs/develop/maintenance/dev-container.md`](docs/develop/maintenance/dev-container.md)

## Two-PR workflow (critical)

Dev container changes always follow a **BUILD → ACTIVATE** split. Never collapse into one PR.

| Step                | Branch pattern                      | Files touched                                                                 |
| ------------------- | ----------------------------------- | ----------------------------------------------------------------------------- |
| 1 — BUILD & PUBLISH | `chore/devcontainer/build-<date>`   | `.github/.devcontainer/Dockerfile`, `.github/.devcontainer/devcontainer.json` |
| 2 — ACTIVATE        | `chore/devcontainer/activate-<sha>` | `.devcontainer/devcontainer.json`                                             |

There are **two different `devcontainer.json` files** with distinct roles:

- `.github/.devcontainer/devcontainer.json` — build recipe (used by CI to bake the image; never referenced by VS Code directly)
- `.devcontainer/devcontainer.json` — active definition (what VS Code opens; points to a published GHCR image tag)

## Adding tools

- New tools go in `.github/.devcontainer/Dockerfile`, not in `dev-env.sh`
- `dev-env.sh` is sourced on every terminal session — it is for environment configuration only
- Tools installed after the `USER $user` switch (as `ubuntu`) install to `~/.local/` — this works because no volume mount shadows `/home/ubuntu` in the devcontainer setup, and `~/.local/bin` is already on PATH (added by `pipx ensurepath`)
