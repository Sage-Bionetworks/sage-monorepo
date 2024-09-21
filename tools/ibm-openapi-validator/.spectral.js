const ibmCloudValidationRules = require('@ibm-cloud/openapi-ruleset');
const { pathSegmentCasingConvention } = require('@ibm-cloud/openapi-ruleset/src/functions');
const { schemas } = require('@ibm-cloud/openapi-ruleset-utilities/src/collections');

module.exports = {
  extends: ibmCloudValidationRules,
  rules: {
    'ibm-accept-and-return-models': 'off',
    'ibm-enum-casing-convention': 'off',
    'ibm-operation-summary-length': 'warn',
    'ibm-parameter-casing-convention': 'off',
    'ibm-path-segment-casing-convention': {
      description: 'Path segments must follow camel case',
      message: '{{error}}',
      resolved: true,
      given: schemas,
      severity: 'error',
      then: {
        function: pathSegmentCasingConvention,
        functionOptions: {
          type: 'snake',
        },
      },
    },
    'ibm-property-casing-convention': 'off',
  },
};