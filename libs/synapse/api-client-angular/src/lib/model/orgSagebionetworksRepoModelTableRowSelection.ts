/**
 * Synapse REST API
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

/**
 * Represents a selection of rows of a TableEntity
 */
export interface OrgSagebionetworksRepoModelTableRowSelection {
  tableId?: string;
  etag?: string;
  /**
   * Each row id of this list refers to a single row of a TableEntity.
   */
  rowIds?: Array<number>;
}
