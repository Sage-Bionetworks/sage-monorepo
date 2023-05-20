/* eslint-disable no-unused-vars */
/* eslint-disable no-new */
import { Construct } from 'constructs';
import { SageStack } from './sage-stack';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { AmazonRegion, Ami, SageCostCenter } from './constants';
import { NetworkConfig } from './network/network-config';
import { Network } from './network/network';
import { SingleStackInstance } from './ec2/single-stack-instance';
import { SecurityGroups } from './security-group/security-groups';
import { Aspects, TerraformOutput } from 'cdktf';
import { BastionConfig } from './bastion/bastion-config';
import { Bastion } from './bastion/bastion';
import { TagsAddingAspect } from './tag/tags-adding-aspect';
import * as os from 'os';
import * as fs from 'fs';
import { KeyPair } from '@cdktf/provider-aws/lib/key-pair';

export class OpenChallengesPreviewStack extends SageStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    const bastionKeyPath = `${os.homedir()}/.ssh/openchallenges-ec2.pub`;
    const bastionPublicKey = fs.readFileSync(bastionKeyPath, 'utf-8');
    const bastionKeyName = 'openchallenges-preview-bastion-key';
    const stackOwnerEmail = 'thomas.schaffter@sagebionetworks.org';

    new AwsProvider(this, 'AWS', {
      region: AmazonRegion.US_EAST_1,
    });

    const networkConfig = new NetworkConfig({
      defaultRegion: AmazonRegion.US_EAST_1,
      tagPrefix: 'openchallenges-preview',
      vpcCirdBlock: '10.0.0.0/16',
    });

    const network = new Network(this, 'network', networkConfig);

    const securityGroups = new SecurityGroups(
      this,
      'security_groups',
      network.vpc.id
    );

    const bastionKeyPair = new KeyPair(this, 'bastion_keypair', {
      publicKey: bastionPublicKey,
      keyName: bastionKeyName,
    });

    const bastionConfig = new BastionConfig({
      ami: Ami.UBUNTU_22_04_LTS,
      defaultRegion: AmazonRegion.US_EAST_1,
      instanceType: 't2.micro',
      keyName: bastionKeyName,
      securityGroupId: securityGroups.bastionSg.id,
      subnetId: network.publicSubnets[0].id,
      tagPrefix: 'openchallenges-preview',
    });

    const bastion = new Bastion(this, 'bastion', bastionConfig);

    // Add tags to every resource defined within this stack.
    Aspects.of(this).add(
      new TagsAddingAspect({
        OwnerEmail: stackOwnerEmail,
        CostCenter: SageCostCenter.ITCR,
      })
    );

    // Outputs
    new TerraformOutput(this, 'public_ip', {
      value: bastion.instance.publicIp,
    });
  }
}
