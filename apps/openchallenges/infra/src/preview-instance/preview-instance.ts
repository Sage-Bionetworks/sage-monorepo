import { Instance } from '@cdktf/provider-aws/lib/instance';
import { Construct } from 'constructs';
import { readFileSync } from 'fs';
import { PreviewInstanceConfig } from './preview-instance-config';

export class PreviewInstance extends Construct {
  instance: Instance;

  constructor(scope: Construct, id: string, config: PreviewInstanceConfig) {
    super(scope, id);

    this.instance = new Instance(this, id, {
      ami: config.ami,
      instanceType: config.instanceType,
      keyName: config.keyName,
      subnetId: config.subnetId,
      tags: { Name: `${config.tagPrefix}-preview-instance` },
      userData: readFileSync(
        './src/resources/scripts/preview-instance.sh',
        'utf8'
      ),
      vpcSecurityGroupIds: config.securityGroupIds,
      rootBlockDevice: {
        deleteOnTermination: true,
        encrypted: true,
        volumeSize: 20,
        volumeType: 'gp2',
      },
    });
  }
}
