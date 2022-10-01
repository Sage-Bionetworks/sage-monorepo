module.exports = {
  displayName: 'challenge-bot',
  preset: '../../jest.preset.js',
  globals: {
    'ts-jest': {
      tsconfig: '<rootDir>/tsconfig.spec.json',
    },
  },
  // transform: {
  //   '^.+\\.[tj]s$': 'ts-jest',
  // },
  // transformIgnorePatterns: ['<rootDir>/node_modules/(?!*)'],
  moduleFileExtensions: ['ts', 'js', 'html'],
  coverageDirectory: '../../coverage/apps/challenge-bot',
  testMatch: ['<rootDir>/test/**/*.spec.ts'],
};
