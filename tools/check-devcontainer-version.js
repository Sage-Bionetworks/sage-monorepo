'use strict';

const fs = require('fs');

const data = fs.readFileSync('.devcontainer/devcontainer.json');
const json = JSON.parse(data);

const image = json['image'];
if (image === undefined) {
  console.error('The property `image` is missing.');
  process.exit(0);
}
if (!image.includes(':')) {
  console.error('The value of `image` must include the image tag.');
  process.exit(0);
}

const expectedDevcontainerVersion = image.split(':')[1];
const currentDevcontainerVersion = process.env.DEVCONTAINER_VERSION;

if (currentDevcontainerVersion === undefined) {
  console.error('Unable to read the environment variable `DEVCONTAINER_VERSION`.');
  process.exit(0);
}

if (expectedDevcontainerVersion !== currentDevcontainerVersion) {
  console.info('🐋 The dev container has changed. Please rebuild it.');
  // console.debug(`Expected dev container version: ${expectedDevcontainerVersion}`);
  // console.debug(`Current dev container version: ${currentDevcontainerVersion}`);
}
