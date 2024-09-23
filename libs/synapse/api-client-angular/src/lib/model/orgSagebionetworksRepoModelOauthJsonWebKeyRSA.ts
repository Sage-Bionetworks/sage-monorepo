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
 * JSON Web Key for the RSA algorithm
 */
export interface OrgSagebionetworksRepoModelOauthJsonWebKeyRSA {
  kid?: string;
  kty?: string;
  use?: string;
  concreteType: OrgSagebionetworksRepoModelOauthJsonWebKeyRSA.ConcreteTypeEnum;
  e?: string;
  n?: string;
}
export namespace OrgSagebionetworksRepoModelOauthJsonWebKeyRSA {
  export type ConcreteTypeEnum = 'org.sagebionetworks.repo.model.oauth.JsonWebKeyRSA';
  export const ConcreteTypeEnum = {
    OrgSagebionetworksRepoModelOauthJsonWebKeyRsa:
      'org.sagebionetworks.repo.model.oauth.JsonWebKeyRSA' as ConcreteTypeEnum,
  };
}
