/**
 * OpenChallenges REST API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { Organization } from './organization';

/**
 * A page of organizations
 */
export interface OrganizationsPage {
  /**
   * The page number.
   */
  number: number;
  /**
   * The number of items in a single page.
   */
  size: number;
  /**
   * Total number of elements in the result set.
   */
  totalElements: number;
  /**
   * Total number of pages in the result set.
   */
  totalPages: number;
  /**
   * Returns if there is a next page.
   */
  hasNext: boolean;
  /**
   * Returns if there is a previous page.
   */
  hasPrevious: boolean;
  /**
   * A list of organizations
   */
  organizations: Array<Organization>;
}
