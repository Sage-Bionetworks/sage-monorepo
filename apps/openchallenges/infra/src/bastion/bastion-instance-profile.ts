import { DataAwsIamPolicyDocument } from '@cdktf/provider-aws/lib/data-aws-iam-policy-document';
import { IamInstanceProfile } from '@cdktf/provider-aws/lib/iam-instance-profile';
import { IamRole } from '@cdktf/provider-aws/lib/iam-role';
import { Construct } from 'constructs';

export class BastionInstanceProfile extends Construct {
  iamRole: IamRole;
  iamInstanceProfile: IamInstanceProfile;

  constructor(scope: Construct, id: string, nameTagPrefix: string) {
    super(scope, id);

    const iamPolicy = new DataAwsIamPolicyDocument(this, 'bastion_assume_role', {
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
    });

    this.iamRole = new IamRole(this, 'bastion_role', {
      name: `${nameTagPrefix}-bastion-role`,
      assumeRolePolicy: iamPolicy.json,
      managedPolicyArns: ['arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore'],
    });

    this.iamInstanceProfile = new IamInstanceProfile(this, 'bastion_instance_profile', {
      name: `${nameTagPrefix}-bastion-instance-profile`,
      role: this.iamRole.name,
    });
  }
}
