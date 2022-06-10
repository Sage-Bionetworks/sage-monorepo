# Develop on a remote host

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

## Use case

This table summarizes the local compute resources available to the developers of
the challenge registry. The same information is displayed for two types of
Amazon EC2 instances that were selected as candidate alternative development
environments for the team members. The table also includes the runtimes in
seconds of different tasks such as linting or testing all the projects included
in the monorepo (the method used to generate these results is described in the
next section).

|                                                        | Shirou       | Rin          | Sakura       | m5.2xlarge   | t3a.xlarge   |
| ------------------------------------------------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
| Computer Type                                          | Desktop PC   | MacBook Pro  | MacBook Pro  | Amazon EC2   | Amazon EC2   |
| Architecture                                           | 64-bit (x86) | 64-bit (x86) | 64-bit (x86) | 64-bit (x86) | 64-bit (x86) |
| CPU Count                                              | 8            | 4            | 4            | 8            | 4            |
| CPU Frequency (GHz)                                    | 3.6          | 2.4          | 1.7          | 2.5          | 2.2          |
| Memory (GB)                                            | 32           | 16           | 16           | 32           | 16           |
| Runtime: Lint All Projects (s)                         | 15.4         | 208.9        | 183.8        | 18.6         | 33.4         |
| Runtime: Build All Projects (s)                        | 19.4         | 196.2        | 162.2        | 26.7         | 44.9         |
| Runtime: Test All Projects (s)                         | 12.4         | 117.1        | 82.8         | 15.3         | 29.2         |
| Runtime: Test api (s)                                  | 6.2          | 29.6         | 21.3         | 7.2          | 10.4         |
| Runtime: Test web-app (s)                              | 5.3          | 43.0         | 35.0         | 6.5          | 9.2          |
| Download speed (Mbit/s)                                | 395.9        | 52.1         | 160.1        | 2165.0       | 1606.7       |
| Upload speed (Mbit/s)                                  | 183.3        | 15.6         | 10.3         | 1861.0       | 1030.2       |
| On-Demand Cost ($/day)                                 | n/a          | n/a          | n/a          | 9.2          | 3.6          |
| On-Demand Cost ($/year)                                | n/a          | n/a          | n/a          | 3363.8       | 1317.5       |

Note that developers have been asked to measure runtimes and internet speeds
while keeping open the applications that are usually running when they develop
(e.g. Spotify, several instances of VS Code, browser with many tabs open). This
could be one reason why runtimes reported by a developer are larger that those
reported by another developer who has less compute resources available.

The table below shows the number of times a task is faster than the slowest
runtime (denoted by "1.0").

|                                                        | Shirou       | Rin       | Sakura         | m5.2xlarge   | t3a.xlarge   |
| ------------------------------------------------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
| Runtime: Lint All Projects  | 13.6         | 1.0          | 1.1          | 11.2         | 6.3          |
| Runtime: Build All Projects | 10.1         | 1.0          | 1.2          | 7.3          | 4.4          |
| Runtime: Test All Projects  | 9.4          | 1.0          | 1.4          | 7.6          | 4.0          |
| Runtime: Test api           | 4.8          | 1.0          | 1.4          | 4.1          | 2.8          |
| Runtime: Test web-app       | 8.0          | 1.0          | 1.2          | 6.6          | 4.6          |
| Download speed              | 7.6          | 1.0          | 3.1          | 41.5         | 30.8         |
| Upload speed                | 17.8         | 1.5          | 1.0          | 180.5        | 99.9         |

For example, linting all the projects of this monorepo is 13.6 times faster on
Shirou's computer than on Rin's. Moreover, all the developers can benefit from
improved download speeds (up to 41.5 faster for Rin) and upload speeds (up to
180.5 times faster for Sakura) when developing on an EC2 instance. This table
illustrates well the diversity in compute resources available locally to
developers, and how relying on remote hosts like EC2 instances can provide a
better working environment to developers.

### Data collection

