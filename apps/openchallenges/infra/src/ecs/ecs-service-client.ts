import { EcsService } from '@cdktf/provider-aws/lib/ecs-service';
import { Subnet } from '@cdktf/provider-aws/lib/subnet';
import { Construct } from 'constructs';

export class EcsServiceClient extends Construct {
  service: EcsService;

  constructor(
    scope: Construct,
    id: string,
    clusterArn: string,
    taskDefinitionArn: string,
    targetGroupArn: string,
    subnets: Subnet[],
    clientSecurityGroupId: string,
  ) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    this.service = new EcsService(this, id, {
      name: `${nameTagPrefix}-client`,
      cluster: clusterArn,
      taskDefinition: taskDefinitionArn,
      desiredCount: 1,
      launchType: 'FARGATE',

      loadBalancer: [
        {
          targetGroupArn,
          containerName: 'client',
          containerPort: 9090,
        },
      ],

      networkConfiguration: {
        subnets: subnets.map((subnet) => subnet.id),
        assignPublicIp: false,
        securityGroups: [clientSecurityGroupId],
      },
    });
  }
}
