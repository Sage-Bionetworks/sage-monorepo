package org.sagebionetworks.bixarena.api.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorsDto;
import org.sagebionetworks.bixarena.api.service.QuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Quest API delegate.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QuestApiDelegateImpl implements QuestApiDelegate {

  private final QuestService questService;

  @Override
  public ResponseEntity<QuestContributorsDto> getQuestContributors(
      String questId, Integer minBattles, Integer limit) {

    log.debug(
        "getQuestContributors called: questId={}, minBattles={}, limit={}",
        questId,
        minBattles,
        limit);

    // Apply defaults if not provided
    int minBattlesValue = (minBattles != null) ? minBattles : 1;
    int limitValue = (limit != null) ? limit : 100;

    QuestContributorsDto contributors =
        questService.getQuestContributors(questId, minBattlesValue, limitValue);

    return ResponseEntity.ok(contributors);
  }
}
