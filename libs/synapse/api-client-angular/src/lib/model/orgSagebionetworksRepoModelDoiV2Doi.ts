/**
 * Synapse REST API
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { OrgSagebionetworksRepoModelDoiV2DoiTitle } from './orgSagebionetworksRepoModelDoiV2DoiTitle';
import { OrgSagebionetworksRepoModelDoiV2DoiResourceType } from './orgSagebionetworksRepoModelDoiV2DoiResourceType';
import { OrgSagebionetworksRepoModelDoiV2DoiCreator } from './orgSagebionetworksRepoModelDoiV2DoiCreator';

/**
 * JSON schema for fields associated with a DOI and its metadata. This object contains fields from both <a href=\"${org.sagebionetworks.repo.model.doi.v2.DoiAssociation}\">DoiAssociation</a> and <a href=\"${org.sagebionetworks.repo.model.doi.v2.DataciteMetadata}\">DataciteMetadata</a>.<br>To mint a DOI, the following fields are required: <ul> <li>Information that uniquely identifies an object in Synapse <ul><li> The object ID <i>(e.g. \"syn12345\")</i></li><li>The object type <i>(Note: as of this writing, only ENTITY is supported)</i></li><li>The version number of the object <i>(Optional. A \"null\" version will mint a DOI that will point to the most recent version of the object, even if the object changes)</i></li><li>An eTag <i>(Necessary when updating an existing DOI)</i></li></ul></li> <li>Additional supplementary information to mint a DOI <ul><li>Creator(s)</li><li>Title(s)</li><li>The general resource type</li><li>A publication year (no later than one year after the current year)</li></ul></li> </ul><br>For more information, see the <a href=\"${org.sagebionetworks.repo.model.doi.v2.DoiAssociation}\">DoiAssociation</a> and <a href=\"${org.sagebionetworks.repo.model.doi.v2.DataciteMetadata}\">DataciteMetadata</a> objects.
 */
export interface OrgSagebionetworksRepoModelDoiV2Doi {
  /**
   * Required. The main researchers involved in producing the data, or the authors of the publication, in priority order.
   */
  creators?: Array<OrgSagebionetworksRepoModelDoiV2DoiCreator>;
  /**
   * Required. A name or title by which a resource is known.
   */
  titles?: Array<OrgSagebionetworksRepoModelDoiV2DoiTitle>;
  publicationYear?: number;
  resourceType?: OrgSagebionetworksRepoModelDoiV2DoiResourceType;
  associationId?: string;
  etag?: string;
  doiUri?: string;
  doiUrl?: string;
  objectId?: string;
  objectType?: string;
  objectVersion?: number;
  associatedBy?: string;
  associatedOn?: string;
  updatedBy?: string;
  updatedOn?: string;
}
