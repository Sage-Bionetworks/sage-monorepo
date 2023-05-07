/* eslint-disable no-new */
import { Construct } from 'constructs';
import {
  App,
  Aspects,
  CloudBackend,
  NamedCloudWorkspace,
  TerraformOutput,
} from 'cdktf';
import { logger, Level } from './logger';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { Instance } from '@cdktf/provider-aws/lib/instance';
import { KeyPair } from '@cdktf/provider-aws/lib/key-pair';
import * as os from 'os';
import * as fs from 'fs';
import { TagsAddingAspect } from './aspect/tags-adding-aspect';
import {
  Ami,
  AmazonEc2InstanceType,
  SageCostCenter,
  AmazonRegion,
} from './constants';
import { SageStack } from './stack/sage-stack';
import { S3Bucket } from '@cdktf/provider-aws/lib/s3-bucket';

class OpenChallengesStack extends SageStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    const keyPath = `${os.homedir()}/.ssh/openchallenges-ec2.pub`;
    const publicKey = fs.readFileSync(keyPath, 'utf-8');
    const keyName = 'openchallenges-ec2-key';
    const stackOwnerEmail = 'thomas.schaffter@sagebionetworks.org';

    new AwsProvider(this, 'AWS', {
      region: AmazonRegion.US_EAST_1,
    });

    const keyPair = new KeyPair(this, 'keypair', {
      publicKey,
      keyName,
    });

    const ec2Instance = new Instance(this, 'compute', {
      ami: Ami.UBUNTU_22_04_LTS,
      instanceType: AmazonEc2InstanceType.T2_MICRO,
      keyName: keyPair.keyName,
    });

    new TerraformOutput(this, 'public_ip', {
      value: ec2Instance.publicIp,
    });

    // Add tags to every resource defined within this stack.
    Aspects.of(this).add(
      new TagsAddingAspect({
        OwnerEmail: stackOwnerEmail,
        CostCenter: SageCostCenter.ITCR,
      })
    );

    new S3Bucket(this, 'bucket', {
      bucket: 'my2PrefixDemo',
    });
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
