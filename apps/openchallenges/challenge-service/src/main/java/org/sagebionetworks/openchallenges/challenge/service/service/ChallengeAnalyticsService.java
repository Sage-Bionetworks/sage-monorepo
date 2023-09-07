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
            "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021",
            "2022", "2023");
    List<Integer> challengeCounts =
        Arrays.asList(1, 7, 11, 17, 24, 33, 58, 69, 88, 106, 148, 170, 188);

    return ChallengesPerYearDto.builder().years(years).challengeCounts(challengeCounts).build();
  }
}
