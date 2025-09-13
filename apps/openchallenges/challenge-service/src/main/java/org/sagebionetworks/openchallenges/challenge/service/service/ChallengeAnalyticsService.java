package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPerYearDto;
import org.sagebionetworks.openchallenges.challenge.service.model.projection.YearlyChallengeCount;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChallengeAnalyticsService {

  private final ChallengeRepository challengeRepository;

  @Transactional(readOnly = true)
  public ChallengesPerYearDto getChallengesPerYear() {
    List<YearlyChallengeCount> results = challengeRepository.findChallengeCountsByYear();
    int undatedChallengeCount = challengeRepository.countUndatedChallenges();

    log.debug("undatedChallengeCount: {}", undatedChallengeCount);

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
