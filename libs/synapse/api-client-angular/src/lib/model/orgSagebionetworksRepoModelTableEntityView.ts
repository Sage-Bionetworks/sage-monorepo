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
 * A view of Entities within a defined scope.
 */
export interface OrgSagebionetworksRepoModelTableEntityView {
  name?: string;
  description?: string;
  id?: string;
  etag?: string;
  createdOn?: string;
  modifiedOn?: string;
  createdBy?: string;
  modifiedBy?: string;
  parentId?: string;
  concreteType: OrgSagebionetworksRepoModelTableEntityView.ConcreteTypeEnum;
  versionNumber?: number;
  versionLabel?: string;
  versionComment?: string;
  isLatestVersion?: boolean;
  /**
   * The list of ColumnModel IDs that define the schema of the object.
   */
  columnIds?: Array<string>;
  isSearchEnabled?: boolean;
  viewTypeMask?: number;
  type?: string;
  /**
   * The list of container ids that define the scope of this view.
   */
  scopeIds?: Array<string>;
}
export namespace OrgSagebionetworksRepoModelTableEntityView {
  export type ConcreteTypeEnum = 'org.sagebionetworks.repo.model.table.EntityView';
  export const ConcreteTypeEnum = {
    OrgSagebionetworksRepoModelTableEntityView:
      'org.sagebionetworks.repo.model.table.EntityView' as ConcreteTypeEnum,
  };
}
