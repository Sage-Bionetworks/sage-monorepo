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
 * JSON schema for the fields of a search document.  Note that awesome search does not support camel case so we have to deviate from the usual naming scheme for properties.
 */
export interface OrgSagebionetworksRepoModelSearchDocumentFields {
  name?: string;
  description?: string;
  parent_id?: string;
  node_type?: string;
  etag?: string;
  created_on?: number;
  modified_on?: number;
  created_by?: string;
  modified_by?: string;
  /**
   * All group names with READ access to this entity
   */
  acl?: Array<string>;
  /**
   * All group names with UPDATE access to this entity
   */
  update_acl?: Array<string>;
  diagnosis?: string;
  tissue?: string;
  consortium?: string;
  organ?: string;
}
