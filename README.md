

# Challenge Registry

Monorepo creation status:

|                  | prepare | lint | lint-fix | build | test | coverage | serve | e2e | docker |
|------------------|---------|------|----------|-------|------|----------|-------|-----|--------|
| apps/api         | ✔️       | ✔️    | ✔️        |       | ✔️    |          | ✔️     |     | ✔️      |
| apps/api-db      | ✔️       |      |          |       |      |          | ✔️     |     | ✔️      |
| apps/db-cli      |         | ✔️    | ✔️        | ✔️     | ✔️    |          | ✔️     |     |        |
| apps/web-app     | ✔️       | ✔️    | ✔️        | ✔️     | ✔️    |          | ✔️     |     | ✔️      |
| apps/web-app-e2e |         | ✔️    | ✔️        |       |      |          |       | ✔️   |        |
| libs/api-angular |         | ✔️    | ✔️        | ✔️     | ✔️    |          |       |     |        |
| libs/api-docs    |         |      |          | ✔️     |      |          | ✔️     |     |        |
| libs/api-spec    |         | ✔️    |          | ✔️     |      |          | ✔️     |     |        |

This project was generated using [Nx](https://nx.dev).

## Requirements

- [Docker]
- [Node.js] >= 14
- [Yarn] >= 1.22

## Usage

### Running with Docker

Clone this repository.

    git clone --depth 1 https://github.com/Sage-Bionetworks/challenge-registry.git

Source `dev-env.sh`.

    . dev-env.sh

Prepare the development environment.

    challenge-registry-prepare

Build the Docker images.

    yarn docker

Seed the API DB with the sample Challenge data.

    yarn seed-db

Start the Challenge Registry.

    docker compose up

In your browser, open http://localhost.

## Development

Lint the projects.

    yarn lint

Build the projects.

    yarn build

Test the projects.

    yarn test

Start the Challenge Registry.

    yarn start

Seed the API DB with the default seed (`production`).

    yarn seed-db

In your browser, open http://localhost:4200.

## Contributing

See [Contributing.md](.github/CONTRIBUTING.md).

<!-- Links -->

[Docker]: https://docs.docker.com/get-docker/
[Node.js]: https://nodejs.org/en/
[Yarn]: https://yarnpkg.com/
