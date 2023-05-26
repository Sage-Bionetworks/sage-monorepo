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

1. Create the TF Cloud workspace by running the following command (currently hard-coded in
   `main.ts`: `openchallenges-test`).
    ```
    cdktf diff
    ```
2. Login to your TF Cloud account
3. Select the workspace
4. [Add a variable] named `AWS_ACCESS_KEY_ID` (sensitive, environment variable).
5. [Add a variable] named `AWS_SECRET_ACCESS_KEY` (sensitive, environment variable).

### Generate an SSH key for accessing the EC2 instance

Generate the SSH key for connecting to the EC2 instance that we will create as part of the stack.

```console
ssh-keygen -t ed25519 -C "your_email@example.com" -f ~/.ssh/openchallenges-ec2 -N ''
```

### Login into Terraform

Enter this command and follow the instructions to authenticate with Terraform.

```console
terraform login
```

## Usage

### Generate the Terraform configuration file

```console
cdktf synth
```

### Deploy the stack

```console
cdktf deploy
```

### Connect to the Bastion

#### With AWS SSM

Add the following profile to `~/.aws/config`.

```ini
[profile cnb-dev]
region = us-east-1
sso_start_url = https://d-906769aa66.awsapps.com/start
sso_region = us-east-1
sso_account_id = 384625883722
sso_role_name = Administrator
output = json
```

Login to AWS SSO.

```console
aws --profile cnb-dev sso login
```

A browser window will pop up, asking for access from an application. Click `Allow`. You are now
authenticated for remote access for the next 8-10 hours. Re-login as necessary.

Connect to the instance as `ubuntu`, passing the `Instance ID` to `--target`.

```console
aws ssm start-session \
  --profile cnb-dev \
  --document-name AWS-StartInteractiveCommand \
  --parameters command="sudo su - ubuntu" \
  --target <instance id>
```

#### With SSH (deprecated)

Connect to the bastion using the private key. The public IP address shown below should be replaced
with the address returned by Terraform at the end of the deployment process.

```console
ssh -i ~/.ssh/openchallenges-ec2 ubuntu@<bastion public ip>
```

### Connect to the Preview instance

Connect to the preview instance from the bastion:

```console
ssh -i ~/.ssh/openchallenges-ec2 ubuntu@<preview instance private ip>
```

### Destroy the stack

From this project folder:

```console
cdktf destroy
```

## FAQ

### Error: Required plugins are not installe

CDKTF may throw the following error when attempting to deploy the stack from a fresh development
environment.

```console
$ cdktf apply
2023-05-16 23:41:22 info: Welcome to the deployment of the OpenChallenges stack.

openchallenges-stack  ╷
                      │ Error: Required plugins are not installed
                      │
                      │ The installed provider plugins are not consistent with the packages selected
                      │ in the dependency lock file:
                      │   - registry.terraform.io/hashicorp/aws: there is no package for registry.terraform.io/hashicorp/aws 4.65.0 cached in .terraform/providers
                      │
                      │ Terraform uses external plugins to integrate with a variety of different
                      │ infrastructure services. To download the plugins required for this
                      │ configuration, run:
                      │   terraform init
```

Removing the directory `cdktf.out` and running `cdktf apply` again should solve this issue.

## References

- [DK for Terraform]

<!-- Links -->

[Terraform CDK]: https://developer.hashicorp.com/terraform/cdktf

[DK for Terraform]: https://developer.hashicorp.com/terraform/cdktf
[Add a variable]: https://developer.hashicorp.com/terraform/cloud-docs/workspaces/variables/managing-variables#add-a-variable
