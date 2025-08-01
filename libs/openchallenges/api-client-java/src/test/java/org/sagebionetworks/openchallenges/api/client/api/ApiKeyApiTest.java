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

package org.sagebionetworks.openchallenges.api.client.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.api.client.model.ApiKey;
import org.sagebionetworks.openchallenges.api.client.model.BasicError;
import org.sagebionetworks.openchallenges.api.client.model.CreateApiKeyRequest;
import org.sagebionetworks.openchallenges.api.client.model.CreateApiKeyResponse;

/**
 * API tests for ApiKeyApi
 */
@Disabled
public class ApiKeyApiTest {

  private final ApiKeyApi api = new ApiKeyApi();

  /**
   * Create API key
   *
   * Generate a new API key for the authenticated user
   */
  @Test
  public void createApiKeyTest() {
    CreateApiKeyRequest createApiKeyRequest = null;
    CreateApiKeyResponse response = api.createApiKey(createApiKeyRequest);
    // TODO: test validations
  }

  /**
   * Delete API key
   *
   * Revoke an API key
   */
  @Test
  public void deleteApiKeyTest() {
    UUID keyId = null;
    api.deleteApiKey(keyId);
    // TODO: test validations
  }

  /**
   * List API keys
   *
   * Get all API keys for the authenticated user
   */
  @Test
  public void listApiKeysTest() {
    List<ApiKey> response = api.listApiKeys();
    // TODO: test validations
  }
}
