export interface TermsOfUseInfo {
  termsOfServiceUrl: string;
  latestTermsOfServiceVersion: string;
  currentRequirements: {
    requirementDate: string;
    minimumTermsOfServiceVersion: string;
  };
}
