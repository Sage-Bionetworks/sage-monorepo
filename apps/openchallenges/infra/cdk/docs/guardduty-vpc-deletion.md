# GuardDuty VPC Endpoint and Stack Deletion

## Problem

When destroying CloudFormation stacks that use ECS Fargate with GuardDuty Runtime Monitoring enabled, VPC deletion fails with errors like:

```
The subnet 'subnet-xxx' has dependencies and cannot be deleted.
The vpc 'vpc-xxx' has dependencies and cannot be deleted.
```

## Root Cause

AWS GuardDuty ECS Runtime Monitoring automatically creates AWS-managed resources outside of CloudFormation:

1. **VPC Endpoint**: `com.amazonaws.us-east-1.guardduty-data` (Interface endpoint)

   - Creates Elastic Network Interfaces (ENIs) in private subnets
   - Required for GuardDuty agent sidecar containers to communicate with GuardDuty service

2. **Security Group**: `GuardDutyManagedSecurityGroup-vpc-*`
   - Attached to the GuardDuty VPC endpoint
   - Allows traffic between ECS tasks and GuardDuty service

These resources are created when you:

- Enable GuardDuty Runtime Monitoring for ECS
- Deploy Fargate tasks (which automatically get GuardDuty agent sidecar containers)

Because these resources are created outside CloudFormation, they are **not tracked in the CDK stack** and don't get deleted when you destroy the stack, causing the VPC deletion to fail.

## Solution

### Implemented Fix

We now **explicitly create and manage the GuardDuty VPC endpoint** in our CDK stack (`vpc_construct.py`):

```python
# Create VPC endpoint for GuardDuty
# This is required for ECS Runtime Monitoring and must be explicitly
# managed to avoid orphaned resources during stack deletion
self.guardduty_endpoint = ec2.InterfaceVpcEndpoint(
    self,
    "GuardDutyEndpoint",
    vpc=self.vpc,
    service=ec2.InterfaceVpcEndpointAwsService.GUARDDUTY_DATA,
    # Place endpoints in private subnets for security
    subnets=ec2.SubnetSelection(subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS),
    # Enable private DNS to use standard GuardDuty endpoint name
    private_dns_enabled=True,
)
```

**Benefits:**

- VPC endpoint is tracked in CloudFormation
- Automatically deleted when stack is destroyed
- No orphaned resources
- Clean stack deletion

### Manual Cleanup (If Needed)

If you deployed before this fix was implemented, you may need to manually clean up GuardDuty resources:

#### 1. Identify GuardDuty Resources

```bash
# List VPC endpoints
aws ec2 describe-vpc-endpoints \
  --filters "Name=vpc-id,Values=YOUR_VPC_ID" \
  --query 'VpcEndpoints[*].[VpcEndpointId,ServiceName,State]' \
  --output table

# List GuardDuty security groups
aws ec2 describe-security-groups \
  --filters "Name=vpc-id,Values=YOUR_VPC_ID" \
  --query 'SecurityGroups[?contains(GroupName, `GuardDuty`)].[GroupId,GroupName]' \
  --output table
```

#### 2. Delete GuardDuty VPC Endpoint

```bash
aws ec2 delete-vpc-endpoints --vpc-endpoint-ids vpce-xxxxx
```

#### 3. Delete GuardDuty Security Group

```bash
aws ec2 delete-security-group --group-id sg-xxxxx
```

#### 4. Retry Stack Deletion

```bash
nx destroy openchallenges-infra-cdk:dev --force
```

## Related AWS Documentation

- [GuardDuty Runtime Monitoring for ECS](https://docs.aws.amazon.com/guardduty/latest/ug/runtime-monitoring-ecs.html)
- [VPC Endpoints for Interface Endpoints](https://docs.aws.amazon.com/vpc/latest/privatelink/create-interface-endpoint.html)
- [Troubleshooting VPC Deletion](https://docs.aws.amazon.com/vpc/latest/userguide/delete-vpc.html)

## Cost Considerations

Each VPC endpoint costs approximately **$7.30/month** ($0.01/hour) plus data processing charges.

For the GuardDuty VPC endpoint:

- **Base cost**: ~$7.30/month per AZ
- **Data processing**: $0.01/GB (first 1 PB/month)
- **Total for 2 AZs**: ~$14.60/month + data transfer

This is automatically included when using GuardDuty Runtime Monitoring for ECS.
