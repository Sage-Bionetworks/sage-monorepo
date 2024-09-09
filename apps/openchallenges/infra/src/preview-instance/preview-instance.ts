import { Instance } from '@cdktf/provider-aws/lib/instance';
import { Construct } from 'constructs';
import { readFileSync } from 'fs';
import { PreviewInstanceConfig } from './preview-instance-config';
import { PreviewInstanceInstanceProfile } from './preview-instance-instance-profile';

export class PreviewInstance extends Construct {
  instance: Instance;
  instanceProfile: PreviewInstanceInstanceProfile;

  constructor(scope: Construct, id: string, config: PreviewInstanceConfig) {
    super(scope, id);

    this.instanceProfile = new PreviewInstanceInstanceProfile(
      this,
      'preview_instance_instance_profile',
      config.tagPrefix,
    );

    this.instance = new Instance(this, 'preview_instance_instance', {
      ami: config.ami,
      instanceType: config.instanceType,
      keyName: config.keyName,
      subnetId: config.subnetId,
      tags: { Name: `${config.tagPrefix}-preview-instance` },
      userData: readFileSync('./src/resources/scripts/preview-instance.sh', 'utf8'),
      userDataReplaceOnChange: true,
      vpcSecurityGroupIds: config.securityGroupIds,
      iamInstanceProfile: this.instanceProfile.iamInstanceProfile.name,
      rootBlockDevice: {
        deleteOnTermination: true,
        encrypted: true,
        volumeSize: 50,
        volumeType: 'gp2',
      },
    });
  }
}
