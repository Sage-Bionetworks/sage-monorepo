import { Route53RecordConfig } from '@cdktf/provider-aws/lib/route53-record';
import { Route53ZoneConfig } from '@cdktf/provider-aws/lib/route53-zone';

export type ZoneConfig = Route53ZoneConfig & {
  account: string;
  zones: ZoneConfig[];
  records: Route53RecordConfig[];
};
