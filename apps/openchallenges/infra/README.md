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
TF_VAR_hello='Hello' cdktf deploy openchallenges-preview
```

### Connect to the Bastion with AWS SSM

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

> **Note** An interactive way to create this profile is to run `aws configure sso`.

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

Add a new profile to the `~/.ssh/config`. Replace `<instance id>` with the ID of the instance.

```
Host oc-bastion
  HostName <instance id>
  User ubuntu
  IdentityFile ~/.ssh/openchallenges-ec2
  ProxyCommand sh -c "AWS_PROFILE=cnb-dev aws ssm start-session --target %h --document-name AWS-StartSSHSession --parameters 'portNumber=%p'"
```

Connect to the bastion with SSM:

```console
ssh oc-bastion
```

### Connect to the Preview instance

Connect to the preview instance from the bastion:

```console
ssh -i ~/.ssh/openchallenges-ec2 ubuntu@<preview instance private ip>
```

### Check that the container images are available

The images are built using the dev container shown below. This container is then automatically
removed once the images have been built. The example below shows that the container is still
running and that more time is needed to build the images.

```console
$ docker ps --format 'table {{.Names}}\t{{.Status}}'
sage_devcontainer   Up 14 minutes
```

### Configure the OC containers

The following configuration must be changed in the private GitHub repo that hosts the remote config
files.

- `openchallenges-image-service-development.yml`
  - Set `openchallenges-image-service.thumbor-host` to `<alb_dns_name>/img/`

The config files of the stack components (`.env`) have been generated during the creation of the
instance from their example config files (`.env.example`). The config of the following components
need to be updated before deploying the stack with Docker Compose.

- `apps/openchallenges/app/.env`
  - Set `API_URL` to `<alb_dns_name>/api/v1`
- `apps/openchallenges/config-server/.env`
  - Set `GIT_DEFAULT_LABEL` to `private-preview` or any other branches that includes the config
    needed
  - Set `GIT_PRIVATE_KEY` (see the README of the Config Server)
- `apps/openchallenges/thumbor/.env`
  - Use the config example `.env.example.aws`
  - Set `AWS_LOADER_BUCKET_NAME` to the name of the S3 bucket
  - Set `AWS_LOADER_S3_ACCESS_KEY_ID` and `AWS_LOADER_S3_SECRET_ACCESS_KEY` to give Thumbor read
    access to the S3 bucket
  - Review the other parameters, e.g. `AWS_LOADER_REGION_NAME` and `AWS_LOADER_S3_ENDPOINT_URL` that
    depends on the region where the S3 bucket live

### Deploy the OC containers

The following requirements applies when running Elasticsearch in Docker in production.

```console
sudo sysctl -w vm.max_map_count=262144
```

Step into the sage-monorepo folder.

```console
cd ~/sage-monorepo
```

Run the command below to start the OC containers.

```console
$ OPENCHALLENGES_VERSION=edge ./docker/openchallenges/serve-detach.sh openchallenges-apex
[+] Building 0.0s (0/0)
[+] Running 15/15
 ✔ Container openchallenges-zipkin                Healthy                                                        67.0s
 ✔ Container openchallenges-elasticsearch-node-3  Healthy                                                         0.5s
 ✔ Container openchallenges-mariadb               Healthy                                                         1.5s
 ✔ Container openchallenges-config-server         Healthy                                                         1.5s
 ✔ Container openchallenges-service-registry      Healthy                                                         1.5s
 ✔ Container openchallenges-thumbor               Healthy                                                         1.5s
 ✔ Container openchallenges-api-gateway           Healthy                                                        67.0s
 ✔ Container openchallenges-image-service         Healthy                                                        22.3s
 ✔ Container openchallenges-elasticsearch-node-2  Healthy                                                         0.5s
 ✔ Container openchallenges-elasticsearch         Healthy                                                        20.5s
 ✔ Container openchallenges-challenge-service     Started                                                         0.7s
 ✔ Container openchallenges-organization-service  Healthy                                                        44.7s
 ✔ Container openchallenges-app                   Started                                                         0.7s
 ✔ Container openchallenges-apex                  Started
```

> **Note** If the above command is stuck on containers that report the status `Error`, try stopping
> the command and run it again.

### Manually build the OC images

Step into the repo folder:

```console
cd ~/sage-monorepo
```

Stash the changes:

```console
git stash
```

Checkout a different version from `main`:

```console
git fetch
git checkout <commit_id>
```

Set the dev container definition file to use `docker-outside-of-docker`:

```console
./tools/switch-devcontainer-to-docker-outside-of-docker.sh
```

Stop the containers that are running:

```console
docker stop $(docker ps -aq)
docker system prune
```

Step outside of the repo folder, start the dev container, build the OC images and stop the dev
container:

```console
# Step outside of the repository
cd ..

# Start the dev container
devcontainer up --workspace-folder sage-monorepo

# Build the images
devcontainer exec --workspace-folder sage-monorepo bash -c \
  ". ./dev-env.sh \
  && workspace-install \
  && openchallenges-build-images"

# Remove the dev container
docker rm -f sage_devcontainer
```

### Destroy the stack

From this project folder:

```console
cdktf destroy
```

## FAQ

### Error: Required plugins are not installed

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
