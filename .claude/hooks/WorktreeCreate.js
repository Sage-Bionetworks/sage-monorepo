#!/usr/bin/env node
// WorktreeCreate hook for Claude Code
//
// Handles worktree creation for `claude -w`:
// reuses an existing branch if found, otherwise creates a new one,
// copies gitignored files listed in .worktreeinclude into the worktree,
// then runs workspace-install in the background.
//
// Input (JSON on stdin): { "name": "<slug>", "cwd": "<project-root>", ... }
// Output (stdout):       Absolute path to the created worktree directory

const { execFileSync, spawnSync, spawn } = require('child_process');
const fs = require('fs');
const path = require('path');

const input = JSON.parse(fs.readFileSync(process.stdin.fd, 'utf8'));
const { name, cwd } = input;
const worktreePath = path.join(cwd, '.claude', 'worktrees', name);

// If the worktree directory already exists, reuse it as-is
if (fs.existsSync(worktreePath)) {
  process.stdout.write(worktreePath + '\n');
  process.exit(0);
}

// Reuse existing branch if it exists, otherwise create a new one
let branchExists = false;
try {
  execFileSync('git', ['-C', cwd, 'show-ref', '--verify', '--quiet', `refs/heads/${name}`]);
  branchExists = true;
} catch {
  // branch does not exist yet
}

if (branchExists) {
  execFileSync('git', ['-C', cwd, 'worktree', 'add', worktreePath, name], {
    stdio: ['ignore', 'ignore', 'inherit'],
  });
} else {
  execFileSync('git', ['-C', cwd, 'worktree', 'add', '-b', name, worktreePath, 'HEAD'], {
    stdio: ['ignore', 'ignore', 'inherit'],
  });
}

// Copy gitignored files listed in .worktreeinclude into the new worktree.
// Uses git ls-files to enumerate matches (supports full gitignore pattern syntax),
// then pipes through tar for an atomic bulk copy.
const worktreeInclude = path.join(cwd, '.worktreeinclude');
if (fs.existsSync(worktreeInclude)) {
  const ls = spawnSync(
    'git',
    ['ls-files', '--others', '--ignored', '--exclude-from=.worktreeinclude', '-z'],
    { cwd, encoding: 'buffer' },
  );
  if (ls.stdout && ls.stdout.length > 0) {
    const tar = spawnSync('tar', ['-C', cwd, '--null', '-T', '-', '-cf', '-'], {
      input: ls.stdout,
      encoding: 'buffer',
    });
    if (tar.stderr && tar.stderr.length > 0) process.stderr.write(tar.stderr);
    if (tar.stdout && tar.stdout.length > 0) {
      const extract = spawnSync('tar', ['-C', worktreePath, '-xf', '-'], {
        input: tar.stdout,
        encoding: 'buffer',
      });
      if (extract.stderr && extract.stderr.length > 0) process.stderr.write(extract.stderr);
    }
  }
  if (ls.stderr && ls.stderr.length > 0) process.stderr.write(ls.stderr);
}

// Run workspace-install in the background; SessionStart hook will notify the user.
// Output is logged to .workspace-install.log — tail it to follow progress.
//
// We wrap the install in a Node.js child process so it can delete the sentinel
// file when the install finishes. Spawning bash directly with child.unref() causes
// WorktreeCreate to exit before the install completes, so child.on('close') never fires.
const pidFile = path.join(worktreePath, '.workspace-installing');
const logFile = fs.openSync(path.join(worktreePath, '.workspace-install.log'), 'w');
const installWrapper = [
  `const {spawnSync} = require('child_process'), fs = require('fs'), path = require('path');`,
  `const devEnvPath = path.join(${JSON.stringify(cwd)}, 'dev-env.sh');`,
  `spawnSync('bash', ['-c', 'shopt -s expand_aliases && source "$1" && workspace-install', '--', devEnvPath], {cwd: ${JSON.stringify(worktreePath)}, stdio: 'inherit'});`,
  `fs.rmSync(${JSON.stringify(pidFile)}, {force: true});`,
].join(' ');
const child = spawn(process.execPath, ['-e', installWrapper], {
  cwd: worktreePath,
  detached: true,
  windowsHide: true,
  stdio: ['ignore', logFile, logFile],
});
child.unref();
if (child.pid != null) {
  fs.writeFileSync(pidFile, String(child.pid));
}

process.stdout.write(worktreePath + '\n');
