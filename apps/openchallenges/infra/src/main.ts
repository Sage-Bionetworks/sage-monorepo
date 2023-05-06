/* eslint-disable no-new */
import { Construct } from 'constructs';
import {
  App,
  Aspects,
  CloudBackend,
  NamedCloudWorkspace,
  TerraformOutput,
  TerraformStack,
} from 'cdktf';
import { logger, Level } from './logger';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { Instance } from '@cdktf/provider-aws/lib/instance';
import { KeyPair } from '@cdktf/provider-aws/lib/key-pair';
import * as os from 'os';
import * as fs from 'fs';
import { TagsAddingAspect } from './aspect/tags-adding-aspect';

class OpenChallengesStack extends TerraformStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    const keyPath = `${os.homedir()}/.ssh/openchallenges-ec2.pub`;
    const publicKey = fs.readFileSync(keyPath, 'utf-8');
    const keyName = 'openchallenges-ec2-key';

    new AwsProvider(this, 'AWS', {
      region: 'us-east-1',
      // profile: 'cdktf',
    });

    const keyPair = new KeyPair(this, 'keypair', {
      publicKey,
      keyName,
    });

    const ec2Instance = new Instance(this, 'compute', {
      ami: 'ami-0044130ca185d0880', // Ubuntu 22.04 LTS
      instanceType: 't2.micro',
      keyName: keyPair.keyName,
    });

    new TerraformOutput(this, 'public_ip', {
      value: ec2Instance.publicIp,
    });

    // Add tags to every resource defined within this stack.
    Aspects.of(this).add(
      new TagsAddingAspect({
        OwnerEmail: 'thomas.schaffter@sagebionetworks.org',
        Department: 'CNB',
        Project: 'challenge',
        CostCenter: 'NIH-ITCR / 101600',
      })
    );
  }
}

logger.setLevel(Level.Debug);
logger.info('Welcome to the deployment of the OpenChallenges stack.');

const app = new App();
const stack = new OpenChallengesStack(app, 'openchallenges-stack');

new CloudBackend(stack, {
  hostname: 'app.terraform.io',
  organization: 'sagebionetworks',
  workspaces: new NamedCloudWorkspace('openchallenges-test'),
});

app.synth();
