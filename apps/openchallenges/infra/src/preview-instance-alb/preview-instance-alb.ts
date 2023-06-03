/* eslint-disable no-unused-vars */
import { Alb } from '@cdktf/provider-aws/lib/alb';
import { AlbTargetGroupAttachment } from '@cdktf/provider-aws/lib/alb-target-group-attachment';
import { LbListener } from '@cdktf/provider-aws/lib/lb-listener';
import { LbTargetGroup } from '@cdktf/provider-aws/lib/lb-target-group';
import { Subnet } from '@cdktf/provider-aws/lib/subnet';
import { Construct } from 'constructs';

export class PreviewInstanceAlb extends Construct {
  lb: Alb;
  targetGroup: LbTargetGroup;
  listener: LbListener;

  constructor(
    scope: Construct,
    id: string,
    subnets: Subnet[],
    securityGroupId: string,
    vpcId: string,
    targetId: string
  ) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    this.lb = new Alb(this, 'preview_instance_alb', {
      securityGroups: [securityGroupId],
      namePrefix: `pi-`,
      loadBalancerType: 'application',
      subnets: subnets.map((subnet) => subnet.id),
      idleTimeout: 60,
      ipAddressType: 'dualstack',
      tags: { Name: `${nameTagPrefix}-preview-instance-alb` },
    });

    this.targetGroup = new LbTargetGroup(this, 'preview_instance_alb_targets', {
      namePrefix: 'pi-',
      port: 8080,
      protocol: 'HTTP',
      vpcId,
      deregistrationDelay: '30',
      targetType: 'ip',

      healthCheck: {
        enabled: true,
        path: '/',
        healthyThreshold: 3,
        unhealthyThreshold: 3,
        timeout: 30,
        interval: 60,
        protocol: 'HTTP',
      },

      tags: { Name: `${nameTagPrefix}-preview-instance-tg` },
    });

    this.listener = new LbListener(this, 'preview_instance_alb_http_80', {
      loadBalancerArn: this.lb.arn,
      port: 80,
      protocol: 'HTTP',

      defaultAction: [
        {
          type: 'forward',
          targetGroupArn: this.targetGroup.arn,
        },
      ],
    });

    const targetGroupAttachment = new AlbTargetGroupAttachment(
      this,
      'preview_instance_alb_targer_group_attachment',
      {
        targetGroupArn: this.targetGroup.arn,
        targetId,
      }
    );
  }
}
