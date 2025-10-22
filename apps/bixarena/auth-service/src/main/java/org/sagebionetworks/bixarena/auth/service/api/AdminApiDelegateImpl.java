package org.sagebionetworks.bixarena.auth.service.api;

import org.sagebionetworks.bixarena.auth.service.model.dto.AdminStats200ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 * Delegate implementation for admin API operations.
 */
@Component
public class AdminApiDelegateImpl implements AdminApiDelegate {

  /**
   * Get administrative statistics.
   *
   * <p>This endpoint requires the user to have the USER role.
   *
   * @return Admin statistics response
   */
  @Override
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<AdminStats200ResponseDto> adminStats() {
    AdminStats200ResponseDto response = new AdminStats200ResponseDto();
    response.setOk(true);
    return ResponseEntity.ok(response);
  }
}
