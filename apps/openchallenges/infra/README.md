# OpenChallenges Infra

## Overview

This project describe how to deploy the OpenChallenges stack with [Terraform CDK].

## Preparation

### Create an IAM user for deploying the stack

1. Create the IAM user `OpenChallenges-GitHubActionsUser`

TODO: Document the roles that this user should have

### Set AWS configuration

The configuration differs depending on whether the stack is deployed from the local environment or
from a TF cloud environment.

> **Note** This project is currently configured to deploy the stack using TF cloud backend.

#### When using TF local backend

TF reads the local AWS credentials when deploying the stack with TF local backend. Stack state files
will be stored locally.

Run this command to quickly set your credentials, Region, and output format. The following example
shows sample credentials that should be replaced.

```console
$ aws configure --profile cdktf
AWS Access Key ID [None]: AKIAIOSFODNN7EXAMPLE
AWS Secret Access Key [None]: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
Default region name [None]: us-east-1
Default output format [None]
```

#### When using TF cloud backend

When using TF cloud backend, the deployment of the stacks will be executed from a cloud environment
owned by TF. The AWS credentials must be available to this environment so that it can deploys the
stack with the AWS provider.

1. Create the TF Cloud workspace (currently hard-coded in `main.ts`: `openchallenges-test`)
    ```
    cdktf plan
    ```
2. Login to your TF Cloud account
3. Select the workspace
4. Click on "Variables"
5. Create the variable `AWS_ACCESS_KEY_ID`
    - Click on "Add variable"
    - Category: `Environment variable`
    - Key: `AWS_ACCESS_KEY_ID`
    - Value: `YOUR_ACCESS_KEY_ID`
    - Sensitive: Yes
    - Click on "Add variable"
6. Repeat the same process to create the variable `AWS_SECRET_ACCESS_KEY`

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

Connect to the instance using the private key. The IP address shown below should be replaced with
the address returned by Terraform at the end of the deployment process.

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