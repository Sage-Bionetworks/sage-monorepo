// This script computes a sha256 hash of the yarn.lock file and compares it to a value on file. If
// the two hashes are different, the command `yarn install --frozen-lockfile` is executed.

'use strict';

const crypto = require('crypto');
const fs = require('fs');
const { spawn } = require("child_process");

const nodejsPackageFilePath = '/tmp/nodejs-packages.json';
const yarnLockFilePath = `${process.env.CHALLENGE_DIR}/yarn.lock`;

const installNodejsPackages = () => {
  console.log('📦 Installing Node.js packages');
  spawn('yarn', ['install', '--frozen-lockfile'], {stdio:'inherit'})
    .on('exit', function (error) {
      if (error) {
        console.log(`error: ${error.message}`);
        return;
      }
    });
}

const getExpectedYarnLockHash = (filePath) => {
  try {
    const jsonData = fs.readFileSync(filePath);
    const json = JSON.parse(jsonData);
    return json['hash'];
  } catch {
    return undefined;
  }
}

const getCurrentYarnLockHash = (filePath) => {
  try {
    const fileBuffer = fs.readFileSync(filePath);
    const hashSum = crypto.createHash('sha256');
    hashSum.update(fileBuffer);
    return hashSum.digest('hex');
  } catch {
    return undefined;
  }
}

console.log('📦 Checking Node.js packages');
const expectedHash = getExpectedYarnLockHash(nodejsPackageFilePath);
const currentHash = getCurrentYarnLockHash(yarnLockFilePath);

if (expectedHash !== currentHash) {
  installNodejsPackages();
  const newJson = { hash: currentHash };
  const newJsonData = JSON.stringify(newJson);
  fs.writeFileSync(nodejsPackageFilePath, newJsonData);
}