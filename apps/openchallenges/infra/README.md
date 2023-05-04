# OpenChallenges Infra

## Overview

This project describe how to deploy the OpenChallenges stack with [Terraform CDK].

## Preparation

### Create an IAM user for deploying the stack

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

## Usage

### Deploy the stack

```console
cdktf deploy
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