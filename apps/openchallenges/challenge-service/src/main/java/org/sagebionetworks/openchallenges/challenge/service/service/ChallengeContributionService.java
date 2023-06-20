package org.sagebionetworks.openchallenges.challenge.service.service;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.springframework.stereotype.Service;

@Service
public class ChallengeContributionService {

  public ChallengeContributionsPageDto listChallengeContributions(Long challengeId) {

    return ChallengeContributionsPageDto.builder().build();
  }
}
