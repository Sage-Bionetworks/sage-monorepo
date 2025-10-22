package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.AdminStats200ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Admin API delegate.
 * Handles administrative operations requiring elevated privileges.
 */
@Component
public class AdminApiDelegateImpl implements AdminApiDelegate {

  /**
   * Get administrative statistics.
   * Requires USER role for testing authorization.
   *
   * @return Admin statistics response
   */
  @Override
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<AdminStats200ResponseDto> adminStats() {
    AdminStats200ResponseDto response = AdminStats200ResponseDto.builder().ok(true).build();
    return ResponseEntity.ok(response);
  }
}
