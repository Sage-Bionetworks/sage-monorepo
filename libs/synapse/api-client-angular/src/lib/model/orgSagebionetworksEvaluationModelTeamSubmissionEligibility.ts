/**
 * Synapse REST API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v1
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { OrgSagebionetworksEvaluationModelSubmissionEligibility } from './orgSagebionetworksEvaluationModelSubmissionEligibility';
import { OrgSagebionetworksEvaluationModelMemberSubmissionEligibility } from './orgSagebionetworksEvaluationModelMemberSubmissionEligibility';

/**
 * Describes the eligibility of a Challenge Team to submit to an Evalution queue, reflecting the queue\'s submission quotas and current submissions.
 */
export interface OrgSagebionetworksEvaluationModelTeamSubmissionEligibility {
  teamId?: string;
  evaluationId?: string;
  teamEligibility?: OrgSagebionetworksEvaluationModelSubmissionEligibility;
  /**
   * Describes the submission eligibility of the contributors to the Submission.
   */
  membersEligibility?: Array<OrgSagebionetworksEvaluationModelMemberSubmissionEligibility>;
  eligibilityStateHash?: number;
}
