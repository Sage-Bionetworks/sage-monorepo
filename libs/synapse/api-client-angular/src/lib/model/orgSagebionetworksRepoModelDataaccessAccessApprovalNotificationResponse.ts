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
import { OrgSagebionetworksRepoModelDataaccessAccessApprovalNotification } from './orgSagebionetworksRepoModelDataaccessAccessApprovalNotification';

/**
 * Contains the response for an <a href=\"${org.sagebionetworks.repo.model.dataaccess.AccessApprovalNotificationRequest}\">AccessApprovalNotificationRequest</a>.
 */
export interface OrgSagebionetworksRepoModelDataaccessAccessApprovalNotificationResponse {
  requirementId?: number;
  /**
   * The list of notifications, sorted by the recipient and the sentOn date.
   */
  results?: Array<OrgSagebionetworksRepoModelDataaccessAccessApprovalNotification>;
}