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
import { OrgSagebionetworksRepoModelSearchFacetConstraint } from './orgSagebionetworksRepoModelSearchFacetConstraint';

/**
 * JSON schema for a continuous or literal value Facet found in the search results.
 */
export interface OrgSagebionetworksRepoModelSearchFacet {
  name?: string;
  type?: string;
  min?: number;
  max?: number;
  /**
   * The list of constraints for this facet
   */
  constraints?: Array<OrgSagebionetworksRepoModelSearchFacetConstraint>;
}
