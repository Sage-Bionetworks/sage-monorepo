/**
 * Synapse REST API
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { OrgSagebionetworksRepoModelProjectExternalGoogleCloudStorageLocationSetting } from './orgSagebionetworksRepoModelProjectExternalGoogleCloudStorageLocationSetting';
import { OrgSagebionetworksRepoModelProjectExternalS3StorageLocationSetting } from './orgSagebionetworksRepoModelProjectExternalS3StorageLocationSetting';
import { OrgSagebionetworksRepoModelProjectExternalObjectStorageLocationSetting } from './orgSagebionetworksRepoModelProjectExternalObjectStorageLocationSetting';

/**
 * A storage location that needs to provide a bucket name
 */
/**
 * @type OrgSagebionetworksRepoModelProjectBucketStorageLocationSetting
 * A storage location that needs to provide a bucket name
 * @export
 */
export type OrgSagebionetworksRepoModelProjectBucketStorageLocationSetting =
  | OrgSagebionetworksRepoModelProjectExternalGoogleCloudStorageLocationSetting
  | OrgSagebionetworksRepoModelProjectExternalObjectStorageLocationSetting
  | OrgSagebionetworksRepoModelProjectExternalS3StorageLocationSetting;
