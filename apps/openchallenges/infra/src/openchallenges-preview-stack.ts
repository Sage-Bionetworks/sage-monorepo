/* eslint-disable no-unused-vars */
/* eslint-disable no-new */
import { Construct } from 'constructs';
import { SageStack } from './sage-stack';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { AmazonRegion, Ami, SageCostCenter } from './constants';
import { NetworkConfig } from './network/network-config';
import { Network } from './network/network';
import { SecurityGroups } from './security-group/security-groups';
import { Aspects, TerraformOutput, TerraformVariable } from 'cdktf';
import { BastionConfig } from './bastion/bastion-config';
import { Bastion } from './bastion/bastion';
import { TagsAddingAspect } from './tag/tags-adding-aspect';
import * as os from 'os';
import * as fs from 'fs';
import { KeyPair } from '@cdktf/provider-aws/lib/key-pair';
import { PreviewInstanceConfig } from './preview-instance/preview-instance-config';
import { PreviewInstance } from './preview-instance/preview-instance';
import { PreviewInstanceAlb } from './preview-instance-alb/preview-instance-alb';
import { OpenChallengesDevDns } from './dns/openchallenges-dev-dns';

export class OpenChallengesPreviewStack extends SageStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    const bastionKeyPath = `${os.homedir()}/.ssh/openchallenges-ec2.pub`;
    const bastionPublicKey = fs.readFileSync(bastionKeyPath, 'utf-8');
    const bastionKeyName = 'openchallenges-preview-bastion-key';
    const stackOwnerEmail = 'thomas.schaffter@sagebionetworks.org';
    const bastionPrivateIp = '10.70.2.172';

    // Inputs
    const hello = new TerraformVariable(this, 'hello', {
      type: 'string',
      description: 'Example of environment variable to add to the bastion',
      sensitive: true,
    });

    // The AWS provider
    new AwsProvider(this, 'AWS', {
      region: AmazonRegion.US_EAST_1,
    });

    // The network
    const networkConfig = new NetworkConfig({
      defaultRegion: AmazonRegion.US_EAST_1,
      tagPrefix: 'openchallenges-preview',
      vpcCirdBlock: '10.70.0.0/16',
    });

    const network = new Network(this, 'network', networkConfig);

    // The security groups
    const securityGroups = new SecurityGroups(
      this,
      'security_groups',
      network.vpc.id,
    );

    // The bastion
    const bastionKeyPair = new KeyPair(this, 'bastion_keypair', {
      publicKey: bastionPublicKey,
      keyName: bastionKeyName,
    });

    const bastionConfig = new BastionConfig({
      ami: Ami.UBUNTU_22_04_LTS,
      defaultRegion: AmazonRegion.US_EAST_1,
      hello: hello.value,
      instanceType: 't2.micro',
      keyName: bastionKeyName,
      privateIp: bastionPrivateIp,
      securityGroupId: securityGroups.bastionSg.id,
      subnetId: network.publicSubnets[0].id,
      tagPrefix: 'openchallenges-preview',
    });

    const bastion = new Bastion(this, 'bastion', bastionConfig);

    // The preview instance
    const previewInstanceConfig = new PreviewInstanceConfig({
      ami: Ami.UBUNTU_22_04_LTS,
      defaultRegion: AmazonRegion.US_EAST_1,
      instanceType: 't3.xlarge',
      keyName: bastionKeyName, // TODO Set unique key
      securityGroupIds: [
        securityGroups.previewInstanceSg.id,
        securityGroups.sshFromBastionSg.id,
      ],
      subnetId: network.privateSubnets[0].id,
      tagPrefix: 'openchallenges-preview',
    });

    const previewInstance = new PreviewInstance(
      this,
      'preview_instance',
      previewInstanceConfig,
    );

    // The preview instance ALB
    const previewInstanceAlb = new PreviewInstanceAlb(
      this,
      'preview_instance_alb',
      network.publicSubnets,
      securityGroups.previewInstanceAlbSg.id,
      network.vpc.id,
      previewInstance.instance.privateIp,
    );

    // The DNS configuration
    const dns = new OpenChallengesDevDns(this, 'dns', previewInstanceAlb.lb);

    // Add tags to every resource defined within this stack.
    Aspects.of(this).add(
      new TagsAddingAspect({
        OwnerEmail: stackOwnerEmail,
        CostCenter: SageCostCenter.ITCR,
      }),
    );

    // Outputs
    new TerraformOutput(this, 'public_subnet_0_cidr_block', {
      value: network.publicSubnets[0].cidrBlock,
    });
    new TerraformOutput(this, 'public_subnet_1_cidr_block', {
      value: network.publicSubnets[1].cidrBlock,
    });
    new TerraformOutput(this, 'private_subnet_0_cidr_block', {
      value: network.privateSubnets[0].cidrBlock,
    });
    new TerraformOutput(this, 'private_subnet_1_cidr_block', {
      value: network.privateSubnets[1].cidrBlock,
    });
    new TerraformOutput(this, 'bastion_id', {
      value: bastion.instance.id,
    });
    new TerraformOutput(this, 'preview_instance_id', {
      value: previewInstance.instance.id,
    });
    new TerraformOutput(this, 'preview_instance_private_ip', {
      value: previewInstance.instance.privateIp,
    });
    new TerraformOutput(this, 'preview_instance_endpoint', {
      value: previewInstanceAlb.lb.dnsName,
      description:
        'DNS name (endpoint) of the AWS ALB for the preview instance',
    });
  }
}
