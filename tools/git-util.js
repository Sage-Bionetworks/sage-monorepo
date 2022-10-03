const { promisify } = require('util');
const exec = promisify(require('child_process').exec);

// Returns the files tracked with git that have changed since the last git move.
const getGitDiffFiles = async () => {
  const changedFiles = await exec('git diff --name-only HEAD@{1} HEAD');
  return changedFiles.stdout.trim().split(/\n/);
}

// Returns the value of the git property `user.name`.
const getGitUserName = async () => {
  const userName = await exec('git config --global user.name');
  return userName.stdout.trim();
}

module.exports = {
  getGitDiffFiles,
  getGitUserName
};