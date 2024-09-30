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
import { OrgSagebionetworksRepoModelDoiV2DoiNameIdentifier } from './orgSagebionetworksRepoModelDoiV2DoiNameIdentifier';

/**
 * JSON schema for DOI Metadata Creator.
 */
export interface OrgSagebionetworksRepoModelDoiV2DoiCreator {
  creatorName?: string;
  /**
   * Optional. Uniquely identifies an individual or legal entity, according to various schemas.
   */
  nameIdentifiers?: Array<OrgSagebionetworksRepoModelDoiV2DoiNameIdentifier>;
}