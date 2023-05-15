/* eslint-disable no-new */
import { Construct } from 'constructs';
import { SecurityGroupRule } from '@cdktf/provider-aws/lib/security-group-rule';
import { SecurityGroup } from '@cdktf/provider-aws/lib/security-group';

export class SecurityGroups extends Construct {
  clientAlbSecurityGroup: SecurityGroup;
  clientServiceSecurityGroup: SecurityGroup;
  upstreamServiceAlbSecurityGroup: SecurityGroup;
  upstreamServiceSecurityGroup: SecurityGroup;
  databaseSecurityGroup: SecurityGroup;

  constructor(scope: Construct, id: string, vpcId: string) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    // reusable ingress 80 rule
    const allowIngress80 = (securityGroupId: string, constructId: string) =>
      new SecurityGroupRule(this, constructId, {
        securityGroupId,
        type: 'ingress',
        protocol: 'tcp',
        fromPort: 80,
        toPort: 80,
        cidrBlocks: ['0.0.0.0/0'],
        ipv6CidrBlocks: ['::/0'],
        description: 'Allow HTTP traffic',
      });

    // reusable egress all rule
    const allowEgressAll = (securityGroupId: string, constructId: string) =>
      new SecurityGroupRule(this, constructId, {
        securityGroupId,
        type: 'egress',
        protocol: '-1',
        fromPort: 0,
        toPort: 0,
        cidrBlocks: ['0.0.0.0/0'],
        ipv6CidrBlocks: ['::/0'],
        description: 'Allow any outbound traffic',
      });

    const allowInboundSelf = (securityGroupId: string, constructId: string) =>
      new SecurityGroupRule(this, constructId, {
        securityGroupId,
        type: 'ingress',
        protocol: '-1',
        fromPort: 0,
        toPort: 0,
        selfAttribute: true,
        description:
          'Allow any inbound traffic from others with same security group',
      });

    // Client Application Load Balancer Security Group and Rules
    this.clientAlbSecurityGroup = new SecurityGroup(
      this,
      'client_alb_security_group',
      {
        namePrefix: `${nameTagPrefix}-ecs-client-alb`,
        description:
          'Security group for client service application load balancer',
        vpcId,
      }
    );
    allowIngress80(this.clientAlbSecurityGroup.id, 'client_alb_allow_80');
    allowEgressAll(this.clientAlbSecurityGroup.id, 'client_alb_allow_outbound');

    // Client Service Security Group and Rules
    this.clientServiceSecurityGroup = new SecurityGroup(
      this,
      'client_service',
      {
        namePrefix: `${nameTagPrefix}-client-service`,
        description: 'security group for client service',
        vpcId,
      }
    );

    new SecurityGroupRule(this, 'client_service_allow_alb_9090', {
      securityGroupId: this.clientServiceSecurityGroup.id,
      type: 'ingress',
      protocol: 'tcp',
      fromPort: 9090,
      toPort: 9090,
      sourceSecurityGroupId: this.clientAlbSecurityGroup.id,
      description: 'Allow HTTP traffic on 9090 from the Client ALB',
    });
    allowInboundSelf(
      this.clientServiceSecurityGroup.id,
      'client_service_allow_inbound_self'
    );
    allowEgressAll(
      this.clientServiceSecurityGroup.id,
      'client_service_allow_outbound'
    );

    // Upstream Service ALB Security Group and Rules
    this.upstreamServiceAlbSecurityGroup = new SecurityGroup(
      this,
      'upstream_service_alb',
      {
        namePrefix: `${nameTagPrefix}-upstream-service-alb`,
        description: 'Security group for upstream services ALB',
        vpcId,
      }
    );

    new SecurityGroupRule(this, 'upstream_service_alb_allow_client_80', {
      securityGroupId: this.upstreamServiceAlbSecurityGroup.id,
      type: 'ingress',
      protocol: 'tcp',
      fromPort: 80,
      toPort: 80,
      // sourceSecurityGroupId: this.clientService.id,
      cidrBlocks: ['0.0.0.0/0'],
      description: 'Allow HTTP traffic on 80 from the Client Service',
    });
    allowEgressAll(
      this.upstreamServiceAlbSecurityGroup.id,
      'upstream_service_alb_allow_outbound'
    );

    // Upstream Service Security Group and Rules
    this.upstreamServiceSecurityGroup = new SecurityGroup(
      this,
      'upstream_service',
      {
        namePrefix: `${nameTagPrefix}-upstream-service`,
        description: 'Security group for upstream services',
        vpcId,
      }
    );

    new SecurityGroupRule(this, 'upstream_service_allow_alb_9090', {
      securityGroupId: this.upstreamServiceSecurityGroup.id,
      type: 'ingress',
      protocol: 'tcp',
      fromPort: 9090,
      toPort: 9090,
      sourceSecurityGroupId: this.upstreamServiceAlbSecurityGroup.id,
      description: 'Allow HTTP traffic on 9090 from the Upstream Service ALB',
    });
    allowInboundSelf(
      this.upstreamServiceSecurityGroup.id,
      'upstream_service_allow_inbound_self'
    );
    allowEgressAll(
      this.upstreamServiceSecurityGroup.id,
      'upstream_service_allow_outbound'
    );

    // Database Security Group and Rules
    this.databaseSecurityGroup = new SecurityGroup(this, 'database', {
      namePrefix: `${nameTagPrefix}-database`,
      description: 'Security group for the database',
      vpcId,
    });

    new SecurityGroupRule(this, 'database_allow_service_27017', {
      securityGroupId: this.databaseSecurityGroup.id,
      type: 'ingress',
      protocol: 'tcp',
      fromPort: 27017,
      toPort: 27017,
      sourceSecurityGroupId: this.upstreamServiceSecurityGroup.id,
      description: 'Allow HTTP traffic on 27017 from the Upstream Services',
    });
    allowEgressAll(this.databaseSecurityGroup.id, 'database_allow_outbound');
  }
}
