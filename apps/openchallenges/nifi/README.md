# OpenChallenges NiFi

## Build the Docker image

Build the Docker image of NiFi and the NiFi registry.

```console
nx run-many -t build-image -p openchallenges-{nifi,nifi-registry}
```

## Deploy NiFi with Docker Compose

```console
nx serve-detach openchallenges-nifi
```

## Access NiFi UI

1. In your browser, navigate to http://localhost:8443/nifi.
2. Accept the security risk (in the development environment).
3. Login with the credentials specified in the config file `.env`.

The NiFi canvas should now be visible.

## Connect NiFi to the NiFi registry

Configuring a NiFi registry client enable to version control the Process Groups created in NiFi.

1. Click on the three-line menu button.
2. Click on "Controller Settings".
3. Click on the tab "Registry Clients".
4. Click on the button "+".
5. Add the registry client:
   - Name: openchallenges-nifi-registry.
   - Type: NifiRegistryFlowRegistryClient.
   - Click on "Apply".
6. Configure the registry client:
   - Click on the three-dot button.
   - Click on "Edit".
   - Click on the tab "Properties".
   - Set the URL to `http://openchallenges-nifi-registry:18080`.
   - Click on "Apply".
7. Click on the NiFi logo in the navigation bar to return to the canvas.

Here is a quick way to check that NiFi can connect to the registry:

1. Add a Process Group to the canvas.
2. Right-click on the PG and select "Version" > "Start Version Control".
3. The "Save Flow Version" dialog should open without any error messages visible.

## Stop and remove the NiFi container

```console
docker rm -f openchallenges-nifi-registry
```

## Data persistence

Process Groups created in NiFi can be saved and version-controlled using the NiFi Registry.
