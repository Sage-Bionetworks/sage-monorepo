#!/usr/bin/env node
// WorktreeRemove hook for Claude Code
//
// Handles cleanup when a worktree session ends.
// Since WorktreeCreate replaces the default git behavior,
// we need this hook to properly run git worktree remove.
//
// Input (JSON on stdin): { "worktree_path": "<absolute-path>", ... }

const { execFileSync, spawn } = require('child_process');
const fs = require('fs');
const path = require('path');

const input = JSON.parse(fs.readFileSync(process.stdin.fd, 'utf8'));
const { worktree_path: worktreePath } = input;

// Kill any in-progress workspace-install before removing the worktree
const pidFile = path.join(worktreePath, '.workspace-installing');
if (fs.existsSync(pidFile)) {
  const pid = parseInt(fs.readFileSync(pidFile, 'utf8').trim(), 10);
  if (!isNaN(pid)) {
    try {
      // On Unix, negative PID kills the entire process group (pnpm + children).
      // On Windows, only the pnpm process is killed; orphaned children will exit when the worktree is deleted.
      process.kill(process.platform === 'win32' ? pid : -pid, 'SIGKILL');
    } catch {
      // process may have already exited
    }
  }
}

// Rename the entire worktree directory atomically so git worktree prune can clean up
// the stale metadata instantly, then delete the renamed directory in the background.
// This makes the hook return in milliseconds rather than waiting on rm -rf.
const tempPath = path.join(
  path.dirname(worktreePath),
  `.removing_${path.basename(worktreePath)}_${Date.now()}`,
);

try {
  fs.renameSync(worktreePath, tempPath);
  execFileSync('git', ['worktree', 'prune'], { stdio: 'ignore' });
  const child = spawn(
    process.platform === 'win32' ? 'cmd.exe' : 'rm',
    process.platform === 'win32' ? ['/c', 'rd', '/s', '/q', tempPath] : ['-rf', tempPath],
    { detached: true, stdio: 'ignore' },
  );
  child.unref();
} catch {
  // rename failed (e.g. cross-device) — fall back to synchronous removal
  try {
    execFileSync('git', ['worktree', 'remove', worktreePath, '--force'], {
      stdio: 'ignore',
    });
  } catch {
    /* ignore */
  }
  try {
    execFileSync('git', ['worktree', 'prune'], { stdio: 'ignore' });
  } catch {
    /* ignore */
  }
  try {
    fs.rmSync(worktreePath, { recursive: true, force: true });
  } catch {
    /* ignore */
  }
}
