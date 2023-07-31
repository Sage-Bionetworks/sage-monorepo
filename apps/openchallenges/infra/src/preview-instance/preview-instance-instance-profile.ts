import { DataAwsIamPolicyDocument } from '@cdktf/provider-aws/lib/data-aws-iam-policy-document';
import { IamInstanceProfile } from '@cdktf/provider-aws/lib/iam-instance-profile';
import { IamRole } from '@cdktf/provider-aws/lib/iam-role';
import { Construct } from 'constructs';

export class PreviewInstanceInstanceProfile extends Construct {
  iamRole: IamRole;
  iamInstanceProfile: IamInstanceProfile;

  constructor(scope: Construct, id: string, nameTagPrefix: string) {
    super(scope, id);

    const iamPolicy = new DataAwsIamPolicyDocument(
      this,
      'preview_instance_assume_role',
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

    this.iamRole = new IamRole(this, 'preview_instance_role', {
      name: `${nameTagPrefix}-preview-instance-role`,
      assumeRolePolicy: iamPolicy.json,
      managedPolicyArns: [
        'arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore',
      ],
    });

    this.iamInstanceProfile = new IamInstanceProfile(
      this,
      'preview_instance_instance_profile',
      {
        name: `${nameTagPrefix}-preview-instance-instance-profile`,
        role: this.iamRole.name,
      }
    );
  }
}
