# FAQ

## Unable to create Nx workspace because of GPG-related bug

The solution is to temporarily disable the git config `commit.gpgsign`.

```console
git config --global commit.gpgsign false
yarn create nx-workspace openchallenges --preset=empty --packageManager=yarn
git config --global commit.gpgsign true
```

## Task `api:lint` fails

```console
✖  nx run api:lint
    Loading .env environment variables...
    Error: the command black could not be found within PATH or Pipfile's [scripts].
    ERROR: Something went wrong in @nrwl/run-commands - Command failed: pipenv run black ./openapi_server --check --exclude '(models|test)'
```

Run `nx python api` to create the Python virtualenv and install the tools needed.

## No space left on device

This error likely happens because the root filesystem is full. Verify whether that is the case by
running the following command on the host (not from within the dev container).

```console
$ df -h
Filesystem      Size  Used Avail Use% Mounted on
devtmpfs        7.8G     0  7.8G   0% /dev
tmpfs           7.8G     0  7.8G   0% /dev/shm
tmpfs           7.8G  692K  7.8G   1% /run
tmpfs           7.8G     0  7.8G   0% /sys/fs/cgroup
/dev/nvme0n1p1   50G   50G  679M  99% /
tmpfs           1.6G     0  1.6G   0% /run/user/1000
```

The root filesystem (/) is indeed full. The command below lists the folders starting from the
largest one. Make sure to run this command as root, otherwise only the folders visible to the user
will be taken into account.

```console
[ec2-user@ip-10-41-30-136 ~]$ sudo du -aBM / 2>/dev/null | sort -nr | head -n 50 | more
53903M  /
41411M  /var
40578M  /var/lib
40455M  /var/lib/docker
30810M  /var/lib/docker/overlay2
10171M  /home/ec2-user
10171M  /home
9609M   /var/lib/docker/volumes
8585M   /var/lib/docker/volumes/dind-var-lib-docker/_data
8585M   /var/lib/docker/volumes/dind-var-lib-docker
7976M   /var/lib/docker/volumes/dind-var-lib-docker/_data/overlay2
5184M   /var/lib/docker/overlay2/a4675f542ab8ee863953360af4be4d3ab46fa9743bbb4a0ec56a56a887c8ef52
...
```

Regularly building Docker images filled up the root filesystem. Consider performing the following
actions to free up disk space.

Remove unused images:

```console
docker container prune --force && docker rmi $(docker images -aq) --force
```

> **Note** When using VS Code dev container with the feature `docker-in-docker`, removing the images
> on the host won't remove the images in the dev container. If that is the case, you can also run
> the above command inside the dev container.

Prune Docker system:

```console
docker system prune
```

Prune Docker volumes:

```console
# remove selected volume
docker volume ls
docker volume rm <volume name>

# remove all volumes
docker volume prune
```

> **Note** The volume `dind-var-lib-docker` is created by the dev container feature
> `docker-in-docker`. The volume `vscode` is created when starting a dev container with VS Code.
> These two volumes should not be removed if a dev container is running.
