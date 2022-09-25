import {
  createLambdaFunction,
  createProbot,
} from '@probot/adapter-aws-lambda-serverless';
import app from '../app';

module.exports.webhooks = createLambdaFunction(app, {
  probot: createProbot(),
});
