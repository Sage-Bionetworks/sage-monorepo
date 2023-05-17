import { Construct } from 'constructs';
import { EcsTaskDefinition } from '@cdktf/provider-aws/lib/ecs-task-definition';
import { Fn } from 'cdktf';

export class EcsTaskDefinitionClient extends Construct {
  definition: EcsTaskDefinition;

  constructor(scope: Construct, id: string, upstreamUriString: string) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    this.definition = new EcsTaskDefinition(this, 'task_definition', {
      family: `${nameTagPrefix}-client`,
      memory: '512',
      cpu: '256',
      networkMode: 'awsvpc',

      containerDefinitions: Fn.jsonencode([
        {
          name: 'client',
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
              value: 'client',
            },
            {
              name: 'MESSAGE',
              value: 'Hello World from the client!',
            },
            {
              name: 'UPSTREAM_URIS',
              value: upstreamUriString,
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
