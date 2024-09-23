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
 * An object that serves as registration for a Synapse user to receive events for the specified event.
 */
export interface OrgSagebionetworksRepoModelWebhookWebhook {
  id?: string;
  createdBy?: string;
  createdOn?: string;
  modifiedOn?: string;
  objectId?: string;
  objectType?: string;
  /**
   * The set of event types to subscribe to.
   */
  eventTypes?: Set<string>;
  invokeEndpoint?: string;
  isEnabled?: boolean;
  verificationStatus?: string;
  verificationMsg?: string;
}
