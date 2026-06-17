# Working with Git Worktrees

Claude worktrees are created inside `.claude/worktrees/` (e.g. via `claude -w my-branch`). This directory is listed in `.gitignore` to prevent the worktree contents from appearing as untracked files in the parent repo.

## Opening a worktree in VS Code

Because `.claude/worktrees` is gitignored, VS Code's file indexer excludes worktree files from Cmd+P and file search when viewed from the parent window. The correct workflow is to open the worktree as its own VS Code window:

```sh
code .claude/worktrees/my-branch
```

This opens a new window rooted at the worktree. Cmd+P, the Explorer, and file search all work normally against the worktree's files.

## Background install

When a worktree is created, `workspace-install` runs in the background to set up Node.js, Python, and other dependencies for the branch. A sentinel file `.workspace-installing` is written to the worktree root while it runs.

If you open a Claude session in the worktree before the install completes, you will see a notification:

```
workspace-install is running in the background. Run: tail -f .workspace-install.log to follow progress.
```

## Environment files

Files matching `apps/**/.env` and `apps/**/.env.*` are copied from the parent repo into the worktree automatically on creation, so projects like `model-ad-data` have their credentials available without any manual setup.
