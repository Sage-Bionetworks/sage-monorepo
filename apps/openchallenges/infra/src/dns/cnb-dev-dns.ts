import { Construct } from 'constructs';
import { Route53Zone } from '@cdktf/provider-aws/lib/route53-zone';
import { AcmCertificate } from '@cdktf/provider-aws/lib/acm-certificate';

export class CnbDevDns extends Construct {
  devZone: Route53Zone;
  openchallengesDevCert: AcmCertificate;
  openchallengesProdCert: AcmCertificate;

  constructor(scope: Construct, id: string) {
    super(scope, id);

    const nameTagPrefix = 'cnb-dev';

    this.devZone = new Route53Zone(this, 'openchallenges_zone', {
      name: 'dev.openchallenges.io',
      tags: {
        Name: `${nameTagPrefix}-openchallenges-zone`,
      },
    });

    this.openchallengesDevCert = new AcmCertificate(
      this,
      'openchallenges_dev_cert',
      {
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
          Name: `${nameTagPrefix}-openchallenges-dev-cert`,
        },
      }
    );

    this.openchallengesProdCert = new AcmCertificate(
      this,
      'openchallenges_prod_cert',
      {
        domainName: 'openchallenges.io',
        subjectAlternativeNames: ['*.openchallenges.io'],
        validationMethod: 'DNS',
        validationOption: [
          {
            domainName: 'openchallenges.io',
            validationDomain: 'openchallenges.io',
          },
        ],
        lifecycle: {
          createBeforeDestroy: true,
        },
        tags: {
          Name: `${nameTagPrefix}-openchallenges-prod-cert`,
        },
      }
    );
  }
}
