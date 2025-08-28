# Developing on a remote host

## Introduction

Team members who develop locally may not benefit from the same compute resources. The most notable
resources that can impact the productivity of developers are the number and frequency of the CPU
cores, the memory available and internet speed. The worse case is when a machine does not have the
resources to run the apps that the team develops, for example when not enough memory is available.
On other times, the time required to complete a task may be many times slower on a computer with
lower CPU resources.

Moreover, working remotely means that developers no longer benefit from the same internet speed,
either because of the quality of the internet connection available at their location or because the
speed is shared among the members of a household. As a result, tasks that involve downloading or
uploading artifacts, like pulling or pushing Docker images, may take significantly longer to
complete.

This page describes how to setup a environment that enables developers to use VS Code while using
the compute resources of a remote host.

## Motivation

To illustrate the benefit of developing on a remote host, this table summarizes the local compute
resources available to the developers of OpenChallenges in 2023. The same information is displayed
for two types of Amazon EC2 instances and one type of GitHub Codespace instance that were selected
as candidate alternative development environments for the team members. The table also includes the
runtimes in seconds of different tasks such as linting or testing all the projects included in the
monorepo (the method used to generate these results is described in the next section).

|                                 | Shirou       | Rin          | Sakura       | m5.2xlarge   | t3a.xlarge   | 4-core Codespace | 8-core Codespace |
| ------------------------------- | ------------ | ------------ | ------------ | ------------ | ------------ | ---------------- | ---------------- |
| Computer Type                   | Desktop PC   | MacBook Pro  | MacBook Pro  | Amazon EC2   | Amazon EC2   | GitHub Codespace | GitHub Codespace |
| Architecture                    | 64-bit (x86) | 64-bit (x86) | 64-bit (x86) | 64-bit (x86) | 64-bit (x86) | 64-bit (x86)     | 64-bit (x86)     |
| CPU Count                       | 8            | 4            | 4            | 8            | 4            | 4                | 8                |
| CPU Frequency (GHz)             | 3.6          | 2.4          | 1.7          | 2.5          | 2.2          | 2.7              | 2.8              |
| Memory (GB)                     | 32           | 16           | 16           | 32           | 16           | 8                | 16               |
| Runtime: Lint All Projects (s)  | 15.4         | 208.9        | 183.8        | 18.6         | 33.4         | 24.6             | 16.9             |
| Runtime: Build All Projects (s) | 19.4         | 196.2        | 162.2        | 26.7         | 44.9         | 32.3             | 14.1             |
| Runtime: Test All Projects (s)  | 12.4         | 117.1        | 82.8         | 15.3         | 29.2         | 31.6             | 24.5             |
| Runtime: Test api (s)           | 6.2          | 29.6         | 21.3         | 7.2          | 10.4         | 6.5              | 6.5              |
| Runtime: Test web-app (s)       | 5.3          | 43.0         | 35.0         | 6.5          | 9.2          | 6.7              | 6.0              |
| Download speed (Mbit/s)         | 395.9        | 52.1         | 160.1        | 2165.0       | 1606.7       | 8571             | 8603             |
| Upload speed (Mbit/s)           | 183.3        | 15.6         | 10.3         | 1861.0       | 1030.2       | 4893             | 5125             |
| On-Demand Cost ($/day)          | n/a          | n/a          | n/a          | 9.2          | 3.6          | 8.64 (1,2)       | 17.28 (1,2)      |
| On-Demand Cost ($/year)         | n/a          | n/a          | n/a          | 3363.8       | 1317.5       | 3153.6 (1,2)     | 6307.2 (1,2)     |

(1) GitHub codespaces stop automatically after 1h of inactivity. A codespace used by an full-time
engineer (8h/day) - without taking into account vacation for the sake of simplicity - would cost 8
hours/day _ 5 days/week _ 52 weeks \* $0.36/hour (4-core) = $748/year (see [Codespaces pricing]).
Similarly, the cost for an 8-core codespace would become $1496/year. In addition, GitHub bills $0.07
of GB of storage independently on whether the codespace is running or stopped. Pricing valid on
2023-12-31.

(2) GitHub offers core hours and storage. For example, a Free user can use a 2-core instance for 60
hours per month for free or an 8-core instance for 15 hours. You will be notified by email when you
have used 75%, 90%, and 100% of your included quotas.

- Free users: 120 core hours/month and 15 GB month of storage
- Pro users: 180 core hours/month and 20 GB month of storage

!!! note

    Note that developers have been asked to measure runtimes and internet speeds while keeping open the
    applications that are usually running when they develop (e.g. Spotify, several instances of VS Code,
    browser with many tabs open). This could be one reason why runtimes reported by a developer are
    larger that those reported by another developer who has less compute resources available.

The table below shows the number of times a task ran by a developer is faster than the slowest
runtime (denoted by "1.0").

