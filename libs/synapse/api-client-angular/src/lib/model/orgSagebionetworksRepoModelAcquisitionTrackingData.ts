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
import { OrgSagebionetworksRepoModelStatusHistoryRecord } from './orgSagebionetworksRepoModelStatusHistoryRecord';

/**
 * JSON schema for AcquisitionTrackingData
 */
export interface OrgSagebionetworksRepoModelAcquisitionTrackingData {
  status?: string;
  dataAcquisitionReference?: string;
  requestor?: string;
  followupRequirements?: string;
  comments?: string;
  /**
   * Status history
   */
  history?: Array<OrgSagebionetworksRepoModelStatusHistoryRecord>;
}