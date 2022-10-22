import {
  createLambdaFunction,
  createProbot,
} from '@probot/adapter-aws-lambda-serverless';
import { buildChallengeApp } from '../build-challenge-app';

module.exports.webhooks = createLambdaFunction(buildChallengeApp, {
  probot: createProbot(),
});
