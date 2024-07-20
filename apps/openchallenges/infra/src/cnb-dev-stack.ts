/* eslint-disable no-new */
import { Construct } from 'constructs';
import { SageStack } from './sage-stack';
import { Aspects, TerraformOutput } from 'cdktf';
import { TagsAddingAspect } from './tag/tags-adding-aspect';
import { CnbDevDns } from './dns/cnb-dev-dns';
import { AmazonRegion, SageCostCenter } from './constants';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';

export class CnbDevStack extends SageStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    const stackOwnerEmail = 'thomas.schaffter@sagebionetworks.org';

    // The AWS provider
    new AwsProvider(this, 'AWS', {
      region: AmazonRegion.US_EAST_1,
    });

    // The DNS configuration
    const dns = new CnbDevDns(this, 'dns');

    // Add tags to every resource defined within this stack.
    Aspects.of(this).add(
      new TagsAddingAspect({
        OwnerEmail: stackOwnerEmail,
        CostCenter: SageCostCenter.ITCR,
      }),
    );

    // Outputs
    new TerraformOutput(this, 'dev_zone_id', {
      value: dns.devZone.id,
    });
    new TerraformOutput(this, 'openchallenges_cert_arn', {
      value: dns.openchallengesCert.arn,
    });
  }
}
