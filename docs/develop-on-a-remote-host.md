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

## Use case

This table summarizes the local compute resources available to the developers of
the challenge registry. The same information is displayed for two types of EC2
instances that were selected as candidates as alternative development
environment for the team members. The table also includes the runtimes in
seconds of different tasks such as linting or testing all the projects included
in the monorepo (the method used to generate these results is described in the
next section).

|                                                        | Thomas       | Verena       | Rong         | m5.2xlarge   | t3a.xlarge   |
| ------------------------------------------------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
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
| On-Demand Cost ($/day)                                 | n/a            | n/a            | n/a            | 9.2          | 3.6          |
| On-Demand Cost ($/year)                                | n/a            | n/a            | n/a            | 3363.8       | 1317.5       |

The table below shows the number of times a task is faster than the slowest
runtime (denoted by "1.0").

|                                                        | Thomas       | Verena       | Rong         | m5.2xlarge   | t3a.xlarge   |
| ------------------------------------------------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
| Runtime: Lint All Projects  | 13.6         | 1.0          | 1.1          | 11.2         | 6.3          |
| Runtime: Build All Projects | 10.1         | 1.0          | 1.2          | 7.3          | 4.4          |
| Runtime: Test All Projects  | 9.4          | 1.0          | 1.4          | 7.6          | 4.0          |
| Runtime: Test api           | 4.8          | 1.0          | 1.4          | 4.1          | 2.8          |
| Runtime: Test web-app       | 8.0          | 1.0          | 1.2          | 6.6          | 4.6          |
| Download speed              | 7.6          | 1.0          | 3.1          | 41.5         | 30.8         |
| Upload speed                | 17.8         | 1.5          | 1.0          | 180.5        | 99.9         |

For example, linting all the projects of this monorepo is 13.6 times faster on
Thomas' computer than on Verena's. Note that developers have been asked to
measure runtimes and internet speeds while keeping open the applications that
are usually running when they develop (e.g. Spotify, several instances of VS
Code, browser with many tabs open). This could be one reason why runtimes
reported a developers are larger that those reported by another despite having
higher compute resources available. 

Moreover, all the developers can benefit from improved download speeds (up to
41.5 faster for Verena) and upload speeds (up to 180 times faster for Rong) when
developing on an EC2 instance.

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
In this case, `localhost` can no longer be used to access the apps and services.

## Uploading files

Simply drag and drop files to the VS Code explorer to upload files from your
local environment to the remote host.

## Closing the remote connection

Click on the button in the bottom-left corner of VS Code and select one of these
options:

- `Close Remote Connection` to close the connection with the remote host.
- `Reopen Folder in SSH` if you want to stop the devcontainer but stay connected
  to the remote host.
