// This script checks if the `pnpm-lock.yaml` file has changed since the last git move. If it has
// changed, then the command `pnpm install --frozen-lockfile` is executed.

'use strict';

const { spawn } = require('child_process');
const { getGitDiffFiles } = require('./git-util');

const gitHookName = process.argv[2];

const installNodejsDependencies = () => {
  spawn('pnpm', ['install', '--frozen-lockfile'], { stdio: 'inherit' }).on(
    'exit',
    function (error) {
      if (error) {
        console.log(`error: ${error.message}`);
        return;
      }
    },
  );
};

console.log('✨ Preparing Node.js environments');
getGitDiffFiles().then((changedFiles) => {
  if (changedFiles.includes('pnpm-lock.yaml')) {
    if (gitHookName === 'post-merge') {
      console.log(
        '🙏 If you have any issues with `pnpm`, `nx` or any other Node.js dependencies after a `git merge`, please open an Issue in GitHub.',
      );
    }
    installNodejsDependencies();
  }
});
