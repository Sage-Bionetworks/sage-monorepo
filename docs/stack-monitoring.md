# Stack Monitoring

## Start the ELK stack

Run this command to start the ELK stack in your local environment.

```console
challenge-elk-serve-detach
```

Open Kibana in your browser at http://localhost:5601.

> **Note**
> Only the logs of dockerized apps are captured at this time.

## Access the app logs

1. Log in to Kibana
   - Local ELK stack:
     - Username: `elastic`
     - Password: `changeme`
2. Click on Kibana app menu (three-line icon) > *Logs*.
3. Filter logs, e.g.
   - by Docker container name: `docker.name:"/challenge-keycloak"`.
   - by timestamp using the dedicated UI component.

