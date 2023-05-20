import { Instance } from '@cdktf/provider-aws/lib/instance';
import { Construct } from 'constructs';
import { Ami, SageCostCenter } from '../constants';
import { NatGateway } from '@cdktf/provider-aws/lib/nat-gateway';
import { readFileSync } from 'fs';
import { Aspects } from 'cdktf/lib/aspect';
import { TagsAddingAspect } from '../tag/tags-adding-aspect';

export class SingleStackInstance extends Construct {
  instance: Instance;

  constructor(
    scope: Construct,
    id: string,
    subnetId: string,
    securityGroupId: string,
    natGateway: NatGateway
  ) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';
    const stackOwnerEmail = 'thomas.schaffter@sagebionetworks.org';

    this.instance = new Instance(this, id, {
      ami: Ami.UBUNTU_22_04_LTS,
      instanceType: 't3.xlarge',
      vpcSecurityGroupIds: [securityGroupId],
      subnetId,
      privateIp: '10.0.32.253', // TODO replace by config param
      // privateIp: vars.databasePrivateIp,
      tags: { Name: `${nameTagPrefix}-single-stack-instance` },
      dependsOn: [natGateway],
      userData: readFileSync(
        './src/resources/scripts/single-stack-instance.sh',
        'utf8'
      ),
    });

    // Add tags to every resource defined within this stack.
    Aspects.of(this).add(
      new TagsAddingAspect({
        OwnerEmail: stackOwnerEmail,
        CostCenter: SageCostCenter.ITCR,
      })
    );
  }
}
