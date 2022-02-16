

# Challenge Registry

Monorepo creation status:

|                  | prepare | lint | lint-fix | build | test | coverage | serve |
|------------------|---------|------|----------|-------|------|----------|-------|
| apps/api         | ✔️       | ✔️    | ✔️        | n/a   | ✔️    |          | ✔️     |
| apps/api-db      | ✔️       |      |          |       |      |          | ✔️     |
| apps/db-cli      |         | ✔️    | ✔️        | ✔️     | ✔️    |          | ✔️     |
| apps/web-app     |         |      |          |       |      |          |       |
| libs/api-angular |         | ✔️    | ✔️        | ✔️     | ✔️    |          |       |
| libs/api-docs    |         |      |          | ✔️     | n/a  | n/a      | ✔️     |
| libs/api-spec    |         | ✔️    |          | ✔️     | n/a  | n/a      | ✔️     |

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

<!-- Links -->

[Docker]: https://docs.docker.com/get-docker/
[Yarn]: https://yarnpkg.com/
