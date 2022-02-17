

# Challenge Registry

Monorepo creation status:

|                  | prepare | lint | lint-fix | build | test | coverage | serve | e2e |
|------------------|---------|------|----------|-------|------|----------|-------|-----|
| apps/api         | ✔️       | ✔️    | ✔️        | n/a   | ✔️    |          | ✔️     |     |
| apps/api-db      | ✔️       |      |          |       |      |          | ✔️     |     |
| apps/db-cli      |         | ✔️    | ✔️        | ✔️     | ✔️    |          | ✔️     |     |
| apps/web-app     |         | ✔️    | ✔️        | ✔️     | ✔️    |          | ✔️     |     |
| apps/web-app-e2e |         | ✔️    | ✔️        |       |      |          |       | ✔️   |
| libs/api-angular |         | ✔️    | ✔️        | ✔️     | ✔️    |          |       |     |
| libs/api-docs    |         |      |          | ✔️     | n/a  | n/a      | ✔️     |     |
| libs/api-spec    |         | ✔️    |          | ✔️     | n/a  | n/a      | ✔️     |     |

This project was generated using [Nx](https://nx.dev).

## Requirements

- [Docker]
- [Yarn]

## Development

Source `dev-env.sh`.

    source dev-env.sh

Prepare the development environment.

    challenge-registry-prepare

Lint the projects.

    yarn lint

Build the projects.

    yarn build

Test the projects.

    yarn test

Start the stack.

    yarn start

Seed the API DB with the default seed (`production`).

    challenge-registry-seed-db

<!-- Links -->

[Docker]: https://docs.docker.com/get-docker/
[Yarn]: https://yarnpkg.com/
