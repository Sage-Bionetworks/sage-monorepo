# OpenChallenges Infra

## Overview

This project describe how to deploy the OpenChallenges stack with [Terraform CDK].

## Preparation

### Create an IAM user for deploying the stack

1. Create the IAM user `OpenChallenges-GitHubActionsUser`

TODO: Document the roles that this user should have

### Set AWS configuration

Run this command to quickly set your credentials, Region, and output format. The following example
shows sample credentials that should be replaced.

```console
$ aws configure
AWS Access Key ID [None]: AKIAIOSFODNN7EXAMPLE
AWS Secret Access Key [None]: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
Default region name [None]: us-east-1
Default output format [None]
```

### Generate an SSH key for accessing the EC2 instance

Generate the SSH key for connecting to the EC2 instance that we will create as part of the stack.

```console
ssh-keygen -t ed25519 -C "your_email@example.com" -f ~/.ssh/openchallenges-ec2 -N ''
```

## Usage

### Deploy the stack

```console
cdktf deploy
```

### Connect to the EC2 instance

First, check that the security group of the EC2 instance to make sure that it access inbound SSH
traffic.

Connect to the instance using the private key:

```console
$ ssh -i ~/.ssh/openchallenges-ec2 ubuntu@44.206.227.254
```

### Destroy the stack

From this project folder:

```console
cdktf destroy
```

## References

- [DK for Terraform]

<!-- Links -->

[Terraform CDK]: https://developer.hashicorp.com/terraform/cdktf

[DK for Terraform]: https://developer.hashicorp.com/terraform/cdktf