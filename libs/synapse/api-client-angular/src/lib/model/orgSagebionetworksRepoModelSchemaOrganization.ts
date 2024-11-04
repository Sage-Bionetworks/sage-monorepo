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
 * The first step in creating new type schema in Synapse is to setup and configure an organization. The name of the organization serves as the root for each schema\'s $id managed by that organization.  The organization name \'org.sagebionetworks\' is reserved for the core Synapse model objects.   Each organization also has an Access Control List (ACL) that controls who can add schemas to an organization.  All schemas created under an Organization will be considered publicly readable and reference-able. Organizations are immutable
 */
export interface OrgSagebionetworksRepoModelSchemaOrganization {
  id?: string;
  name?: string;
  createdOn?: string;
  createdBy?: string;
}