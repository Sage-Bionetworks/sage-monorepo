/**
 * Synapse REST API
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { OrgSagebionetworksRepoModelFileS3UploadDestination } from './orgSagebionetworksRepoModelFileS3UploadDestination';
import { OrgSagebionetworksRepoModelFileExternalS3UploadDestination } from './orgSagebionetworksRepoModelFileExternalS3UploadDestination';
import { OrgSagebionetworksRepoModelFileExternalObjectStoreUploadDestination } from './orgSagebionetworksRepoModelFileExternalObjectStoreUploadDestination';
import { OrgSagebionetworksRepoModelFileExternalGoogleCloudUploadDestination } from './orgSagebionetworksRepoModelFileExternalGoogleCloudUploadDestination';
import { OrgSagebionetworksRepoModelFileExternalUploadDestination } from './orgSagebionetworksRepoModelFileExternalUploadDestination';

/**
 * The upload destination contains information to start an upload of a file generated according to the underlying <a href=\"${org.sagebionetworks.repo.model.project.StorageLocationSetting}\">StorageLocationSetting</a>.
 */
/**
 * @type OrgSagebionetworksRepoModelFileUploadDestination
 * The upload destination contains information to start an upload of a file generated according to the underlying <a href=\"${org.sagebionetworks.repo.model.project.StorageLocationSetting}\">StorageLocationSetting</a>.
 * @export
 */
export type OrgSagebionetworksRepoModelFileUploadDestination =
  | OrgSagebionetworksRepoModelFileExternalGoogleCloudUploadDestination
  | OrgSagebionetworksRepoModelFileExternalObjectStoreUploadDestination
  | OrgSagebionetworksRepoModelFileExternalS3UploadDestination
  | OrgSagebionetworksRepoModelFileExternalUploadDestination
  | OrgSagebionetworksRepoModelFileS3UploadDestination;
