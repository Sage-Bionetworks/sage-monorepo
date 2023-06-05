/* eslint-disable no-unused-vars */
import { Instance } from '@cdktf/provider-aws/lib/instance';
import { Construct } from 'constructs';
import { readFileSync } from 'fs';
import { BastionConfig } from './bastion-config';
import { BastionInstanceProfile } from './bastion-instance-profile';

export class Bastion extends Construct {
  instance: Instance;
  instanceProfile: BastionInstanceProfile;

  constructor(scope: Construct, id: string, config: BastionConfig) {
    super(scope, id);

    this.instanceProfile = new BastionInstanceProfile(
      this,
      'bastion_instance_profile',
      config.tagPrefix
    );

    this.instance = new Instance(this, id, {
      ami: config.ami,
      instanceType: config.instanceType,
      keyName: config.keyName,
      privateIp: config.privateIp,
      subnetId: config.subnetId,
      tags: { Name: `${config.tagPrefix}-bastion` },
      userData: readFileSync('./src/resources/scripts/bastion.sh', 'utf8'),
      vpcSecurityGroupIds: [config.securityGroupId],
      iamInstanceProfile: this.instanceProfile.iamInstanceProfile.name,
    });
  }
}
