/**
 * AMP-ALS REST API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { DatasetSort } from './datasetSort';
import { DatasetDirection } from './datasetDirection';

/**
 * A dataset search query.
 */
export interface DatasetSearchQuery {
  /**
   * The page number.
   */
  pageNumber?: number;
  /**
   * The number of items in a single page.
   */
  pageSize?: number;
  sort?: DatasetSort;
  /**
   * The seed that initializes the random sorter.
   */
  sortSeed?: number | null;
  direction?: DatasetDirection | null;
  /**
   * A string of search terms used to filter the results.
   */
  searchTerms?: string;
}
