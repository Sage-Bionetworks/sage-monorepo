'use strict';

// import { getGitDiffFiles } from './git-util';
const { getGitDiffFiles } = require('./git-util');

// // const crypto = require('crypto');
// // const fs = require('fs');
// const { promisify } = require('util');
// const exec = promisify(require('child_process').exec)



// const getUserName = async () => {
//   const userName = await exec('git config --global user.name');
//   return userName.stdout.trim();
// }

// // Returns the files tracked with git that have changed since the last git move.
// const getGitDiffFiles = async () => {
//   const changedFiles = await exec('git diff --name-only HEAD@{1} HEAD');
//   return changedFiles.stdout.trim().split(/\n/);
// }


// getUserName().then((name) => console.log(name));
getGitDiffFiles().then((changedFiles) => console.log(changedFiles));