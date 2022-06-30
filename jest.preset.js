const nxPreset = require('@nrwl/jest/preset').default;

module.exports = {
  ...nxPreset,
  runInBand: true,
  collectCoverage: true,
  coverageReporters: ['text', 'html','json','lcov'],
  coverageThreshold: {
    "global": {
      "branches": 0,
      "functions": 0,
      "lines": 0,
      "statements": 0
    }
  }
};
