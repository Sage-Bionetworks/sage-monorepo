// This script checks if the `yarn.lock` file has changed since the last git move. If it has
// changed, then the command `yarn install --frozen-lockfile` is executed.

'use strict';

const { spawn } = require("child_process");
const { getGitDiffFiles } = require('./git-util');

const gitHookName = process.argv[2];

const installNodejsPackages = () => {
  spawn('yarn', ['install', '--immutable'], {stdio:'inherit'})
    .on('exit', function (error) {
      if (error) {
        console.log(`error: ${error.message}`);
        return;
      }
    });
}

console.log('✨ Preparing Node.js packages');
getGitDiffFiles().then((changedFiles) => {
  if (changedFiles.includes('yarn.lock')) {
    if (gitHookName === 'post-merge') {
      console.log('🙏 If you have any issues with `yarn`, `nx` or any other Node.js packages after a `git merge`, please open an Issue in GitHub.');
    }
    installNodejsPackages();
  }
});