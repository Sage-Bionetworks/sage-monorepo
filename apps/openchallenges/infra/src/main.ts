import { Construct } from 'constructs';
import { App, TerraformStack } from 'cdktf';
import { logger, Level } from './logger';

class MyStack extends TerraformStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    // define resources here
  }
}

logger.setLevel(Level.Debug);
logger.info('Welcome to OpenChallenges infra project');

const app = new App();
// eslint-disable-next-line no-new
new MyStack(app, 'openchallenges-infra');
app.synth();
