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
 * JSON schema for File POJO
 */
export interface OrgSagebionetworksRepoModelFileEntity {
  name?: string;
  description?: string;
  id?: string;
  etag?: string;
  createdOn?: string;
  modifiedOn?: string;
  createdBy?: string;
  modifiedBy?: string;
  parentId?: string;
  concreteType: OrgSagebionetworksRepoModelFileEntity.ConcreteTypeEnum;
  versionNumber?: number;
  versionLabel?: string;
  versionComment?: string;
  isLatestVersion?: boolean;
  dataFileHandleId?: string;
  fileNameOverride?: string;
}
export namespace OrgSagebionetworksRepoModelFileEntity {
  export type ConcreteTypeEnum = 'org.sagebionetworks.repo.model.FileEntity';
  export const ConcreteTypeEnum = {
    OrgSagebionetworksRepoModelFileEntity:
      'org.sagebionetworks.repo.model.FileEntity' as ConcreteTypeEnum,
  };
}