|                             | Shirou | Rin | Sakura | m5.2xlarge | t3a.xlarge |
| --------------------------- | ------ | --- | ------ | ---------- | ---------- |
| Runtime: Lint All Projects  | 13.6   | 1.0 | 1.1    | 11.2       | 6.3        |
| Runtime: Build All Projects | 10.1   | 1.0 | 1.2    | 7.3        | 4.4        |
| Runtime: Test All Projects  | 9.4    | 1.0 | 1.4    | 7.6        | 4.0        |
| Runtime: Test api           | 4.8    | 1.0 | 1.4    | 4.1        | 2.8        |
| Runtime: Test web-app       | 8.0    | 1.0 | 1.2    | 6.6        | 4.6        |
| Download speed              | 7.6    | 1.0 | 3.1    | 41.5       | 30.8       |
| Upload speed                | 17.8   | 1.5 | 1.0    | 180.5      | 99.9       |

For example, linting all the projects of this monorepo is 13.6 times faster on Shirou's computer
than on Rin's. Moreover, all the developers can benefit from improved download speeds (up to 41.5
faster for Rin) and upload speeds (up to 180.5 times faster for Sakura) when developing on an EC2
instance. This table illustrates well the diversity in compute resources available locally to
developers, and how relying on remote hosts like EC2 instances can provide a better working
environment to developers.

### Collectings OS info and benchmarking tasks

