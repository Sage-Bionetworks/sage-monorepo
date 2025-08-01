/*
 * OpenChallenges API
 * Discover, explore, and contribute to open biomedical challenges.
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package org.sagebionetworks.openchallenges.api.client.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

@jakarta.annotation.Generated(
  value = "org.openapitools.codegen.languages.JavaClientCodegen",
  comments = "Generator version: 7.13.0"
)
public interface Authentication {
  /**
   * Apply authentication settings to header and / or query parameters.
   *
   * @param queryParams The query parameters for the request
   * @param headerParams The header parameters for the request
   * @param cookieParams The cookie parameters for the request
   */
  void applyToParams(
    MultiValueMap<String, String> queryParams,
    HttpHeaders headerParams,
    MultiValueMap<String, String> cookieParams
  );
}
