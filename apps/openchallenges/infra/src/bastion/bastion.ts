/* eslint-disable no-unused-vars */
import { Instance } from '@cdktf/provider-aws/lib/instance';
import { AssetType, Fn, TerraformAsset } from 'cdktf';
import { Construct } from 'constructs';
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
      config.tagPrefix,
    );

    const instanceScript = new TerraformAsset(this, 'bastion_instance_script', {
      path: `${process.cwd()}/src/resources/scripts/bastion.sh`,
      type: AssetType.FILE,
    });

    this.instance = new Instance(this, 'bastion_instance', {
      ami: config.ami,
      instanceType: config.instanceType,
      keyName: config.keyName,
      privateIp: config.privateIp,
      subnetId: config.subnetId,
      tags: { Name: `${config.tagPrefix}-bastion` },
      userData: Fn.templatefile(instanceScript.path, {
        hello: config.hello,
      }),
      userDataReplaceOnChange: true,
      vpcSecurityGroupIds: [config.securityGroupId],
      iamInstanceProfile: this.instanceProfile.iamInstanceProfile.name,
    });
  }
}
