/* eslint-disable no-new */
import { Construct } from 'constructs';
import { App, TerraformOutput, TerraformStack } from 'cdktf';
import { logger, Level } from './logger';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { Instance } from '@cdktf/provider-aws/lib/instance';
import { KeyPair } from '@cdktf/provider-aws/lib/key-pair';
import * as os from 'os';
import * as fs from 'fs';

class MyStack extends TerraformStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    const keyPath = `${os.homedir()}/.ssh/openchallenges-ec2.pub`;
    const publicKey = fs.readFileSync(keyPath, 'utf-8');

    new AwsProvider(this, 'AWS', {
      region: 'us-east-1',
    });

    const keyPair = new KeyPair(this, 'keypair', {
      publicKey,
      keyName: 'openchallenges-ec2-key',
    });

    const ec2Instance = new Instance(this, 'compute', {
      ami: 'ami-0044130ca185d0880', // Ubuntu 22.04 LTS
      instanceType: 't2.micro',
      keyName: keyPair.keyName,
    });

    new TerraformOutput(this, 'public_ip', {
      value: ec2Instance.publicIp,
    });
  }
}

logger.setLevel(Level.Debug);
logger.info('Welcome to the deployment of the OpenChallenges stack.');

const app = new App();
new MyStack(app, 'openchallenges-stack');
app.synth();
