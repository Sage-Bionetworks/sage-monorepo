import { Construct } from 'constructs';
import { ecsCluster } from '@cdktf/provider-aws';

export class EcsCluster extends Construct {
  ecsCluster: ecsCluster.EcsCluster;

  constructor(scope: Construct, id: string) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    this.ecsCluster = new ecsCluster.EcsCluster(this, id, {
      name: `${nameTagPrefix}-ecs-cluster`,
    });
  }
}
