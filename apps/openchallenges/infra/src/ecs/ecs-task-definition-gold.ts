import { Construct } from 'constructs';
import { EcsTaskDefinition } from '@cdktf/provider-aws/lib/ecs-task-definition';
import { Fn } from 'cdktf';

export class EcsTaskDefinitionGold extends Construct {
  definition: EcsTaskDefinition;

  constructor(scope: Construct, id: string, executionRoleArn: string) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    this.definition = new EcsTaskDefinition(this, 'task_definition', {
      family: `${nameTagPrefix}-gold`,
      memory: '512',
      cpu: '256',
      networkMode: 'awsvpc',
      executionRoleArn,

      containerDefinitions: Fn.jsonencode([
        {
          name: 'gold',
          image: 'nicholasjackson/fake-service:v0.23.1',
          cpu: 0,
          essential: true,

          portMappings: [
            {
              containerPort: 9090,
              hostPort: 9090,
              protocol: 'tcp',
            },
          ],

          environment: [
            {
              name: 'NAME',
              value: 'gold',
            },
            {
              name: 'MESSAGE',
              value: 'Hello World from the gold service!',
            },
            {
              name: 'UPSTREAM_URIS',
              value: `http://10.0.32.253:27017`,
            },
          ],
        },
        // {
        //   name: 'datadog-agent',
        //   image: 'public.ecr.aws/datadog/agent:latest',
        //   environment: [
        //     {
        //       name: 'DD_API_KEY',
        //       value: `${process.env.DD_API_KEY}`,
        //     },
        //     {
        //       name: 'ECS_FARGATE',
        //       value: 'true', // https://github.com/aws/amazon-ecs-agent/issues/2571, seems that unmarshal in Go doesn't allow for straight booleans
        //     },
        //   ],
        // },
      ]),
    });
  }
}
