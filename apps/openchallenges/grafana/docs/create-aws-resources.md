# Create AWS Resources

> **Note** The following resources have already been created for the OpenChallenges development
> team.

- S3 bucket `openchallenges-grafana`
- IAM policy `openchallenges-grafana` with write access to the S3 bucket
- IAM user `openchallenges-grafana` with the above policy

IAM policy:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:ListBucket", "s3:GetObject", "s3:GetObject*"],
      "Resource": ["arn:aws:s3:::openchallenges-grafana", "arn:aws:s3:::openchallenges-grafana/*"]
    },
    {
      "Effect": "Allow",
      "Action": "s3:*Object",
      "Resource": ["arn:aws:s3:::openchallenges-grafana/*"]
    }
  ]
}
```
