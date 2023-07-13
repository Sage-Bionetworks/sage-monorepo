import { Construct } from 'constructs';
import { Route53Record } from '@cdktf/provider-aws/lib/route53-record';
import { Alb } from '@cdktf/provider-aws/lib/alb';

export class OpenChallengesDevDns extends Construct {
  record: Route53Record;

  constructor(scope: Construct, id: string, lb: Alb) {
    super(scope, id);

    // Create an alias record that points to the ALB
    this.record = new Route53Record(this, 'record', {
      name: 'dev.openchallenges.io',
      zoneId: `Z08311133Q4NQKBJPU4ZX`,
      type: 'A',
      alias: {
        name: lb.dnsName,
        zoneId: lb.zoneId,
        evaluateTargetHealth: true,
      },
    });
  }
}
