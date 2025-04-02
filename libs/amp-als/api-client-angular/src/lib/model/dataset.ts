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

/**
 * A dataset
 */
export interface Dataset {
  /**
   * The unique identifier of the dataset.
   */
  id: number;
  /**
   * The name of the dataset.
   */
  name: string;
  /**
   * The description of the dataset.
   */
  description: string;
  /**
   * Datetime when the object was added to the database.
   */
  createdAt: string;
  /**
   * Datetime when the object was last modified in the database.
   */
  updatedAt: string;
}
