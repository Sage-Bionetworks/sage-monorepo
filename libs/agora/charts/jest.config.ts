/* eslint-disable */
export default {
  displayName: 'agora-charts',
  preset: '../../../jest.preset.js',
  setupFilesAfterEnv: ['<rootDir>/src/test-setup.ts'],
  globals: {},
  coverageDirectory: '../../../coverage/libs/agora/charts',
  transform: {
    '^.+\\.(ts|mjs|js|html)$': [
      'jest-preset-angular',
      {
        tsconfig: '<rootDir>/tsconfig.spec.json',
        stringifyContentPathRegex: '\\.(html|svg)$',
      },
    ],
  },
  maxWorkers: '50%',
  testEnvironment: 'jest-fixed-jsdom',
  transformIgnorePatterns: ['node_modules/(?!(.pnpm/.*/node_modules/)?(.*\\.mjs$|until-async))'],
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
