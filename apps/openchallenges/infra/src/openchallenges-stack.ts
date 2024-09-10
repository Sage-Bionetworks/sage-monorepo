/* eslint-disable no-unused-vars */
/* eslint-disable no-new */
import { Construct } from 'constructs';
import { Aspects, TerraformOutput } from 'cdktf';
import { logger, Level } from './logger';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { Instance } from '@cdktf/provider-aws/lib/instance';
import { KeyPair } from '@cdktf/provider-aws/lib/key-pair';
import { SecurityGroup } from '@cdktf/provider-aws/lib/security-group';
// import { SecurityGroup } from '@cdktf/provider-aws/lib/security-group';
import * as os from 'os';
import * as fs from 'fs';
import { TagsAddingAspect } from './tag/tags-adding-aspect';
import { SageCostCenter, AmazonRegion } from './constants';
import { SageStack } from './sage-stack';
// import { S3Bucket } from '@cdktf/provider-aws/lib/s3-bucket';
import { NetworkConfig } from './network/network-config';
import { Network } from './network/network';
import { SecurityGroups } from './security-group/security-groups';
import { EcsClientAlb, EcsCluster, EcsUpstreamServiceAlb } from './ecs';
import { Database } from './ec2/database';
import { EcsTaskDefinitionClient } from './ecs/ecs-task-definition-client';
import { EcsServiceClient } from './ecs/ecs-service-client';
import { EcsTaskDefinitionGold } from './ecs/ecs-task-definition-gold';
import { EcsServiceUpstream } from './ecs/ecs-service-upstream';
import { EcsTaskDefinitionSilver } from './ecs/ecs-task-definition-silver';

export class OpenChallengesStack extends SageStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    const keyPath = `${os.homedir()}/.ssh/openchallenges-ec2.pub`;
    const publicKey = fs.readFileSync(keyPath, 'utf-8');
    const keyName = 'openchallenges-ec2-key';
    const stackOwnerEmail = 'thomas.schaffter@sagebionetworks.org';

    new AwsProvider(this, 'AWS', {
      region: AmazonRegion.US_EAST_1,
    });

    // const keyPair = new KeyPair(this, 'keypair', {
    //   publicKey,
    //   keyName,
    // });

    const networkConfig = new NetworkConfig({
      defaultRegion: AmazonRegion.US_EAST_1,
      tagPrefix: 'openchallenges',
      vpcCirdBlock: '10.70.0.0/16',
    });

    // The AWS VPC
    const network = new Network(this, 'network', networkConfig);

    // The security groups
    const securityGroups = new SecurityGroups(this, 'security_groups', network.vpc.id);

    // The ECS Cluster
    const cluster = new EcsCluster(this, 'ecs_cluster');

    // Load Balancers - Need to come first for references in Task Definitions
    const clientAlb = new EcsClientAlb(
      this,
      'client_alb',
      network.publicSubnets,
      securityGroups.clientAlbSg.id,
      network.vpc.id,
    );

    const goldAlb = new EcsUpstreamServiceAlb(
      this,
      'gold_alb',
      network.privateSubnets,
      securityGroups.upstreamServiceAlbSg.id,
      network.vpc.id,
    );

    const silverAlb = new EcsUpstreamServiceAlb(
      this,
      'silver_alb',
      network.privateSubnets,
      securityGroups.upstreamServiceAlbSg.id,
      network.vpc.id,
    );

    // The Database
    new Database(
      this,
      'database',
      // vars,
      network.privateSubnets[0].id,
      securityGroups.databaseSg.id,
      network.natGateway,
    );

    // Client Service Resources
    const clientTaskDefinition = new EcsTaskDefinitionClient(
      this,
      'client_task_definition',
      `http://${goldAlb.lb.dnsName},http://${silverAlb.lb.dnsName}`,
    );

    new EcsServiceClient(
      this,
      'client',
      cluster.cluster.arn,
      clientTaskDefinition.definition.arn,
      clientAlb.targetGroup.arn,
      network.privateSubnets,
      securityGroups.clientServiceSg.id,
    );

    // Gold Service Resources
    const goldTaskDefinition = new EcsTaskDefinitionGold(this, 'gold_task_definition');

    new EcsServiceUpstream(
      this,
      'gold',
      cluster.cluster.arn,
      goldTaskDefinition.definition.arn,
      goldAlb.targetGroup.arn,
      network.privateSubnets,
      securityGroups.upstreamServiceSg.id,
    );

    // Silver Service Resources
    const silverTaskDefinition = new EcsTaskDefinitionSilver(this, 'silver_task_definition');

    new EcsServiceUpstream(
      this,
      'silver',
      cluster.cluster.arn,
      silverTaskDefinition.definition.arn,
      silverAlb.targetGroup.arn,
      network.privateSubnets,
      securityGroups.upstreamServiceSg.id,
    );

    // Outputs
    new TerraformOutput(this, 'client_service_endpoint', {
      value: clientAlb.lb.dnsName,
      description: 'DNS name (endpoint) of the AWS ALB for Client service',
    });

    // new TerraformOutput(this, 'vpc_id', {
    //   value: network.vpc.id,
    // });

    // const ports = [22, 80, 443];

    // const securityGroup = new SecurityGroup(this, 'security_group', {
    //   name: 'openchallenges-sg',
    //   vpcId: network.vpc.id,
    //   egress: [
    //     {
    //       fromPort: 0,
    //       toPort: 0,
    //       cidrBlocks: ['0.0.0.0/0'],
    //       protocol: '-1',
    //     },
    //   ],
    //   ingress: ports.map((port) => ({
    //     fromPort: port,
    //     toPort: port,
    //     // TODO Do not use this in production, should be limited to specific IPs or disable (ssm?).
    //     cidrBlocks: ['0.0.0.0/0'],
    //     protocol: '-1',
    //   })),
    // });

    // const ec2Instance = new Instance(this, 'compute', {
    //   ami: Ami.UBUNTU_22_04_LTS,
    //   instanceType: AmazonEc2InstanceType.T2_MICRO,
    //   // subnetId: publicSubnetA.id,
    //   vpcSecurityGroupIds: [securityGroup.id],
    //   keyName: keyPair.keyName,
    // });

    // new TerraformOutput(this, 'public_ip', {
    //   value: ec2Instance.publicIp,
    // });

    // new S3Bucket(this, 'bucket', {
    //   bucket: 'myprefixdemoay7rcmbkaj0',
    // });

    // Add tags to every resource defined within this stack.
    Aspects.of(this).add(
      new TagsAddingAspect({
        OwnerEmail: stackOwnerEmail,
        CostCenter: SageCostCenter.ITCR,
      }),
    );
  }
}
