/**
 * Synapse REST API
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { OrgSagebionetworksRepoModelDataaccessAccessorChange } from './orgSagebionetworksRepoModelDataaccessAccessorChange';

/**
 *
 */
export interface OrgSagebionetworksRepoModelDataaccessSubmissionSearchResult {
  id?: string;
  createdOn?: string;
  modifiedOn?: string;
  accessRequirementId?: string;
  accessRequirementVersion?: string;
  accessRequirementName?: string;
  /**
   * The list of principal ids that are allowed to review the submission
   */
  accessRequirementReviewerIds?: Array<string>;
  submitterId?: string;
  /**
   * List of user changes. A user can gain access, renew access or have access revoked.
   */
  accessorChanges?: Array<OrgSagebionetworksRepoModelDataaccessAccessorChange>;
  state?: string;
}
