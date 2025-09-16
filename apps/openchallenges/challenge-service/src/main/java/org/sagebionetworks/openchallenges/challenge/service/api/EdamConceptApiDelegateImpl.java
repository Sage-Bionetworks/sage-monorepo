package org.sagebionetworks.openchallenges.challenge.service.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.EdamConceptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EdamConceptApiDelegateImpl implements EdamConceptApiDelegate {

  private final EdamConceptService edamConceptService;

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:edam-concepts')")
  public ResponseEntity<EdamConceptsPageDto> listEdamConcepts(EdamConceptSearchQueryDto query) {
    return ResponseEntity.ok(edamConceptService.listEdamConcepts(query));
  }
}
