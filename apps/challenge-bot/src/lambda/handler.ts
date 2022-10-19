import {
  createLambdaFunction,
  createProbot,
} from '@probot/adapter-aws-lambda-serverless';
import { challengeBot } from '../index';

module.exports.webhooks = createLambdaFunction(challengeBot, {
  probot: createProbot(),
});
