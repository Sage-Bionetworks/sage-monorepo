package org.sagebionetworks.bixarena.api.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorsDto;
import org.sagebionetworks.bixarena.api.service.QuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * Implementation of the Quest API delegate.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QuestApiDelegateImpl implements QuestApiDelegate {

  private final QuestService questService;

  @Override
  public ResponseEntity<QuestContributorsDto> listQuestContributors(
      String questId, Integer minBattles, Integer limit) {

    log.debug(
        "listQuestContributors called: questId={}, minBattles={}, limit={}",
        questId,
        minBattles,
        limit);

    // Apply defaults if not provided
    int minBattlesValue = (minBattles != null) ? minBattles : 1;
    int limitValue = (limit != null) ? limit : 100;

    // Validate parameter bounds (as defined in OpenAPI spec)
    if (minBattlesValue < 1) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "minBattles must be at least 1");
    }

    if (limitValue < 1) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "limit must be at least 1");
    }

    if (limitValue > 1000) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "limit must not exceed 1000");
    }

    QuestContributorsDto contributors =
        questService.listQuestContributors(questId, minBattlesValue, limitValue);

    return ResponseEntity.ok(contributors);
  }
}