- Runtimes are obtained from [this commit](https://github.com/Sage-Bionetworks/challenge-registry/tree/25f2292388d9e71bf46ba137aa530aefb571deab).
- Identification of the compute resources.
  ```console
  $ nproc
  $ cat /proc/cpuinfo
  $ cat /proc/meminfo
  ```
- Runtimes are averaged over 10 runs that follow a warmup run using [hyperfine](https://github.com/sharkdp/hyperfine).
  ```console
  $ hyperfine --warmup 1 --runs 10 'nx run-many --all --target=lint --skip-nx-cache'
  $ hyperfine --warmup 1 --runs 10 'nx run-many --all --target=test --skip-nx-cache'
  $ hyperfine --warmup 1 --runs 10 'nx run-many --all --target=build --skip-nx-cache'
  $ hyperfine --warmup 1 --runs 10 'nx test api --skip-nx-cache'
  $ hyperfine --warmup 1 --runs 10 'nx test web-ui --skip-nx-cache'
  ```
- Internet speeds are measured with [speedtest-cli](https://www.speedtest.net/apps/cli).
  ```console
  $ speedtest
  ```

## Preparing the remote host

This section describes how to instantiate an AWS EC2 as the remote host.  Steps
outlined below will assume you have access to the Sage AWS Service Catalog.

### On the Service Catalog Portal

- Log in to the [Service Catalog](sc.sageit.org) with your Synapse credentials.
- From the list of Products, select **EC2: Linux Docker**. On the Product page,
click on **Launch product** in the upper-right corner.
- On the next page, fill out the wizard as follows:
  - **Provisioned product name**
    - Name: `<GitHub username>-devcontainers`
  - **Parameters**:
    - EC2 Instance Type: `t3a.xlarge`
    - Base Image: `AmazonLinuxDocker` (leave default)
    - Disk Size: 50
  - **Manage tags**:
    - `Department`: `IBC` or `CNB` (selected from [this list](https://github.com/Sage-Bionetworks-IT/organizations-infra/blob/master/sceptre/scipool/sc-tag-options/internal/Departments.json))
    - `Project`: `challenge` (selected from [this list](https://github.com/Sage-Bionetworks-IT/organizations-infra/blob/master/sceptre/scipool/sc-tag-options/internal/Projects.json))
    - `CostCenter`: `NIH-ITCR / 101600` (selected from [these lists](https://github.com/Sage-Bionetworks/aws-infra/tree/master/templates/tags))
  - **Enable event notifications**: SKIP - DO NOT MODIFY
- Click on **Launch product**. Your instance will take anywhere between 3-5
minutes to deploy.  You can either wait on this page until "EC2Instance" shows
up on the list under Resources, or you can leave and come back at a later time.

### On your local host

#### Note:
**If this is your first time connecting to an instance,** you will first need to
set up access to the EC2 instances with the AWS Systems Manager (SSM).  Follow
the instructions under:
  - [**Create a Synapse personal access token**](https://help.sc.sageit.org/sc/Service-Catalog-Provisioning.938836322.html#ServiceCatalogProvisioning-CreateaSynapsepersonalaccesstoken)
  - [**SSM access to an Instance**](https://help.sc.sageit.org/sc/Service-Catalog-Provisioning.938836322.html#ServiceCatalogProvisioning-SSMaccesstoanInstance)

(Don't worry, you will only need to do this once!)

- Navigate to the Provisioned products page for your instance.  Under **Events**,
copy the `EC2InstancePrivateIpAddress`
- In your terminal, connect to your instance following the
[**Connecting to an Instance - SSM with SSH**](https://help.sc.sageit.org/sc/Service-Catalog-Provisioning.938836322.html#ServiceCatalogProvisioning-SSMwithSSH)
instructions from the Service Catalog Provisioning.
- Once you can successfully login through SSM with SSH, exit the instance and
return to your local `.ssh/config`. Add the following:
   ```console
   Host devcontainers
       HostName <private_ip>
       User ec2-user
       IdentityFile ~/.ssh/id_rsa
   ```
- Connect to [Sage VPN](https://sagebionetworks.jira.com/wiki/spaces/IT/pages/1705246745/AWS+Client+VPN+User+Guide)
- SSH to the instance
   ```console
   ssh devcontainers
   ```

### On the EC2 instance

- Update system packages
   ```console
   sudo yum update -y
   ```
- Docker should already be readily available on the instance. Verify this
by running any Docker command, e.g.
   ```console
   docker --version
   ```
- Clone your fork in the home directory
- Follow the [**Storing GitHub credentials on the EC2 instance**](https://sagebionetworks.jira.com/wiki/spaces/APGD/pages/2590244872/Service+Catalog+Instance+Setup#Storing-GitHub-credentials-on-the-EC2-instance) instructions to config your GitHub account

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
the address `http://localhost` even though they are running on the remote host!

Accessing the apps and services using the IP address of the remote host won't
work, unless you replace the feature `docker-in-docker` by `docker-from-docker`.
In this case, `http://localhost` can no longer be used to access the apps and
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
