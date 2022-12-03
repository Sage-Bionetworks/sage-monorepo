/**
 * Challenge Registry API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 0.6.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { PageMetadataPaging } from './pageMetadataPaging';


/**
 * The metadata of a page
 */
export interface PageMetadata { 
    paging: PageMetadataPaging;
    /**
     * Total number of results in the result set
     */
    totalResults: number;
}

