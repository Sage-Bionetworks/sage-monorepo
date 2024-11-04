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
 * A special webhook message that is sent to verify a webhook
 */
export interface OrgSagebionetworksRepoModelWebhookWebhookVerificationMessage {
  concreteType: OrgSagebionetworksRepoModelWebhookWebhookVerificationMessage.ConcreteTypeEnum;
  messageId: string;
  eventTimestamp: string;
  verificationCode?: string;
}
export namespace OrgSagebionetworksRepoModelWebhookWebhookVerificationMessage {
  export type ConcreteTypeEnum =
    'org.sagebionetworks.repo.model.webhook.WebhookVerificationMessage';
  export const ConcreteTypeEnum = {
    OrgSagebionetworksRepoModelWebhookWebhookVerificationMessage:
      'org.sagebionetworks.repo.model.webhook.WebhookVerificationMessage' as ConcreteTypeEnum,
  };
}