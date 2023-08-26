package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPerYearDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeAnalyticsService {

  @Transactional(readOnly = true)
  public ChallengesPerYearDto getChallengesPerYear() {
    List<String> years = Arrays.asList("2010", "2011");
    List<Integer> challengeCounts = Arrays.asList(100, 200);

    return ChallengesPerYearDto.builder().years(years).challengeCounts(challengeCounts).build();
  }
}
