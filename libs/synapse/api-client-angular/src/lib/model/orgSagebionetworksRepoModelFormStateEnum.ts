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

export type OrgSagebionetworksRepoModelFormStateEnum =
  | 'WAITING_FOR_SUBMISSION'
  | 'SUBMITTED_WAITING_FOR_REVIEW'
  | 'ACCEPTED'
  | 'REJECTED';

export const OrgSagebionetworksRepoModelFormStateEnum = {
  WaitingForSubmission: 'WAITING_FOR_SUBMISSION' as OrgSagebionetworksRepoModelFormStateEnum,
  SubmittedWaitingForReview:
    'SUBMITTED_WAITING_FOR_REVIEW' as OrgSagebionetworksRepoModelFormStateEnum,
  Accepted: 'ACCEPTED' as OrgSagebionetworksRepoModelFormStateEnum,
  Rejected: 'REJECTED' as OrgSagebionetworksRepoModelFormStateEnum,
};