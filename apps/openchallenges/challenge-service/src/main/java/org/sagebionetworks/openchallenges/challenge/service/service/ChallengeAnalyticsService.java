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
            "2018", "2019", "2020", "2021", "2022", "2023", "2024");
    List<Integer> challengeCounts =
        Arrays.asList(6, 9, 13, 17, 23, 29, 34, 41, 49, 59, 86, 97, 116, 135, 183, 242, 302, 303);
    Integer undatedChallengeCount = 160;

    return ChallengesPerYearDto.builder()
        .years(years)
        .challengeCounts(challengeCounts)
        .undatedChallengeCount(undatedChallengeCount)
        .build();
  }
}
