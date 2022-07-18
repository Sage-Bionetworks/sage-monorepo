# Collecting Challenge Resource Metrics

## Overview

- Are all the EC2 instances up?
- Is an instance at risk of crashing because its storage capacity is nearly filled up?
- Are submissions using all the CPU cores available?
- Are submissions using the GPU cores available?
- Is a submission crashing due to a memory leak (memory usage, logs)?
- Is a submission generating too much data (logs, scratch/pre-processed/output files)
- What fraction of the uptime of an EC2 instance is used to process submissions?
  - How much money could we have saved using on-demand EC2 instances?

## Setup

- 1 EC2 running the ELK stack
- N EC2s running the Beat agent

## Prepare the ELK Stach instance

- Create a GH repository from the template [deviantony/docker-elk].
  - [Sage-Bionetworks/docker-elk]
- Create EC2 instance.
  - Instance name: `<challenge name>-elk`
  - Instance type: `t2.medium`
  - Security group: same as the challenge instances
  - Memory: 4 GB
  - Storage space: 100 GB
- Create a profile for the instance in `.ssh/config` named `<challenge name>-elk`.

   ```console

   ```

- Ssh to the EC2 instance.
- Update the system packages.

   ```console
   sudo apt update
   sudo apt upgrade -y
   sudo reboot
   ```

- Install the [Docker Engine](https://docs.docker.com/engine/install/ubuntu/).
  - [Enable the non-root user to execute Docker commands](https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user).

- Start VS Code and connect via SSH to the EC2 instance.
  - Install the VS Code extension `Remote - SSH`.
  - Click on the button on the bottom-left corner of VS Code.
  - Select `Connect to Host...`.
  - Select the host `<challenge name>-elk`.

- Clone the repository [Sage-Bionetworks/docker-elk] in the home folder.
- Open the cloned folder in VS Code.
- Start the ELK stack.

   ```console
   docker compose up -d
   ```
- Give Kibana about a minute to initialize, then access the Kibana web UI by opening `http://<public
  ip>:5601` in a web browser and use the following (default) credentials to log in:
  - user: `elastic`
  - password: `changeme`


<!-- Links -->

[deviantony/docker-elk]: https://github.com/deviantony/docker-elk
[Sage-Bionetworks/docker-elk]: https://github.com/Sage-Bionetworks/docker-elk