package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.ArrayList;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPerYearDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeAnalyticsService {

  @Transactional(readOnly = true)
  public ChallengesPerYearDto getChallengesPerYear() {

    List<String> years = new ArrayList<String>();
    List<Integer> challengeCounts = new ArrayList<Integer>();

    return ChallengesPerYearDto.builder().years(years).challengeCounts(challengeCounts).build();
  }
}
