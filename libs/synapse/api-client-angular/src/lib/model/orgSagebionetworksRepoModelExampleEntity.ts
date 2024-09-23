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
import { OrgSagebionetworksRepoModelReference } from './orgSagebionetworksRepoModelReference';
import { OrgSagebionetworksRepoModelEnvironmentDescriptor } from './orgSagebionetworksRepoModelEnvironmentDescriptor';

/**
 * This object is deprecated and will be removed in future versions of Synapse.
 */
export interface OrgSagebionetworksRepoModelExampleEntity {
  name?: string;
  description?: string;
  id?: string;
  etag?: string;
  createdOn?: string;
  modifiedOn?: string;
  createdBy?: string;
  modifiedBy?: string;
  parentId?: string;
  concreteType: OrgSagebionetworksRepoModelExampleEntity.ConcreteTypeEnum;
  singleString?: string;
  /**
   * This is an example of a list of strings
   */
  stringList?: Array<string>;
  singleDate?: string;
  /**
   * This is an example of a list of dates
   */
  dateList?: Array<string>;
  singleDouble?: number;
  /**
   * This is an example of a double list
   */
  doubleList?: Array<number>;
  singleInteger?: number;
  /**
   * This is an example of an Integer List.
   */
  integerList?: Array<number>;
  concept?: string;
  someEnum?: string;
  /**
   * References
   */
  references?: Set<OrgSagebionetworksRepoModelReference>;
  /**
   * The list of environment descriptors
   */
  environmentDescriptors?: Set<OrgSagebionetworksRepoModelEnvironmentDescriptor>;
}
export namespace OrgSagebionetworksRepoModelExampleEntity {
  export type ConcreteTypeEnum = 'org.sagebionetworks.repo.model.ExampleEntity';
  export const ConcreteTypeEnum = {
    OrgSagebionetworksRepoModelExampleEntity:
      'org.sagebionetworks.repo.model.ExampleEntity' as ConcreteTypeEnum,
  };
}
