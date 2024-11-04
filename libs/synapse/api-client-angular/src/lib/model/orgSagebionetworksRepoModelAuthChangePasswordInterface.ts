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
import { OrgSagebionetworksRepoModelAuthChangePasswordWithCurrentPassword } from './orgSagebionetworksRepoModelAuthChangePasswordWithCurrentPassword';
import { OrgSagebionetworksRepoModelAuthChangePasswordWithToken } from './orgSagebionetworksRepoModelAuthChangePasswordWithToken';
import { OrgSagebionetworksRepoModelAuthChangePasswordWithTwoFactorAuthToken } from './orgSagebionetworksRepoModelAuthChangePasswordWithTwoFactorAuthToken';
import { OrgSagebionetworksRepoModelAuthPasswordResetSignedToken } from './orgSagebionetworksRepoModelAuthPasswordResetSignedToken';

/**
 * Defines an interface for changing an user account\'s password
 */
/**
 * @type OrgSagebionetworksRepoModelAuthChangePasswordInterface
 * Defines an interface for changing an user account\'s password
 * @export
 */
export type OrgSagebionetworksRepoModelAuthChangePasswordInterface =
  | OrgSagebionetworksRepoModelAuthChangePasswordWithCurrentPassword
  | OrgSagebionetworksRepoModelAuthChangePasswordWithToken
  | OrgSagebionetworksRepoModelAuthChangePasswordWithTwoFactorAuthToken;