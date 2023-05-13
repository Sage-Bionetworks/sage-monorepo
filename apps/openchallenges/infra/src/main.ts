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
import { SecurityGroup } from '@cdktf/provider-aws/lib/security-group';
// import { SecurityGroup } from '@cdktf/provider-aws/lib/security-group';
import * as os from 'os';
import * as fs from 'fs';
import { TagsAddingAspect } from './tag/tags-adding-aspect';
import {
  Ami,
  AmazonEc2InstanceType,
  SageCostCenter,
  AmazonRegion,
} from './constants';
import { SageStack } from './stack/sage-stack';
import { S3Bucket } from '@cdktf/provider-aws/lib/s3-bucket';
import { NetworkConfig } from './network/network-config';
import { Network } from './network/network';

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

    const networkConfig = new NetworkConfig({
      defaultRegion: AmazonRegion.US_EAST_1,
      tagPrefix: 'openchallenges',
      vpcCirdBlock: '10.0.0.0/16',
    });

    // The AWS VPC
    const network = new Network(this, 'network', networkConfig);

    new TerraformOutput(this, 'vpc_id', {
      value: network.vpc.id,
    });

    const ports = [22, 80, 443];

    const securityGroup = new SecurityGroup(this, 'security_group', {
      name: 'openchallenges-sg',
      vpcId: network.vpc.id,
      egress: [
        {
          fromPort: 0,
          toPort: 0,
          cidrBlocks: ['0.0.0.0/0'],
          protocol: '-1',
        },
      ],
      ingress: ports.map((port) => ({
        fromPort: port,
        toPort: port,
        // TODO Do not use this in production, should be limited to specific IPs or disable (ssm?).
        cidrBlocks: ['0.0.0.0/0'],
        protocol: '-1',
      })),
    });

    const ec2Instance = new Instance(this, 'compute', {
      ami: Ami.UBUNTU_22_04_LTS,
      instanceType: AmazonEc2InstanceType.T2_MICRO,
      // subnetId: publicSubnetA.id,
      vpcSecurityGroupIds: [securityGroup.id],
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
      bucket: 'myPrefixDemo',
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
