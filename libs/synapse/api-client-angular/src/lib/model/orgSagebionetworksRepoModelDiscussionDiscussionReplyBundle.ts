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

/**
 * The Reply model object represents a single reply in a thread.
 */
export interface OrgSagebionetworksRepoModelDiscussionDiscussionReplyBundle {
  id?: string;
  threadId?: string;
  forumId?: string;
  projectId?: string;
  createdOn?: string;
  createdBy?: string;
  modifiedOn?: string;
  etag?: string;
  messageKey?: string;
  isEdited?: boolean;
  isDeleted?: boolean;
}
