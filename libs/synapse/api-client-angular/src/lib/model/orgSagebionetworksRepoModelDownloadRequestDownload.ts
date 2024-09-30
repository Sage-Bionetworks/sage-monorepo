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
 * In order to download a one or more files, the user will need to be granted the \'DOWNLOAD\' permission.
 */
export interface OrgSagebionetworksRepoModelDownloadRequestDownload {
  concreteType: OrgSagebionetworksRepoModelDownloadRequestDownload.ConcreteTypeEnum;
  benefactorId?: number;
}
export namespace OrgSagebionetworksRepoModelDownloadRequestDownload {
  export type ConcreteTypeEnum = 'org.sagebionetworks.repo.model.download.RequestDownload';
  export const ConcreteTypeEnum = {
    OrgSagebionetworksRepoModelDownloadRequestDownload:
      'org.sagebionetworks.repo.model.download.RequestDownload' as ConcreteTypeEnum,
  };
}