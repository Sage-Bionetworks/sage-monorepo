import { Construct } from 'constructs';
import { Route53Zone } from '@cdktf/provider-aws/lib/route53-zone';
import { Route53Record } from '@cdktf/provider-aws/lib/route53-record';
import { Alb } from '@cdktf/provider-aws/lib/alb';
import { AcmCertificate } from '@cdktf/provider-aws/lib/acm-certificate';

export class Dns extends Construct {
  devZone: Route53Zone;
  record: Route53Record;
  cert: AcmCertificate;

  constructor(scope: Construct, id: string, lb: Alb) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    // TODO This resource must be managed outside of this stack when more than one stacks are
    // deployed to this account.
    this.devZone = new Route53Zone(this, 'dev_zone', {
      name: 'dev.openchallenges.io',
      tags: {
        Name: 'dev.openchallenges.io',
      },
    });

    // Create an alias record that points to the ALB
    this.record = new Route53Record(this, 'record', {
      name: 'preview.dev.openchallenges.io',
      zoneId: this.devZone.id,
      type: 'A',
      alias: {
        name: lb.dnsName,
        zoneId: lb.zoneId,
        evaluateTargetHealth: true,
      },
    });

    this.cert = new AcmCertificate(this, 'cert', {
      domainName: 'preview.openchallenges.io',
      validationMethod: 'DNS',
      validationOption: [
        {
          domainName: 'preview.openchallenges.io',
          validationDomain: 'openchallenges.io',
        },
      ],
      lifecycle: {
        createBeforeDestroy: true,
      },
      tags: {
        Name: `${nameTagPrefix}-preview-cert`,
      },
    });
  }
}
