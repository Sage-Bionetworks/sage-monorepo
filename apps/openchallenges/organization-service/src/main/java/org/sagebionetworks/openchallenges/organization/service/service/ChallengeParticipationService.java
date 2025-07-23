package org.sagebionetworks.openchallenges.organization.service.service;

import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationCreateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationRoleDto;
import org.springframework.stereotype.Service;

@Service
public class ChallengeParticipationService {

  public ChallengeParticipationDto createChallengeParticipation(
    String org,
    ChallengeParticipationCreateRequestDto requestDto
  ) {
    // TODO: Implement actual creation logic
    return new ChallengeParticipationDto();
  }

  public void deleteChallengeParticipation(
    String org,
    Long challengeId,
    ChallengeParticipationRoleDto role
  ) {
    // TODO: Implement actual deletion logic
  }
}
