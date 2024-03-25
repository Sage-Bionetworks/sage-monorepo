# OpenChallenges App

Plop v3

## Configuration

Test

### Dev Server

- Server host: The server port is defined by the property `options.host` in `project.json` for the
  task `serve`.
- Server port: The server port is defined by the property `options.port` in `project.json` for the
  task `serve`.
- Proxy config: The configuration used by the web app to proxy requests to API servers is defined in
  `src/proxy.conf.json`. This file is referenced in `project.json`. CORS is enabled on the OC API
  gateway so there is no need to proxy the requests sent to it.
- App config: The configuration used by the web app is defined in `src/config/config.json`

### Containerized Server

- Server host: TODO
- Server port: The server listen to the port defined in `docker/nginx/templates/http.conf.template`.
  The port exposed by the container is defined in `Dockerfile` and
  `{workspaceRoot}/docker/openchallenges/services/app.yml`.
- Proxy config: The configuration used by the Nginx to proxy requests to API servers can be
  specified in `docker/nginx/templates/http.conf.template`. CORS is enabled on the OC API gateway so
  there is no need to proxy the requests sent to it.
- App config: The configuration used by the web app is defined by the file
  `src/config/config.json.template` and the environment variables specified to the container.
