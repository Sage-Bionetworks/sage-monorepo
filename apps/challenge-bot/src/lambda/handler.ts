import {
  createLambdaFunction,
  createProbot,
} from '@probot/adapter-aws-lambda-serverless';
import { buildChallengeApp } from '../build-challenge-app';
import serverless from 'serverless-http';
import { Server } from '../server/server';

module.exports.webhooks = createLambdaFunction(buildChallengeApp, {
  probot: createProbot(),
});

module.exports.api = serverless(new Server().expressApp);
