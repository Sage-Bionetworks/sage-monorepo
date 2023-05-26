/* eslint-disable no-unused-vars */
import { Instance } from '@cdktf/provider-aws/lib/instance';
import { Construct } from 'constructs';
import { readFileSync } from 'fs';
import { BastionConfig } from './bastion-config';
import { IamInstanceProfile } from '@cdktf/provider-aws/lib/iam-instance-profile';
import { IamRole } from '@cdktf/provider-aws/lib/iam-role';
import { DataAwsIamPolicyDocument } from '@cdktf/provider-aws/lib/data-aws-iam-policy-document';

export class Bastion extends Construct {
  instance: Instance;

  constructor(scope: Construct, id: string, config: BastionConfig) {
    super(scope, id);

    const policyDocument = new DataAwsIamPolicyDocument(
      this,
      'bastion_assume_role',
      {
        statement: [
          {
            actions: ['sts:AssumeRole'],
            effect: 'Allow',
            principals: [
              {
                identifiers: ['ec2.amazonaws.com'],
                type: 'Service',
              },
            ],
          },
        ],
      }
    );

    const ssmInstanceRole = new IamRole(this, 'bastion_iam_role', {
      name: `${config.tagPrefix}-ec2-role`,
      assumeRolePolicy: policyDocument.json,
      managedPolicyArns: [
        'arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore',
      ],
    });

    const ssmInstanceProfile = new IamInstanceProfile(
      this,
      'ssm_instance_profile',
      {
        name: `${config.tagPrefix}-ssm-instance-profile`,
        role: ssmInstanceRole.name,
      }
    );

    this.instance = new Instance(this, id, {
      ami: config.ami,
      instanceType: config.instanceType,
      keyName: config.keyName,
      privateIp: config.privateIp,
      subnetId: config.subnetId,
      tags: { Name: `${config.tagPrefix}-bastion` },
      userData: readFileSync('./src/resources/scripts/bastion.sh', 'utf8'),
      vpcSecurityGroupIds: [config.securityGroupId],
      iamInstanceProfile: ssmInstanceProfile.name,
    });
  }
}