Runtimes are obtained from [this
commit](https://github.com/Sage-Bionetworks/sage-monorepo/tree/25f2292388d9e71bf46ba137aa530aefb571deab).

Identification of the compute resources.

```console
$ nproc
$ cat /proc/cpuinfo
$ cat /proc/meminfo
```

Runtimes are averaged over 10 runs that follow a warmup run using
[hyperfine](https://github.com/sharkdp/hyperfine).

```console
$ hyperfine --warmup 1 --runs 10 'nx run-many --all --target=lint --skip-nx-cache'
$ hyperfine --warmup 1 --runs 10 'nx run-many --all --target=build --skip-nx-cache'
$ hyperfine --warmup 1 --runs 10 'nx run-many --all --target=test --skip-nx-cache'
$ hyperfine --warmup 1 --runs 10 'nx test api --skip-nx-cache'
$ hyperfine --warmup 1 --runs 10 'nx test web-ui --skip-nx-cache'
```

Internet speeds are measured with [speedtest-cli](https://www.speedtest.net/apps/cli).

```console
$ speedtest
```

## Preparing the remote host - AWS EC2

This section describes how to instantiate an AWS EC2 as the remote host. Steps outlined below will
assume you have access to the [Sage AWS Service Catalog](https://help.sc.sageit.org/sc/Service-Catalog-Provisioning.938836322.html).

### Creating the EC2 instance

1. Log in to the [Service Catalog](https://sc.sageit.org) with your Synapse credentials.
2. From the list of Products, select **EC2: Linux Docker**. On the Product page, click on **Launch
   product** in the upper-right corner.
3. On the next page, fill out the wizard as follows:
   - **Provisioned product name**
     - Name: `{GitHub username}-devcontainers-{yyyymmdd}`
     - Example: `tschaffter-devcontainers-20240404`
   - **Parameters**
     - EC2 Instance Type: `t3a.2xlarge`
     - Base Image: `AmazonLinuxDocker` (leave default)
     - Disk Size: 80
   - **Manage tags**
     - `CostCenter`: Select the Cost Center associated to your project
   - **Enable event notifications**: SKIP - DO NOT MODIFY
4. Click on **Launch product**. Your instance will take anywhere between 3-5 minutes to deploy. You
   can either wait on this page until "EC2Instance" shows up on the list under Resources, or you can
   leave and come back at a later time.

### Stopping the EC2 instance

It's not something you should do now as part of this tutorial. This section serves as a reminder
that AWS charges for evey hour the EC2 instance is running. As soon as you identify that you will no
longer need the instance for the rest of the day, open the Service Catalog to stop it.

1. Open the Service Catalog, then select **Provisioned products**.
2. Select the EC2 instance.
3. Click on the button **Actions** > **Service actions** > **Stop**.
4. Confirm the action.

After a few seconds, the EC2 instance will be stopped.

!!! note

    AWS still charges us for the storage space that the EC2 instance takes even when it's not running.
    Consider destroying the EC2 instance when you decide that you will no longer need it.

### Connecting to the EC2 instance with AWS Console

We will now use the AWS Console to open a terminal to the EC2 instance and setup your public SSH
key.

!!! note

    This section assumes that you already have a public and private SSH key created on your local
    machine from where you are running VS Code.

1. Open the Service Catalog, then select **Provisioned products**.
2. In the section **Resources**, click on the link for "EC2Instance".
3. Click on the checkbox of the new EC2 instance created.
4. Click on the button **Actions** > **Connect**.
   - The error "Failed to describe security groups" shown by AWS can be ignored.
5. Click on the tab **Session Manager**.
6. Click on **Connect**.

### Configuring the SSH public key on the EC2 instance

6. Login as the user `ec2-user` and move to its home directory.
   ```console
   $ sudo -s
   # su ec2-user
   $ cd
   ```
7. Create the folder `~/.ssh` (if needed).
   ```console
   $ mkdir ~/.ssh
   $ chmod 700 ~/.ssh
   ```
8. Create the file `~/.ssh/authorized_keys` (if needed).
   ```console
   $ touch ~/.ssh/authorized_keys
   $ chmod 644 ~/.ssh/authorized_keys
   ```
9. Copy and paste your public SSH key at the end of `~/.ssh/authorized_keys`.
10. Click on the button **Terminate** to terminate the session and confirm the action.

### Configuring SSH on the local machine

This section describes how to create a profile for the EC2 instance in your local `~/.ssh/config`
file.

!!! note

    This section assumes that you already have a public and private SSH key created on your local
    machine from where you are running VS Code.

First, you need to identify the private IP address of the EC2 instance.

1. Open the Service Catalog, then select **Provisioned products**.
2. In the section **Outputs**, the private IP address is the value associated to
   "EC2InstancePrivateIpAddress".

Then, on your local machine:

1. Create the file `~/.ssh/config` (if needed).
   ```console
   $ touch ~/.ssh/config
   $ chmod 600 ~/.ssh/config
   ```
2. Add the following content to your local `~/.ssh/config`.
   ```console
   Host {alias}
       HostName {private ip}
       User ec2-user
       IdentityFile {path to your private SSH key, e.g. ~/.ssh/id_rsa}
   ```
   where the placeholder values `{...}` should be replaced with the correct values.

### Connecting to the EC2 instance with VS Code

1. Connect to the [Sage VPN](https://sagebionetworks.jira.com/wiki/spaces/IT/pages/1705246745/AWS+Client+VPN+User+Guide).
2. Open VS Code.
3. Install the VS Code extension pack "Remote Development".
4. Open the command palette with `Ctrl+Shit+P`.
5. `Remote-SSH: Connect to Host...` > Select the host.
6. Answer the prompts

You are now connected to the EC2 instance! 🚀

!!! tip

    Please remember to stop the EC2 instance at the end of your working day to save on costs.

### Next

Go to the section XXX for the instructions on how to setup your environment to contribute to Sage
Monorepo.

## Preparing the remote host - GitHub Codespace

This section describes how to open your fork of Sage Monorepo in a GitHub Codespaces instance.

!!! note

    In practice, we will prefer to develop in an EC2 instance created from the Service Catalog for
    security and budget reasons. Please refer to the instructions given above. Using a GitHub Codespace
    has been proven to be ponctually useful for quick tests that require a fresh environment, as one of
    Codespaces benefits is that they can be created and destroyed faster than EC2 instances.

1. Open your browser and go to [GitHub Codespaces].
2. Click on the "New codespace".
3. Enter the information requested:
   - **Repository**: Select your fork of the monorepo
   - **Branch**: Select the default branch
   - **Dev container configuration**: Select the dev container definition
   - **Region**: Select your preferred region
   - **Machine type**: Select the machine type
4. Click on "Create codespace".
5. Wait for the codespace to be created.
6. Configure the monorepo and install its dependencies (see README).

### Stopping a Codespace instance

If your codespace is open in your browser, you can stop it with the following step. Note that a
codespace stops automatically after one hour of inactivity.

1. Click on the button "Codespaces" located in the bottom-left corner.
2. Click on "Stop Current Codespace".

### Opening a Codespace with VS Code

If you prefer to develop with VS Code rather than inside your browser:

1. Open your browser and go to [GitHub Codespaces].
2. Find the codespace that you want to open with VS Code.
3. Click on the three-dot menu > "Open in ..." > "Open in Visual Studio Code"

### Changing the machine type

The type of machine used by a codespace can be changed at any time, for example when a beefier
codespace instance is needed. To change the machine type of an existing codespace.

1. Stop the codespace.
2. Open your browser and go to [GitHub Codespaces].
3. Find the codespace that you want to open with VS Code.
4. Click on the three-dot menu > "Change machine type".
5. Update the properties of the machine and click on "Update codespace".

## Accessing apps and services

The devcontainer provided with this project uses the VS Code devcontainer feature
`docker-in-docker`. In addition to isolating the Docker engine running in the devcontainer from the
engine running on the host, this feature enables VS Code to forward the ports defined in
`devcontainer.json` to the local envrionment of the developer. Therefore, apps and services can be
accessed using the address `http://localhost` even though they are running on the remote host!

Accessing the apps and services using the IP address of the remote host won't work, unless you
replace the feature `docker-in-docker` by `docker-from-docker`. In this case, `http://localhost` can
no longer be used to access the apps and services.

## Uploading files

Simply drag and drop files to the VS Code explorer to upload files from your local environment to
the remote host.

## Closing the remote connection

Click on the button in the bottom-left corner of VS Code and select one of these options:

- `Close Remote Connection` to close the connection with the remote host.
- `Reopen Folder in SSH` if you want to stop the devcontainer but stay connected to the remote host.

<!-- Links -->

[GitHub Codespaces]: https://github.com/codespaces
[Codespaces pricing]: https://docs.github.com/en/billing/managing-billing-for-github-codespaces/about-billing-for-github-codespaces
