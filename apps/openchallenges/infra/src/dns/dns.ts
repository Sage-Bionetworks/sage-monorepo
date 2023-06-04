/* eslint-disable no-new */
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { Construct } from 'constructs';
import { DnsConfig } from '../config/dns-config';
import { logger } from '../logger';
import { Route53Zone } from '@cdktf/provider-aws/lib/route53-zone';
import { ZoneConfig } from '../config/zone-config';
import { Route53Record } from '@cdktf/provider-aws/lib/route53-record';

export class Dns extends Construct {
  awsProviders: { [id: string]: AwsProvider } = {};

  constructor(scope: Construct, id: string, config: DnsConfig) {
    super(scope, id);

    // const nameTagPrefix = 'openchallenges';
    logger.info('DNS config, {}', config);

    config.accounts.forEach((accountConfig) => {
      const id = accountConfig.alias || 'default';
      this.awsProviders[id] = new AwsProvider(this, id, accountConfig);
    });

    // Configure hosted zones
    config.zones.forEach((zoneConfig) => {
      this.createZone(null, zoneConfig);
    });
  }

  // Recursive method for adding zones
  createZone(parent: Route53Zone | null, zoneConfig: ZoneConfig) {
    const id = zoneConfig.name.replace('.', '_');

    // add defaults and provider to zone config
    zoneConfig = {
      ...{
        provider: this.awsProviders[zoneConfig.account],
        zones: [],
        records: [],
      },
      ...zoneConfig,
    };
    const zone = new Route53Zone(this, id, zoneConfig);

    // if there is a parent zone, add NS records to it that point to the current zone
    if (parent != null) {
      const recordId = `${parent.id}_${id}`;
      new Route53Record(this, recordId, {
        provider: parent.provider,
        zoneId: parent.zoneId,
        type: 'NS',
        ttl: 300,
        name: zone.name,
        records: zone.nameServers,
      });
    }

    zoneConfig.zones.forEach((z) => this.createZone(zone, z));
    zoneConfig.records.forEach((r) => {
      const recordId = `${zone.id}_${r.name.replace('.', '_')}_${r.type}`;
      const record = {
        ...r,
        ...{
          provider: zone.provider,
          zoneId: zone.zoneId,
        },
      };
      new Route53Record(this, recordId, record);
    });
  }
}
