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
import { OrgSagebionetworksRepoModelTableDatasetCollection } from './orgSagebionetworksRepoModelTableDatasetCollection';
import { OrgSagebionetworksRepoModelEntityRef } from './orgSagebionetworksRepoModelEntityRef';
import { OrgSagebionetworksRepoModelTableEntityView } from './orgSagebionetworksRepoModelTableEntityView';
import { OrgSagebionetworksRepoModelTableDataset } from './orgSagebionetworksRepoModelTableDataset';
import { OrgSagebionetworksRepoModelTableSubmissionView } from './orgSagebionetworksRepoModelTableSubmissionView';

/**
 * A view of objects within a scope
 */
/**
 * @type OrgSagebionetworksRepoModelTableView
 * A view of objects within a scope
 * @export
 */
export type OrgSagebionetworksRepoModelTableView =
  | OrgSagebionetworksRepoModelTableDataset
  | OrgSagebionetworksRepoModelTableDatasetCollection
  | OrgSagebionetworksRepoModelTableEntityView
  | OrgSagebionetworksRepoModelTableSubmissionView;
