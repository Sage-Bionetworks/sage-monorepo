const cdk = require('aws-cdk-lib');
const route53 = require('aws-cdk-lib/aws-route53');

class DevZoneStack extends cdk.Stack {
  constructor(scope, id, props = {}) {
    super(scope, id, props);

    // Prefer: cdk deploy -c domain=example.com
    // Fallback: DOMAIN_NAME=example.com cdk deploy
    const domainName = this.node.tryGetContext('domain') ||process.env.DOMAIN_NAME;

    if (!domainName) {
      throw new Error(
        'Domain name is required. Use: cdk deploy -c domain=example.com (or set DOMAIN_NAME)'
      );
    }

    const hostedZone = new route53.PublicHostedZone(this, 'HostedZone', {
      zoneName: domainName,
      comment: `Hosted zone for ${domainName}`,
    });

    new cdk.CfnOutput(this, 'ZoneId', { value: hostedZone.hostedZoneId });
    new cdk.CfnOutput(this, 'ZoneName', { value: hostedZone.zoneName });
    new cdk.CfnOutput(this, 'NameServers', {
      value: cdk.Fn.join(', ', hostedZone.hostedZoneNameServers ?? []),
      description: 'Update your domain registrar with these NS records',
    });
  }
}

module.exports = { DevZoneStack };

