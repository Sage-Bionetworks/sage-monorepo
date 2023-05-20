import { Instance } from '@cdktf/provider-aws/lib/instance';
import { Construct } from 'constructs';
import { readFileSync } from 'fs';
import { BastionConfig } from './bastion-config';

export class Bastion extends Construct {
  instance: Instance;

  constructor(scope: Construct, id: string, config: BastionConfig) {
    super(scope, id);

    this.instance = new Instance(this, id, {
      ami: config.ami,
      instanceType: config.instanceType,
      keyName: config.keyName,
      subnetId: config.subnetId,
      tags: { Name: `${config.tagPrefix}-bastion` },
      userData: readFileSync('./src/resources/scripts/bastion.sh', 'utf8'),
      vpcSecurityGroupIds: [config.securityGroupId],
    });
  }
}
