package org.sagebionetworks.bixarena.api.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.api.model.dto.AdminStats200ResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestDto;
import org.sagebionetworks.bixarena.api.service.QuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Admin API delegate.
 * Handles administrative operations requiring elevated privileges.
 */
@Component
@RequiredArgsConstructor
public class AdminApiDelegateImpl implements AdminApiDelegate {

  private final QuestService questService;

  @Override
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<AdminStats200ResponseDto> adminStats() {
    AdminStats200ResponseDto response = AdminStats200ResponseDto.builder().ok(true).build();
    return ResponseEntity.ok(response);
  }

  @Override
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<QuestDto> adminGetQuest(String questId) {
    QuestDto quest = questService.getQuestUngated(questId);
    return ResponseEntity.ok(quest);
  }
}
