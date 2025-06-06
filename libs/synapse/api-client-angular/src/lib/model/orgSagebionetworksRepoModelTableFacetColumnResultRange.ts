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
 * Includes the minimum and maximum values of a range facet column and selected ranges applied to the filter.
 */
export interface OrgSagebionetworksRepoModelTableFacetColumnResultRange {
  concreteType: OrgSagebionetworksRepoModelTableFacetColumnResultRange.ConcreteTypeEnum;
  columnName?: string;
  facetType?: string;
  jsonPath?: string;
  columnMin?: string;
  columnMax?: string;
  selectedMin?: string;
  selectedMax?: string;
}
export namespace OrgSagebionetworksRepoModelTableFacetColumnResultRange {
  export type ConcreteTypeEnum = 'org.sagebionetworks.repo.model.table.FacetColumnResultRange';
  export const ConcreteTypeEnum = {
    OrgSagebionetworksRepoModelTableFacetColumnResultRange:
      'org.sagebionetworks.repo.model.table.FacetColumnResultRange' as ConcreteTypeEnum,
  };
}
