package org.sagebionetworks.bixarena.api.api;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorsDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostReorderDto;
import org.sagebionetworks.bixarena.api.service.QuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<QuestDto> createQuest(QuestCreateOrUpdateDto questCreateOrUpdateDto) {
    log.info("Creating quest: {}", questCreateOrUpdateDto.getQuestId());
    QuestDto quest = questService.createQuest(questCreateOrUpdateDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(quest);
  }

  @Override
  public ResponseEntity<QuestDto> getQuest(String questId) {
    log.debug("getQuest called: questId={}", questId);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = extractUserId(auth);

    QuestDto quest = questService.getQuest(questId, userId);
    return ResponseEntity.ok(quest);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<QuestDto> updateQuest(
      String questId, QuestCreateOrUpdateDto questCreateOrUpdateDto) {
    log.info("Updating quest: {}", questId);
    QuestDto quest = questService.updateQuest(questId, questCreateOrUpdateDto);
    return ResponseEntity.ok(quest);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteQuest(String questId) {
    log.info("Deleting quest: {}", questId);
    questService.deleteQuest(questId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<QuestPostDto> createQuestPost(
      String questId, QuestPostCreateOrUpdateDto questPostCreateOrUpdateDto) {
    log.info("Creating post for quest: {}", questId);
    QuestPostDto post = questService.createQuestPost(questId, questPostCreateOrUpdateDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(post);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<QuestPostDto> updateQuestPost(
      String questId, Integer postIndex,
      QuestPostCreateOrUpdateDto questPostCreateOrUpdateDto) {
    log.info("Updating post {} in quest: {}", postIndex, questId);
    QuestPostDto post =
        questService.updateQuestPost(questId, postIndex, questPostCreateOrUpdateDto);
    return ResponseEntity.ok(post);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteQuestPost(String questId, Integer postIndex) {
    log.info("Deleting post {} from quest: {}", postIndex, questId);
    questService.deleteQuestPost(questId, postIndex);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<QuestDto> reorderQuestPosts(
      String questId, QuestPostReorderDto questPostReorderDto) {
    log.info("Reordering posts in quest: {}", questId);
    QuestDto quest = questService.reorderQuestPosts(questId, questPostReorderDto);
    return ResponseEntity.ok(quest);
  }

  @Override
  public ResponseEntity<QuestContributorsDto> listQuestContributors(
      String questId, Integer minBattles, Integer limit) {

    log.debug(
        "listQuestContributors called: questId={}, minBattles={}, limit={}",
        questId,
        minBattles,
        limit);

    int minBattlesValue = (minBattles != null) ? minBattles : 1;
    int limitValue = (limit != null) ? limit : 100;

    if (minBattlesValue < 1) {
      throw new IllegalArgumentException("minBattles must be at least 1");
    }

    if (limitValue < 1) {
      throw new IllegalArgumentException("limit must be at least 1");
    }

    if (limitValue > 1000) {
      throw new IllegalArgumentException("limit must not exceed 1000");
    }

    QuestContributorsDto contributors =
        questService.listQuestContributors(questId, minBattlesValue, limitValue);

    return ResponseEntity.ok(contributors);
  }

  private UUID extractUserId(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()
        || "anonymousUser".equals(auth.getPrincipal())) {
      return null;
    }
    try {
      return UUID.fromString(auth.getName());
    } catch (IllegalArgumentException e) {
      log.warn("Could not parse user ID from authentication: {}", auth.getName());
      return null;
    }
  }

}
