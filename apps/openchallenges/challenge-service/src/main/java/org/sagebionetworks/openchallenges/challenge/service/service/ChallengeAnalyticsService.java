package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.ArrayList;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPerYearDto;
import org.sagebionetworks.openchallenges.challenge.service.model.projection.YearlyChallengeCount;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeAnalyticsService {

  private static final Logger logger = LoggerFactory.getLogger(ChallengeAnalyticsService.class);

  private final ChallengeRepository challengeRepository;

  public ChallengeAnalyticsService(ChallengeRepository challengeRepository) {
    this.challengeRepository = challengeRepository;
  }

  @Transactional(readOnly = true)
  public ChallengesPerYearDto getChallengesPerYear() {
    List<YearlyChallengeCount> results = challengeRepository.findChallengeCountsByYear();
    int undatedChallengeCount = challengeRepository.countUndatedChallenges();

    logger.debug("undatedChallengeCount: {}", undatedChallengeCount);

    List<String> years = new ArrayList<>();
    List<Integer> challengeCounts = new ArrayList<>();

    for (YearlyChallengeCount row : results) {
      years.add(String.valueOf(row.getYear()));
      challengeCounts.add(row.getCount());
    }

    return ChallengesPerYearDto.builder()
      .years(years)
      .challengeCounts(challengeCounts)
      .undatedChallengeCount(undatedChallengeCount)
      .build();
  }
}
