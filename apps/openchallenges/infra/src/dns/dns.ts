import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { Construct } from 'constructs';
import { DnsConfig } from '../config/dns-config';
import { logger } from '../logger';

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
  }
}
