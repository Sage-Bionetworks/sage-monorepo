/**
 * Registry of Open Community Challenges API
 * The OpenAPI specification implemented by the Challenge Registries. # Introduction TBA
 *
 * The version of the OpenAPI document: 0.6.0
 * Contact: thomas.schaffter@sagebionetworks.org
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { User } from './user';
import { ResponsePageMetadataPaging } from './responsePageMetadataPaging';
import { ResponsePageMetadata } from './responsePageMetadata';
import { PageOfUsersAllOf } from './pageOfUsersAllOf';

/**
 * A page of Users
 */
export interface PageOfUsers {
  /**
   * Index of the first result that must be returned
   */
  offset: number;
  /**
   * Maximum number of results returned
   */
  limit: number;
  paging: ResponsePageMetadataPaging;
  /**
   * Total number of results in the result set
   */
  totalResults: number;
  /**
   * An array of Users
   */
  users: Array<User>;
}
