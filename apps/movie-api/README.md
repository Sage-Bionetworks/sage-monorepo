# Movie REST API

## Introduction

Spring Boot REST API to get movie information. This application was developed by
@wkrzywiec as part of his [tutorial on securing a REST API with
Keycloak](https://wkrzywiec.medium.com/implementing-oauth-2-0-access-token-validation-with-spring-security-64c797b42b36).
The code was imported and adapted from
[wkrzywiec/keycloak-security-example](https://github.com/wkrzywiec/keycloak-security-example).

## Development

### Building

```console
nx build movie-api
```

### Testing

```console
nx test movie-app
```

### Starting the app

```console
nx serve movie-api
```

### Building the image

```console
nx build-image movie-api
```
