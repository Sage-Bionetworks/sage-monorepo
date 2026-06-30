#!/usr/bin/env node
// SessionStart hook for Claude Code
//
// Notifies the user if workspace-install is still running in the background
// after a worktree session starts.
//
// Uses plain Node.js (no tsx) so it works even before workspace-install completes.

const fs = require('fs');

if (fs.existsSync('.workspace-installing')) {
  process.stdout.write(
    JSON.stringify({
      systemMessage:
        'workspace-install is running in the background. Run: tail -f .workspace-install.log to follow progress.',
    }) + '\n',
  );
}
