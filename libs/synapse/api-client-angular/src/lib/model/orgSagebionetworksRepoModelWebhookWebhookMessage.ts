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
import { OrgSagebionetworksRepoModelWebhookWebhookVerificationMessage } from './orgSagebionetworksRepoModelWebhookWebhookVerificationMessage';
import { OrgSagebionetworksRepoModelWebhookWebhookSynapseEventMessage } from './orgSagebionetworksRepoModelWebhookWebhookSynapseEventMessage';

/**
 * Data transfer object for messages to be sent to webhook endpoints.
 */
/**
 * @type OrgSagebionetworksRepoModelWebhookWebhookMessage
 * Data transfer object for messages to be sent to webhook endpoints.
 * @export
 */
export type OrgSagebionetworksRepoModelWebhookWebhookMessage =
  | OrgSagebionetworksRepoModelWebhookWebhookSynapseEventMessage
  | OrgSagebionetworksRepoModelWebhookWebhookVerificationMessage;
