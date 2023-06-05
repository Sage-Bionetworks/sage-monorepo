/* eslint-disable no-unused-vars */
/* eslint-disable no-new */
import { App, CloudBackend, NamedCloudWorkspace } from 'cdktf';
import { logger, Level } from './logger';

import { OpenChallengesPreviewStack } from './openchallenges-preview-stack';
import { OpenChallengesStack } from './openchallenges-stack';

logger.setLevel(Level.Debug);
logger.info('Welcome to the deployment of the OpenChallenges stacks.');

const app = new App();
const openChallengesStack = new OpenChallengesStack(app, 'openchallenges');
const openChallengesPreviewStack = new OpenChallengesPreviewStack(
  app,
  'openchallenges-preview'
);

new CloudBackend(openChallengesStack, {
  hostname: 'app.terraform.io',
  organization: 'sagebionetworks',
  workspaces: new NamedCloudWorkspace('openchallenges-test'),
});

new CloudBackend(openChallengesPreviewStack, {
  hostname: 'app.terraform.io',
  organization: 'sagebionetworks',
  workspaces: new NamedCloudWorkspace('openchallenges-preview'),
});

app.synth();
