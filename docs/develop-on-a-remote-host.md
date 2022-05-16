# Develop on a remote Docker host

## Introduction

Team members who develop locally may not benefit from the same compute
resources. The most notable resources that can impact the productivity of
developers are the number and frequency of the CPU cores, the memory available
and internet speed. The worse case is when a machine does not have the resources
to run the apps that the team develops, for example when not enough memory is
available. On other times, the time required to complete a task may be many
times slower on a computer with lower CPU resources.

Working remotely means that developers no longer benefit from the same internet
speed, either because of the quality of the internet connection available at
their location or because the speed is shared among the members of a household.
As a result, tasks that involve downloading or uploading artifacts, like pulling
or pushing Docker images, may take significantly longer to complete.

This page describes how to setup a development environment that enables
developers to use VS Code while using the compute resources of a remote host.
The developers start by creating identical EC2 instances before [connecting to
them with VS
Code](https://code.visualstudio.com/remote/advancedcontainers/develop-remote-host).
This SOP enables developers to continue working [inside the
devcontainer](#devcontainer) provided with this project, hence further
contributing to the standardization of the development envrionment.

## Preparing the remote host

This section describes how to instantiate an AWS EC2 as the remote host.

### Login into the AWS Management Console

- Login into [Sage JumpCloud user console](https://console.jumpcloud.com/userconsole#/)
- `Applications` > Select `aws-sso-organization`
- Select the AWS account `org-sagebase-sandbox`
- Click on `Management console` for the role `Developer`

### In AWS Management Console

- Instantiate EC2 instance
  - Name and tags
    - Name: `<GitHub username>-devcontainers`
    - Tags:
      - `Department`: `IBC` or `CNB` (selected from [this list](https://github.com/Sage-Bionetworks-IT/organizations-infra/blob/master/sceptre/scipool/sc-tag-options/internal/Departments.json))
      - `Project`: `challenge` (selected from [this list](https://github.com/Sage-Bionetworks-IT/organizations-infra/blob/master/sceptre/scipool/sc-tag-options/internal/Projects.json))
      - `OwnerEmail`: `<your email address>`
      - `CostCenter`: `NIH-ITCR / 101600` (selected from [these lists](https://github.com/Sage-Bionetworks/aws-infra/tree/master/templates/tags))
  - Application and OS Images (Amazon Machine Image)
    - Name: `Ubuntu Server 22.04 LTS (HVM), SSD Volume Type`
    - Architecture: `64-bit (x86)`
    - AMI ID: `ami-09d56f8956ab235b3`
  - Instance type
    - Instance type: `t3a.xlarge`
  - Network settings
    - Click on `Edit`
    - VPC: `vpc-0e9b80dc470a797d5 (sandcastlevpc)`
    - Subnet: `subnet-025c297e427e44daf (Sandcastle-Private1)`
    - Firewall
      - Click on `Select existing security group`
      - Select security group `challenge-registry-devcontainers (sg-03b891e56d6a1e851)`
  - Configure storage
    - 1x 50 GB (gp2)
- Identify the Private IPv4 addresses of the EC2 once it has started

### On your local host

- Add a host profile to your local `.ssh/config`
   ```console
   Host devcontainers
       HostName <private_ip>
       User ubuntu
       IdentityFile ~/.ssh/tschaffter-sandbox.pem
   ```
- Connect to [Sage VPN](https://sagebionetworks.jira.com/wiki/spaces/IT/pages/1705246745/AWS+Client+VPN+User+Guide)
- SSH to the instance
   ```console
   ssh devcontainers
   ```

### On the EC2 instance

- Update system packages
   ```console
   sudo apt update && sudo apt upgrade -y
   ```
- [Install Docker Engine on Ubuntu](https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository)
- Add your user account to docker group.
   ```console
   sudo usermod -aG docker ${USER}
   ```
- To apply the new group membership, log out of the server and back in. Run the
  command `groups` to check that your user is a member of the group `docker`.
- Verify that Docker Engine is installed correctly by running the hello-world
  image.
   ```console
   docker run --rm hello-world
   ```
- Clone your fork in the home directory

### In VS Code

- Install the extension `Remote - SSH` and `Remote - Containers`.
- `Remote-SSH: Connect to Host...` > Select the host.
- Verify that the bottom-left corner of the VSCode window shows `SSH: <host
  name>` upon successfully connecting to the remote instance.

  <img src="images/vscode-remote-ssh-button.png" height="24">

- `Remote-Containers: Open Folder in Container...`
- Select the project folder and click on `OK`.
- Verify that the bottom-left corner of the VSCode window shows `Dev Container:
  Challenge Registry @ ssh://<host name>`.

  <img src="images/vscode-remote-ssh-devcontainer-button.png" height="58">

Congratulations, you are now ready to develop in the devcontainer that runs on
the EC2 instance! 🚀

## Accessing apps and services

The devcontainer provided with this project uses the VS Code devcontainer
feature `docker-in-docker`. In addition to isolating the Docker engine running
in the devcontainer from the engine running on the host, this feature enables VS
Code to forward the ports defined in `devcontainer.json` to the local
envrionment of the developer. Therefore, apps and services can be accessed using
the address `localhost` even though they are running on the remote host!

Accessing the apps and services using the IP address of the remote host won't
work, unless you replace the feature `docker-in-docker` by `docker-from-docker`.
In this case, `localhost` can not longer be used to access the apps and
services.

## Uploading files

Simply drag and drop files to the VS Code explorer to upload files from your
local environment to the remote host.

## Closing the remote connection

Click on the button in the bottom-left corner of VS Code and select one of these
options:

- `Close Remote Connection` to close the connection with the remote host.
- `Reopen Folder in SSH` if you want to stop the devcontainer but stay connected
  to the remote host.
