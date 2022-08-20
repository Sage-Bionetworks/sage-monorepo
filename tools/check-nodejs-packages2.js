'use strict';

// const crypto = require('crypto');
// const fs = require('fs');
const { promisify } = require('util');
const exec = promisify(require('child_process').exec)



const getUserName = async () => {
  const userName = await exec('git config --global user.name');
  return userName.stdout.trim();
}

const getChangedFiles = async () => {
  const changedFiles = await exec('git diff --name-only HEAD@{1} HEAD');
  return changedFiles.stdout.trim();
}


getUserName().then((name) => console.log(name));
getChangedFiles().then((changedFiles) => console.log(changedFiles));