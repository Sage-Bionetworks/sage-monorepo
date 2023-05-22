/* eslint-disable no-new */
import { Construct } from 'constructs';
import { SecurityGroupRule } from '@cdktf/provider-aws/lib/security-group-rule';
import { SecurityGroup } from '@cdktf/provider-aws/lib/security-group';

export class SecurityGroups extends Construct {
  clientAlbSg: SecurityGroup;
  clientServiceSg: SecurityGroup;
  upstreamServiceAlbSg: SecurityGroup;
  upstreamServiceSg: SecurityGroup;
  databaseSg: SecurityGroup;
  bastionSg: SecurityGroup;
  sshFromBastionSg: SecurityGroup;

  constructor(scope: Construct, id: string, vpcId: string) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';
    const bastionPrivateIp = '10.0.2.172'; // TODO DRY

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

    // reusable ingress SSH rule
    const allowIngressSsh = (securityGroupId: string, constructId: string) =>
      new SecurityGroupRule(this, constructId, {
        securityGroupId,
        type: 'ingress',
        protocol: 'tcp',
        fromPort: 22,
        toPort: 22,
        cidrBlocks: ['0.0.0.0/0'],
        description: 'Allow SSH traffic',
      });

    // reusable ingress SSH from bastion rule
    const allowIngressSshFromBastion = (
      securityGroupId: string,
      constructId: string
    ) =>
      new SecurityGroupRule(this, constructId, {
        securityGroupId,
        type: 'ingress',
        protocol: 'tcp',
        fromPort: 22,
        toPort: 22,
        cidrBlocks: [`${bastionPrivateIp}/32`],
        description: 'Allow SSH traffic from the bastion',
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

    // Bastion security group and rules
    this.bastionSg = new SecurityGroup(this, 'bastion_security_group', {
      namePrefix: `${nameTagPrefix}-bastion`,
      description: 'Security group for the bastion',
      vpcId,
    });
    allowIngressSsh(this.bastionSg.id, 'bastion_allow_ssh');
    allowEgressAll(this.bastionSg.id, 'bastion_allow_outbound');

    // SSH from bastion security group and rules
    this.sshFromBastionSg = new SecurityGroup(
      this,
      'ssh_from_bastion_security_group',
      {
        namePrefix: `${nameTagPrefix}-ssh-from-bastion`,
        description: 'SSH from bastion security group for the bastion',
        vpcId,
      }
    );
    allowIngressSshFromBastion(
      this.sshFromBastionSg.id,
      'allow_ssh_from_bastion'
    );

    // Client Application Load Balancer Security Group and Rules
    this.clientAlbSg = new SecurityGroup(this, 'client_alb_security_group', {
      namePrefix: `${nameTagPrefix}-ecs-client-alb`,
      description:
        'Security group for client service application load balancer',
      vpcId,
    });
    allowIngress80(this.clientAlbSg.id, 'client_alb_allow_80');
    allowEgressAll(this.clientAlbSg.id, 'client_alb_allow_outbound');

    // Client Service Security Group and Rules
    this.clientServiceSg = new SecurityGroup(this, 'client_service', {
      namePrefix: `${nameTagPrefix}-client-service`,
      description: 'security group for client service',
      vpcId,
    });

    new SecurityGroupRule(this, 'client_service_allow_alb_9090', {
      securityGroupId: this.clientServiceSg.id,
      type: 'ingress',
      protocol: 'tcp',
      fromPort: 9090,
      toPort: 9090,
      sourceSecurityGroupId: this.clientAlbSg.id,
      description: 'Allow HTTP traffic on 9090 from the Client ALB',
    });
    allowInboundSelf(
      this.clientServiceSg.id,
      'client_service_allow_inbound_self'
    );
    allowEgressAll(this.clientServiceSg.id, 'client_service_allow_outbound');

    // Upstream Service ALB Security Group and Rules
    this.upstreamServiceAlbSg = new SecurityGroup(
      this,
      'upstream_service_alb',
      {
        namePrefix: `${nameTagPrefix}-upstream-service-alb`,
        description: 'Security group for upstream services ALB',
        vpcId,
      }
    );

    new SecurityGroupRule(this, 'upstream_service_alb_allow_client_80', {
      securityGroupId: this.upstreamServiceAlbSg.id,
      type: 'ingress',
      protocol: 'tcp',
      fromPort: 80,
      toPort: 80,
      // sourceSecurityGroupId: this.clientService.id,
      cidrBlocks: ['0.0.0.0/0'],
      description: 'Allow HTTP traffic on 80 from the Client Service',
    });
    allowEgressAll(
      this.upstreamServiceAlbSg.id,
      'upstream_service_alb_allow_outbound'
    );

    // Upstream Service Security Group and Rules
    this.upstreamServiceSg = new SecurityGroup(this, 'upstream_service', {
      namePrefix: `${nameTagPrefix}-upstream-service`,
      description: 'Security group for upstream services',
      vpcId,
    });

    new SecurityGroupRule(this, 'upstream_service_allow_alb_9090', {
      securityGroupId: this.upstreamServiceSg.id,
      type: 'ingress',
      protocol: 'tcp',
      fromPort: 9090,
      toPort: 9090,
      sourceSecurityGroupId: this.upstreamServiceAlbSg.id,
      description: 'Allow HTTP traffic on 9090 from the Upstream Service ALB',
    });
    allowInboundSelf(
      this.upstreamServiceSg.id,
      'upstream_service_allow_inbound_self'
    );
    allowEgressAll(
      this.upstreamServiceSg.id,
      'upstream_service_allow_outbound'
    );

    // Database Security Group and Rules
    this.databaseSg = new SecurityGroup(this, 'database', {
      namePrefix: `${nameTagPrefix}-database`,
      description: 'Security group for the database',
      vpcId,
    });

    new SecurityGroupRule(this, 'database_allow_service_27017', {
      securityGroupId: this.databaseSg.id,
      type: 'ingress',
      protocol: 'tcp',
      fromPort: 27017,
      toPort: 27017,
      sourceSecurityGroupId: this.upstreamServiceSg.id,
      description: 'Allow HTTP traffic on 27017 from the Upstream Services',
    });
    allowEgressAll(this.databaseSg.id, 'database_allow_outbound');
  }
}
