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
    List<String> years =
        Arrays.asList(
            "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017",
            "2018", "2019", "2020", "2021", "2022", "2023");
    List<Integer> challengeCounts =
        Arrays.asList(5, 8, 12, 16, 21, 27, 31, 38, 45, 54, 80, 91, 110, 129, 177, 203, 226);
    Integer undatedChallengeCount = 50;

    return ChallengesPerYearDto.builder()
        .years(years)
        .challengeCounts(challengeCounts)
        .undatedChallengeCount(undatedChallengeCount)
        .build();
  }
}
