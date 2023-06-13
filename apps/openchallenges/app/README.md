# OpenChallenges App

## Configuration

### Dev Server

- Server host: The server port is defined by the property `options.host` in `project.json` for the
  task `serve`.
- Server port: The server port is defined by the property `options.port` in `project.json` for the
  task `serve`.
- Proxy config: The configuration used by the web app to proxy requests to API servers is defined in
  `src/proxy.conf.json`. This file is referenced in `project.json`.

### Containerized Server