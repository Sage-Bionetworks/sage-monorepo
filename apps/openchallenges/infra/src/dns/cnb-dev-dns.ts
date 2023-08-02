import { Construct } from 'constructs';
import { Route53Zone } from '@cdktf/provider-aws/lib/route53-zone';
import { AcmCertificate } from '@cdktf/provider-aws/lib/acm-certificate';

export class CnbDevDns extends Construct {
  devZone: Route53Zone;
  cert: AcmCertificate;

  constructor(scope: Construct, id: string) {
    super(scope, id);

    const nameTagPrefix = 'cnb-dev';

    this.devZone = new Route53Zone(this, 'openchallenges_zone', {
      name: 'dev.openchallenges.io',
      tags: {
        Name: `${nameTagPrefix}-openchallenges-zone`,
      },
    });

    this.cert = new AcmCertificate(this, 'openchallenges_cert', {
      domainName: 'dev.openchallenges.io',
      validationMethod: 'DNS',
      validationOption: [
        {
          domainName: 'dev.openchallenges.io',
          validationDomain: 'openchallenges.io',
        },
      ],
      lifecycle: {
        createBeforeDestroy: true,
      },
      tags: {
        Name: `${nameTagPrefix}-openchallenges-cert`,
      },
    });
  }
}
