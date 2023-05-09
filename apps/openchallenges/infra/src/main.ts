/* eslint-disable no-new */
import { Construct } from 'constructs';
import {
  App,
  Aspects,
  CloudBackend,
  NamedCloudWorkspace,
  TerraformOutput,
  TerraformStack,
} from 'cdktf';
import { logger, Level } from './logger';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { Instance } from '@cdktf/provider-aws/lib/instance';
import { KeyPair } from '@cdktf/provider-aws/lib/key-pair';
import { Vpc } from '@cdktf/provider-aws/lib/vpc';
import { Subnet } from '@cdktf/provider-aws/lib/subnet';
import { InternetGateway } from '@cdktf/provider-aws/lib/internet-gateway';
import { Eip } from '@cdktf/provider-aws/lib/eip';
import { NatGateway } from '@cdktf/provider-aws/lib/nat-gateway';
import { RouteTable } from '@cdktf/provider-aws/lib/route-table';
import { Route } from '@cdktf/provider-aws/lib/route';
import { RouteTableAssociation } from '@cdktf/provider-aws/lib/route-table-association';
// import { SecurityGroup } from '@cdktf/provider-aws/lib/security-group';
import * as os from 'os';
import * as fs from 'fs';
import { TagsAddingAspect } from './aspect/tags-adding-aspect';
import {
  Ami,
  AmazonEc2InstanceType,
  SageCostCenter,
  AmazonRegion,
} from './constants';

class OpenChallengesStack extends TerraformStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    const keyPath = `${os.homedir()}/.ssh/openchallenges-ec2.pub`;
    const publicKey = fs.readFileSync(keyPath, 'utf-8');
    const keyName = 'openchallenges-ec2-key';
    const stackOwnerEmail = 'thomas.schaffter@sagebionetworks.org';

    new AwsProvider(this, 'AWS', {
      region: AmazonRegion.US_EAST_1,
    });

    const keyPair = new KeyPair(this, 'keypair', {
      publicKey,
      keyName,
    });

    const vpc = new Vpc(this, 'VPC', {
      cidrBlock: '10.0.0.0/16',
      tags: {
        Name: 'OpenChallenges-VPC',
      },
    });

    const privateSubnetA = new Subnet(this, 'Private-Subnet-A', {
      availabilityZone: 'us-east-1a',
      vpcId: vpc.id,
      mapPublicIpOnLaunch: false,
      cidrBlock: '10.0.1.0/24',
      tags: {
        Name: 'OpenChallenges-Private-Subnet-A',
      },
    });

    const publicSubnetA = new Subnet(this, 'Public-Subnet-A', {
      availabilityZone: 'us-east-1a',
      vpcId: vpc.id,
      mapPublicIpOnLaunch: true,
      cidrBlock: '10.0.6.0/24',
      tags: {
        Name: 'OpenChallenges-Public-Subnet-A',
      },
    });

    const internetGateway = new InternetGateway(this, 'Internet-Gateway', {
      vpcId: vpc.id,
      tags: {
        Name: 'OpenChallenges-Internet-Gateway',
      },
    });

    const publicIpA = new Eip(this, 'Public-Eip-A', {
      vpc: true,
      tags: {
        Name: 'OpenChallenges-Public-Eip-A',
      },
    });

    // Create NAT Gateway For communication Public and Private network
    const natGatewayA = new NatGateway(this, 'NAT-Gateway-A', {
      allocationId: publicIpA.id,
      subnetId: publicSubnetA.id,
      tags: {
        Name: 'OpenChallenges-Public-NAT-Gateway-A',
      },
    });

    // Create Routing Table for communication Public network with Route and Association route
    const publicRouteTable = new RouteTable(this, 'Public-Route-Table', {
      vpcId: vpc.id,
      tags: {
        Name: 'OpenChallenges-Public-Route-Table',
      },
    });

    new Route(this, 'Route', {
      destinationCidrBlock: '0.0.0.0/0',
      routeTableId: publicRouteTable.id,
      gatewayId: internetGateway.id,
    });

    new RouteTableAssociation(this, 'Route-Table-Association-Public-Subnet-A', {
      routeTableId: publicRouteTable.id,
      subnetId: publicSubnetA.id,
    });

    // Create Routing Table for communication Private network with Route and Association route
    const privateRouteTableA = new RouteTable(this, 'Private-Route-Table-A', {
      vpcId: vpc.id,
      tags: {
        Name: 'OpenChallenges-Private-Route-Table-A',
      },
    });

    new Route(this, 'Private-Route-A', {
      destinationCidrBlock: '0.0.0.0/0',
      routeTableId: privateRouteTableA.id,
      natGatewayId: natGatewayA.id,
    });

    new RouteTableAssociation(
      this,
      'Route-Table-Association-Private-Subnet-A',
      {
        routeTableId: privateRouteTableA.id,
        subnetId: privateSubnetA.id,
      }
    );

    new TerraformOutput(this, 'VPC id', {
      value: vpc.id,
    });

    // const ports = [22, 80, 443, 5432];

    // const securityGroup = new SecurityGroup(this, 'security_group', {
    //   name: 'openchallenges-sg',
    //   vpcId: 'openchallenges-vpc',
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
    //     cidrBlocks: ['0.0.0.0/0'],
    //     protocol: '-1',
    //   })),
    // });

    const ec2Instance = new Instance(this, 'compute', {
      ami: Ami.UBUNTU_22_04_LTS,
      instanceType: AmazonEc2InstanceType.T2_MICRO,
      keyName: keyPair.keyName,
    });

    new TerraformOutput(this, 'public_ip', {
      value: ec2Instance.publicIp,
    });

    // Add tags to every resource defined within this stack.
    Aspects.of(this).add(
      new TagsAddingAspect({
        OwnerEmail: stackOwnerEmail,
        CostCenter: SageCostCenter.ITCR,
      })
    );
  }
}

logger.setLevel(Level.Debug);
logger.info('Welcome to the deployment of the OpenChallenges stack.');

const app = new App();
const stack = new OpenChallengesStack(app, 'openchallenges-stack');

new CloudBackend(stack, {
  hostname: 'app.terraform.io',
  organization: 'sagebionetworks',
  workspaces: new NamedCloudWorkspace('openchallenges-test'),
});

app.synth();
