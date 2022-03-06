# Dev Container

## Sharing Git credentials with your container

Copy your host SSH configuration to the dev container:

```console
docker cp ~/.ssh/config <container>:/home/vscode/.ssh/.
docker cp ~/.ssh/id_rsa <container>:/home/vscode/.ssh/.
```

That's it!

TODO: Try using `ssh-agent` instead of copying the above files.

Start the SSH Agent in the background by running the following in a terminal:

    eval "$(ssh-agent -s)"

Check that VS Code is automatically forwarding your local SSH agent if one is
running:

    echo $SSH_AUTH_SOCK

List the keys added to `ssh-agent`:

    ssh-add -l

Is the host set up to do agent forwarding?

https://docs.github.com/en/developers/overview/using-ssh-agent-forwarding

Current error in container:

```console
$ ssh-add -l
error fetching identities: communication with agent failed
$ echo $SSH_AUTH_SOCK
/tmp/vscode-ssh-auth-f155ca0551c7f9d56e912e7e2e142496043990ad.sock
```

On host:

```console
$ sudo apt install keychain
$ eval $(keychain --eval --agents ssh id_rsa)

 * keychain 2.8.5 ~ http://www.funtoo.org
 * Starting ssh-agent...
 * Adding 1 ssh key(s): id_rsa
 * ssh-add: Identities added: id_rsa
```

TODO: Is the container recreated?