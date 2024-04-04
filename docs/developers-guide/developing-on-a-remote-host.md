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
the compute resources of a remote host. More specifically, you will learn how to:

- create an EC2 instance from Sage Service Catalog
- connect to the EC2 instance with VS Code

## Use case

To illustrate the benefit of developing on a remote host, this table summarizes the local compute
resources available to the developers of OpenChallenges in 2023. The same information is displayed
for two types of Amazon EC2 instances and one type of GitHub Codespace instance that were selected
as candidate alternative development environments for the team members. The table also includes the
runtimes in seconds of different tasks such as linting or testing all the projects included in the
monorepo (the method used to generate these results is described in the next section).

|                                                        | Shirou       | Rin          | Sakura       | m5.2xlarge   | t3a.xlarge   | 4-core Codespace | 8-core Codespace |
| ------------------------------------------------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ---------------- | ---------------- |
| Computer Type                                          | Desktop PC   | MacBook Pro  | MacBook Pro  | Amazon EC2   | Amazon EC2   | GitHub Codespace | GitHub Codespace |
| Architecture                                           | 64-bit (x86) | 64-bit (x86) | 64-bit (x86) | 64-bit (x86) | 64-bit (x86) | 64-bit (x86)     | 64-bit (x86)     |
| CPU Count                                              | 8            | 4            | 4            | 8            | 4            | 4                | 8                |
| CPU Frequency (GHz)                                    | 3.6          | 2.4          | 1.7          | 2.5          | 2.2          | 2.7              | 2.8              |
| Memory (GB)                                            | 32           | 16           | 16           | 32           | 16           | 8                | 16               |
| Runtime: Lint All Projects (s)                         | 15.4         | 208.9        | 183.8        | 18.6         | 33.4         | 24.6             | 16.9             |
| Runtime: Build All Projects (s)                        | 19.4         | 196.2        | 162.2        | 26.7         | 44.9         | 32.3             | 14.1             |
| Runtime: Test All Projects (s)                         | 12.4         | 117.1        | 82.8         | 15.3         | 29.2         | 31.6             | 24.5             |
| Runtime: Test api (s)                                  | 6.2          | 29.6         | 21.3         | 7.2          | 10.4         | 6.5              | 6.5              |
| Runtime: Test web-app (s)                              | 5.3          | 43.0         | 35.0         | 6.5          | 9.2          | 6.7              | 6.0              |
| Download speed (Mbit/s)                                | 395.9        | 52.1         | 160.1        | 2165.0       | 1606.7       | 8571             | 8603             |
| Upload speed (Mbit/s)                                  | 183.3        | 15.6         | 10.3         | 1861.0       | 1030.2       | 4893             | 5125             |
| On-Demand Cost ($/day)                                 | n/a          | n/a          | n/a          | 9.2          | 3.6          | 8.64 (1,2)       | 17.28 (1,2)      |
| On-Demand Cost ($/year)                                | n/a          | n/a          | n/a          | 3363.8       | 1317.5       | 3153.6 (1,2)     | 6307.2 (1,2)     |

(1) GitHub codespaces stop automatically after 1h of inactivity. A codespace used by an full-time
engineer (8h/day) - without taking into account vacation for the sake of simplicity - would cost 8
hours/day * 5 days/week * 52 weeks * $0.36/hour (4-core) = $748/year (see [Codespaces pricing]).
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

|                                                        | Shirou       | Rin          | Sakura       | m5.2xlarge   | t3a.xlarge   |
| ------------------------------------------------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
| Runtime: Lint All Projects                             | 13.6         | 1.0          | 1.1          | 11.2         | 6.3          |
| Runtime: Build All Projects                            | 10.1         | 1.0          | 1.2          | 7.3          | 4.4          |
| Runtime: Test All Projects                             | 9.4          | 1.0          | 1.4          | 7.6          | 4.0          |
| Runtime: Test api                                      | 4.8          | 1.0          | 1.4          | 4.1          | 2.8          |
| Runtime: Test web-app                                  | 8.0          | 1.0          | 1.2          | 6.6          | 4.6          |
| Download speed                                         | 7.6          | 1.0          | 3.1          | 41.5         | 30.8         |
| Upload speed                                           | 17.8         | 1.5          | 1.0          | 180.5        | 99.9         |

For example, linting all the projects of this monorepo is 13.6 times faster on Shirou's computer
than on Rin's. Moreover, all the developers can benefit from improved download speeds (up to 41.5
faster for Rin) and upload speeds (up to 180.5 times faster for Sakura) when developing on an EC2
instance. This table illustrates well the diversity in compute resources available locally to
developers, and how relying on remote hosts like EC2 instances can provide a better working
environment to developers.

### Data collection

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

## Preparing the remote host (AWS EC2)

This section describes how to instantiate an AWS EC2 as the remote host. Steps outlined below will
assume you have access to the Sage AWS Service Catalog.

### Creating the EC2 instance

- Log in to the [Service Catalog](https://sc.sageit.org) with your Synapse credentials.
- From the list of Products, select **EC2: Linux Docker**. On the Product page, click on **Launch
product** in the upper-right corner.
- On the next page, fill out the wizard as follows:
  - **Provisioned product name**
    - Name: `{GitHub username}-devcontainers-{yyyymmdd}`
    - Example: `tschaffter-devcontainers-20240404`
  - **Parameters**:
    - EC2 Instance Type: `t3a.2xlarge`
    - Base Image: `AmazonLinuxDocker` (leave default)
    - Disk Size: 80
  - **Manage tags**:
    - `CostCenter`: Select the Cost Center associated to your project
  - **Enable event notifications**: SKIP - DO NOT MODIFY
- Click on **Launch product**. Your instance will take anywhere between 3-5 minutes to deploy.  You
can either wait on this page until "EC2Instance" shows up on the list under Resources, or you can
leave and come back at a later time.

## References