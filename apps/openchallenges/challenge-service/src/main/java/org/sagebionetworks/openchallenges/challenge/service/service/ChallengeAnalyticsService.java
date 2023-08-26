package org.sagebionetworks.openchallenges.challenge.service.service;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPerYearDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeAnalyticsService {
  
  @Transactional(readOnly = true)
  public ChallengesPerYearDto getChallengesPerYear() {
    return null;
  }
}
