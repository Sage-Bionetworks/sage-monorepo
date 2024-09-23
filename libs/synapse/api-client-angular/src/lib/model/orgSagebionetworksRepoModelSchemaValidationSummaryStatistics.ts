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
 * Summary statistics for the JSON schema validation results for the children of an Entity container (Project or Folder)
 */
export interface OrgSagebionetworksRepoModelSchemaValidationSummaryStatistics {
  containerId?: string;
  totalNumberOfChildren?: number;
  numberOfValidChildren?: number;
  numberOfInvalidChildren?: number;
  numberOfUnknownChildren?: number;
  generatedOn?: string;
}
