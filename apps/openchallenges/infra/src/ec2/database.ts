/* eslint-disable no-unused-vars */
import { DataAwsSsmParameter } from '@cdktf/provider-aws/lib/data-aws-ssm-parameter';
import { Instance } from '@cdktf/provider-aws/lib/instance';
import { NatGateway } from '@cdktf/provider-aws/lib/nat-gateway';
import { Construct } from 'constructs';
import { readFileSync } from 'fs';

export class Database extends Construct {
  instance: Instance;

  constructor(
    scope: Construct,
    id: string,
    subnetId: string,
    securityGroupId: string,
    natGateway: NatGateway,
  ) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    const ami = new DataAwsSsmParameter(this, 'ubuntu_1804_ami_id', {
      name: '/aws/service/canonical/ubuntu/server/18.04/stable/current/amd64/hvm/ebs-gp2/ami-id',
    }).value;

    this.instance = new Instance(this, id, {
      ami,
      instanceType: 't2.micro',
      vpcSecurityGroupIds: [securityGroupId],
      subnetId,
      privateIp: '10.70.32.253', // TODO replace by config param
      // privateIp: vars.databasePrivateIp,
      tags: { Name: `${nameTagPrefix}-database` },
      dependsOn: [natGateway],
      userData: readFileSync('./src/resources/scripts/database.sh', 'utf8'),
    });
  }
}
