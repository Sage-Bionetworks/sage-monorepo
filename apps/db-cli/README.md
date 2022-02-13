# DB Client

This CLI is a tool to manage the content of the Challenge Registry MongoDB
instance.

## Usage

Build the client.

    nx build db-cli

Run the client.

    yarn run db-cli --help

Seed the DB with local data (e.g., using the `production` seed).

```console
$ yarn run db-cli seed apps/db-cli/data/seeds/production/
yarn run v1.22.17
$ node dist/apps/db-cli/src/index.js seed apps/db-cli/data/seeds/production/
2022-02-12 21:25:32 info: Collection challenge_organizer removed
2022-02-12 21:25:32 info: Collection challenge_sponsor removed
2022-02-12 21:25:32 info: Collection org_membership removed
2022-02-12 21:25:32 info: Collection challenge_readme removed
2022-02-12 21:25:32 info: Collection challenge_platform removed
2022-02-12 21:25:32 info: Collection challenge removed
2022-02-12 21:25:33 info: 🌱 Seeding users completed
2022-02-12 21:25:33 info: 🌱 Seeding organizations completed
2022-02-12 21:25:33 info: 🌱 Seeding orgMemberships completed
2022-02-12 21:25:33 info: 🌱 Seeding challengePlatforms completed
2022-02-12 21:25:34 info: 🌱 Seeding challenges completed
2022-02-12 21:25:37 info: 🌱 Seeding challengeReadmes completed
2022-02-12 21:25:39 info: 🌱 Seeding challengeOrganizers completed
2022-02-12 21:25:41 info: 🌱 Seeding challengeSponsors completed
Done in 8.60s.
```
