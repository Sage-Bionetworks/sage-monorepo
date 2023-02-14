export const config = {
  mongo: {
    options: {
      user: 'openchallenges',
      pass: 'changeme',
    },
    uri: 'mongodb://localhost:27017/openchallenges',
  },
  mariadb: {
    host: 'openchallenges-mariadb',
    user: 'maria',
    pass: 'changeme',
    port: 3306,
  },
};
