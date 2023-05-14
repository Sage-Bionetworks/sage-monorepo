/* eslint-disable no-unused-vars */
import { DataAwsSsmParameter } from '@cdktf/provider-aws/lib/data-aws-ssm-parameter';
import { Construct } from 'constructs';

export class Database extends Construct {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    const ami = new DataAwsSsmParameter(this, 'ubuntu_1804_ami_id', {
      name: '/aws/service/canonical/ubuntu/server/18.04/stable/current/amd64/hvm/ebs-gp2/ami-id',
    }).value;
  }
}
