/* eslint-disable */
export default {
  displayName: 'agora-nomination-form',
  preset: '../../../jest.preset.js',
  setupFilesAfterEnv: ['<rootDir>/src/test-setup.ts'],
  globals: {},
  coverageDirectory: '../../../coverage/libs/agora/nomination-form',
  transform: {
    '^.+\\.(ts|mjs|js|html)$': [
      'jest-preset-angular',
      {
        tsconfig: '<rootDir>/tsconfig.spec.json',
        stringifyContentPathRegex: '\\.(html|svg)$',
      },
    ],
  },
  transformIgnorePatterns: [
    'node_modules/(?!(.pnpm/.*/node_modules/)?(.*\\.mjs$|@octokit/.*|universal-user-agent|before-after-hook))',
  ],
  moduleNameMapper: {
    d3: '<rootDir>/../../../node_modules/d3/dist/d3.min.js',
    '^d3-(.*)$': '<rootDir>/../../../node_modules/d3-$1/dist/d3-$1.min.js',
  },
  snapshotSerializers: [
    'jest-preset-angular/build/serializers/no-ng-attributes',
    'jest-preset-angular/build/serializers/ng-snapshot',
    'jest-preset-angular/build/serializers/html-comment',
  ],
};
