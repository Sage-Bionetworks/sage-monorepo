/* eslint-disable no-unused-vars */
/* eslint-disable no-new */
import { Construct } from 'constructs';
import { SageStack } from './sage-stack';
import { AwsProvider } from '@cdktf/provider-aws/lib/provider';
import { AmazonRegion } from '../constants';
import { NetworkConfig } from '../network/network-config';
import { Network } from '../network/network';
import { SingleStackInstance } from '../ec2/single-stack-instance';

export class OpenChallengesPreviewStack extends SageStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    // const keyPath = `${os.homedir()}/.ssh/openchallenges-ec2.pub`;
    // const publicKey = fs.readFileSync(keyPath, 'utf-8');
    // const keyName = 'openchallenges-ec2-key';
    // const stackOwnerEmail = 'thomas.schaffter@sagebionetworks.org';

    new AwsProvider(this, 'AWS', {
      region: AmazonRegion.US_EAST_1,
    });

    const networkConfig = new NetworkConfig({
      defaultRegion: AmazonRegion.US_EAST_1,
      tagPrefix: 'openchallenges',
      vpcCirdBlock: '10.0.0.0/16',
    });

    const network = new Network(this, 'network', networkConfig);

    // const securityGroups = new SecurityGroups(
    //   this,
    //   'security_groups',
    //   network.vpc.id
    // );

    // new SingleStackInstance(
    //   this,
    //   'single_stack_instance',
    //   // vars,
    //   network.privateSubnets[0].id,
    //   securityGroups.databaseSg.id,
    //   network.natGateway
    // );
  }
}
