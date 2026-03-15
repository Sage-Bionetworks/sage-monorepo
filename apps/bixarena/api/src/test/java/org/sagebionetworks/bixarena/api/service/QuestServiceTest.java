package org.sagebionetworks.bixarena.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.bixarena.api.exception.DuplicateQuestException;
import org.sagebionetworks.bixarena.api.exception.QuestNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.QuestCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;
import org.sagebionetworks.bixarena.api.model.entity.QuestPostEntity;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestPostRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestRepository;

@ExtendWith(MockitoExtension.class)
class QuestServiceTest {

  @Mock
  private QuestRepository questRepository;

  @Mock
  private QuestPostRepository questPostRepository;

  @Mock
  private BattleRepository battleRepository;

  @InjectMocks
  private QuestService questService;

  private QuestEntity questEntity;

  @BeforeEach
  void setUp() {
    questEntity = QuestEntity.builder()
        .id(1L)
        .questId("test-quest")
        .title("Test Quest")
        .description("A test quest")
        .goal(100)
        .startDate(OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .endDate(OffsetDateTime.of(2026, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC))
        .activePostIndex(0)
        .build();
  }

  // ── createQuest ───────────────────────────────────────────────────────────

  @Test
  @DisplayName("should save and return dto when creating quest")
  void shouldSaveAndReturnDtoWhenCreatingQuest() {
    // given
    QuestCreateOrUpdateDto dto = QuestCreateOrUpdateDto.builder()
        .questId("new-quest")
        .title("New Quest")
        .description("Description")
        .goal(50)
        .startDate(OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .endDate(OffsetDateTime.of(2026, 6, 30, 0, 0, 0, 0, ZoneOffset.UTC))
        .activePostIndex(0)
        .build();

    when(questRepository.findByQuestId("new-quest")).thenReturn(Optional.empty());
    when(questRepository.save(any(QuestEntity.class))).thenAnswer(invocation -> {
      QuestEntity saved = invocation.getArgument(0);
      saved.setId(1L);
      return saved;
    });

    // when
    QuestDto result = questService.createQuest(dto);

    // then
    assertThat(result.getQuestId()).isEqualTo("new-quest");
    assertThat(result.getTitle()).isEqualTo("New Quest");
    assertThat(result.getPosts()).isEmpty();
    assertThat(result.getTotalBlocks()).isEqualTo(0);
    verify(questRepository).save(any(QuestEntity.class));
  }

  @Test
  @DisplayName("should throw duplicate exception when quest id exists")
  void shouldThrowDuplicateExceptionWhenQuestIdExists() {
    // given
    QuestCreateOrUpdateDto dto = QuestCreateOrUpdateDto.builder()
        .questId("test-quest")
        .title("Duplicate")
        .description("Dup")
        .goal(0)
        .startDate(OffsetDateTime.now(ZoneOffset.UTC))
        .endDate(OffsetDateTime.now(ZoneOffset.UTC))
        .activePostIndex(0)
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));

    // when/then
    assertThatThrownBy(() -> questService.createQuest(dto))
        .isInstanceOf(DuplicateQuestException.class);
  }

  // ── getQuest ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("should return published unlocked posts when anonymous")
  void shouldReturnPublishedUnlockedPostsWhenAnonymous() {
    // given
    QuestPostEntity publishedPost = QuestPostEntity.builder()
        .postIndex(0)
        .title("Published Post")
        .description("Visible content")
        .images("[]")
        .publishDate(null) // no publish gate
        .requiredProgress(null) // no progress gate
        .requiredTier(null) // no tier gate
        .build();

    QuestPostEntity futurePost = QuestPostEntity.builder()
        .postIndex(1)
        .title("Future Post")
        .description("Not yet visible")
        .images("[]")
        .publishDate(OffsetDateTime.of(2099, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(List.of(publishedPost, futurePost));
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(10L);

    // when
    QuestDto result = questService.getQuest("test-quest", null);

    // then — future post is excluded
    assertThat(result.getPosts()).hasSize(1);
    assertThat(result.getPosts().get(0).getTitle()).isEqualTo("Published Post");
    assertThat(result.getPosts().get(0).getLocked()).isFalse();
  }

  @Test
  @DisplayName("should return all posts with full content when ungated")
  void shouldReturnAllPostsWithFullContentWhenUngated() {
    // given
    QuestPostEntity post1 = QuestPostEntity.builder()
        .postIndex(0)
        .title("Post 1")
        .description("Content 1")
        .images("[]")
        .build();

    QuestPostEntity lockedPost = QuestPostEntity.builder()
        .postIndex(1)
        .title("Locked Post")
        .description("Secret content")
        .images("[\"https://example.com/img.png\"]")
        .requiredProgress(9999)
        .requiredTier("champion")
        .publishDate(OffsetDateTime.of(2099, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(List.of(post1, lockedPost));
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(0L);

    // when
    QuestDto result = questService.getQuestUngated("test-quest");

    // then — all posts returned with full content, regardless of gates
    assertThat(result.getPosts()).hasSize(2);
    assertThat(result.getPosts().get(0).getDescription()).isEqualTo("Content 1");
    assertThat(result.getPosts().get(1).getDescription()).isEqualTo("Secret content");
    assertThat(result.getPosts().get(1).getLocked()).isFalse();
  }

  // ── deleteQuest ───────────────────────────────────────────────────────────

  @Test
  @DisplayName("should delete posts then quest when deleting quest")
  void shouldDeletePostsThenQuestWhenDeletingQuest() {
    // given
    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));

    // when
    questService.deleteQuest("test-quest");

    // then — posts deleted first, then quest
    verify(questPostRepository).deleteByQuestId(1L);
    verify(questRepository).delete(questEntity);
  }

  @Test
  @DisplayName("should throw not found when quest does not exist")
  void shouldThrowNotFoundWhenQuestDoesNotExist() {
    // given
    when(questRepository.findByQuestId("nonexistent")).thenReturn(Optional.empty());

    // when/then
    assertThatThrownBy(() -> questService.getQuest("nonexistent", null))
        .isInstanceOf(QuestNotFoundException.class);
  }
}
