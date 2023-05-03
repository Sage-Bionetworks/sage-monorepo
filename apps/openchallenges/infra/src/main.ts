/* eslint-disable no-unused-vars */
import { Construct } from 'constructs';
import { App, TerraformStack } from 'cdktf';
import { logger, Level } from './logger';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { Instance } from '@cdktf/provider-aws/lib/instance';

class MyStack extends TerraformStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    // eslint-disable-next-line no-new
    new AwsProvider(this, 'AWS', {
      region: 'us-east-1',
    });

    // eslint-disable-next-line
    const ec2Instance = new Instance(this, 'compute', {
      ami: 'ami-01456a894f71116f2',
      instanceType: 't2.micro',
    });
  }
}

logger.setLevel(Level.Debug);
logger.info('Welcome to OpenChallenges infra project');

const app = new App();
// eslint-disable-next-line no-new
new MyStack(app, 'openchallenges-infra');
app.synth();
