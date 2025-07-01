package org.sagebionetworks.openchallenges.auth.service.api;

import org.sagebionetworks.openchallenges.auth.service.model.dto.ApiKeyDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyResponseDto;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ApiKeysApiDelegateImpl implements ApiKeysApiDelegate {

  private final ApiKeyService apiKeyService;

  public ApiKeysApiDelegateImpl(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  @Override
  public ResponseEntity<CreateApiKeyResponseDto> createApiKey(CreateApiKeyRequestDto createApiKeyRequestDto) {
    // TODO: Implement API key creation logic
    // This would typically:
    // 1. Get the authenticated user (from security context)
    // 2. Generate a new API key
    // 3. Save it to the database
    // 4. Return the key details
    throw new UnsupportedOperationException("Create API key endpoint not yet implemented");
  }

  @Override
  public ResponseEntity<Void> deleteApiKey(UUID keyId) {
    // TODO: Implement API key deletion logic
    // This would typically:
    // 1. Get the authenticated user (from security context)
    // 2. Verify the API key belongs to the user
    // 3. Delete the API key from the database
    throw new UnsupportedOperationException("Delete API key endpoint not yet implemented");
  }

  @Override
  public ResponseEntity<List<ApiKeyDto>> listApiKeys() {
    // TODO: Implement API key listing logic
    // This would typically:
    // 1. Get the authenticated user (from security context)
    // 2. Retrieve all API keys for that user
    // 3. Return the list (without the actual key values for security)
    throw new UnsupportedOperationException("List API keys endpoint not yet implemented");
  }
}
