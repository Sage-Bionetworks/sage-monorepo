# Dev Container

## Sharing Git credentials with your container

Copy your host SSH configuration to the dev container:

```console
docker cp ~/.ssh/config <container>:/home/vscode/.ssh/.
docker cp ~/.ssh/id_rsa <container>:/home/vscode/.ssh/.
```

That's it!

TODO: Try using `ssh-agent` instead of copying the above files.