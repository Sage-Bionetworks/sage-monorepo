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
import { OrgSagebionetworksRepoModelAuthTwoFactorAuthLoginRequest } from './orgSagebionetworksRepoModelAuthTwoFactorAuthLoginRequest';
import { OrgSagebionetworksRepoModelAuthChangePasswordWithTwoFactorAuthToken } from './orgSagebionetworksRepoModelAuthChangePasswordWithTwoFactorAuthToken';

/**
 * Used to perform operations that require two factor authentication.
 */
/**
 * @type OrgSagebionetworksRepoModelAuthHasTwoFactorAuthToken
 * Used to perform operations that require two factor authentication.
 * @export
 */
export type OrgSagebionetworksRepoModelAuthHasTwoFactorAuthToken =
  | OrgSagebionetworksRepoModelAuthChangePasswordWithTwoFactorAuthToken
  | OrgSagebionetworksRepoModelAuthTwoFactorAuthLoginRequest;
