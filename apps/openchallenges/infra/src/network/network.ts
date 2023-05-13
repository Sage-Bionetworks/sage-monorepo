import { Subnet } from '@cdktf/provider-aws/lib/subnet';
import { Vpc } from '@cdktf/provider-aws/lib/vpc';
import { Fn } from 'cdktf';
import { Construct } from 'constructs';
import { logger } from '../logger';
import { NetworkConfig } from './network-config';

export class Network extends Construct {
  vpc: Vpc;
  azs: string[];
  publicSubnets: Subnet[];
  privateSubnets: Subnet[];

  constructor(scope: Construct, id: string, config: NetworkConfig) {
    super(scope, id);

    const nameTagPrefix = 'openchallenges';

    this.azs = ['a', 'b'].map((zone) => `${config.defaultRegion}${zone}`);

    logger.info('azs', this.azs);

    this.vpc = new Vpc(this, 'vpc', {
      // assignGeneratedIpv6CidrBlock: true,
      cidrBlock: config.vpcCirdBlock,
      enableDnsHostnames: true,
      enableDnsSupport: true,
      // instanceTenancy: "default",
      tags: {
        Name: `${config.tagPrefix}-vpc`,
      },
    });

    this.publicSubnets = this.azs.map((az, index) => {
      return new Subnet(this, `public-subnet-${az}`, {
        // assignIpv6AddressOnCreation: true,
        availabilityZone: az,
        cidrBlock: Fn.cidrsubnet(this.vpc.cidrBlock, 4, index),
        // ipv6CidrBlock: Fn.cidrsubnet(this.vpc.ipv6CidrBlock, 8, index),
        mapPublicIpOnLaunch: true,
        tags: {
          Name: `${nameTagPrefix}-public-subnet-${az}`,
        },
        vpcId: this.vpc.id,
      });
    });

    this.privateSubnets = this.azs.map((az, index) => {
      return new Subnet(this, `private-subnet-${az}`, {
        availabilityZone: az,
        cidrBlock: Fn.cidrsubnet(
          this.vpc.cidrBlock,
          4,
          index + this.publicSubnets.length
        ),
        tags: {
          Name: `${nameTagPrefix}-private-subnet-${az}`,
        },
        vpcId: this.vpc.id,
      });
    });
  }
}
