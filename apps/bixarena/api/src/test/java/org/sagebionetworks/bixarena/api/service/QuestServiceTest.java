package org.sagebionetworks.bixarena.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.bixarena.api.exception.DuplicateQuestException;
import org.sagebionetworks.bixarena.api.exception.QuestNotFoundException;
import org.sagebionetworks.bixarena.api.exception.QuestPostNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorsDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostReorderDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;
import org.sagebionetworks.bixarena.api.model.entity.QuestPostEntity;
import org.sagebionetworks.bixarena.api.model.projection.ContributorProjection;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestPostRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestRepository;
import org.springframework.data.domain.PageRequest;

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

  // ── getQuest — gating logic ───────────────────────────────────────────────

  @Test
  @DisplayName("should exclude post when publish date is in future")
  void shouldExcludePostWhenPublishDateIsInFuture() {
    // given
    QuestPostEntity futurePost = QuestPostEntity.builder()
        .postIndex(0)
        .title("Future")
        .description("Not yet")
        .images("[]")
        .publishDate(OffsetDateTime.of(2099, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(List.of(futurePost));
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(0L);

    // when
    QuestDto result = questService.getQuest("test-quest", null);

    // then
    assertThat(result.getPosts()).isEmpty();
  }

  @Test
  @DisplayName("should lock post when progress is below threshold")
  void shouldLockPostWhenProgressIsBelowThreshold() {
    // given
    QuestPostEntity gatedPost = QuestPostEntity.builder()
        .postIndex(0)
        .title("Progress-Gated")
        .description("Secret")
        .images("[]")
        .requiredProgress(100)
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(List.of(gatedPost));
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(50L); // below 100

    // when
    QuestDto result = questService.getQuest("test-quest", null);

    // then
    assertThat(result.getPosts()).hasSize(1);
    assertThat(result.getPosts().get(0).getLocked()).isTrue();
    assertThat(result.getPosts().get(0).getDescription()).isNull();
  }

  @Test
  @DisplayName("should unlock post when progress is above threshold")
  void shouldUnlockPostWhenProgressIsAboveThreshold() {
    // given
    QuestPostEntity gatedPost = QuestPostEntity.builder()
        .postIndex(0)
        .title("Progress-Gated")
        .description("Revealed")
        .images("[]")
        .requiredProgress(100)
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(List.of(gatedPost));
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(200L); // above 100

    // when
    QuestDto result = questService.getQuest("test-quest", null);

    // then
    assertThat(result.getPosts()).hasSize(1);
    assertThat(result.getPosts().get(0).getLocked()).isFalse();
    assertThat(result.getPosts().get(0).getDescription()).isEqualTo("Revealed");
  }

  @Test
  @DisplayName("should lock post when user tier is below required")
  void shouldLockPostWhenUserTierIsBelowRequired() {
    // given — user has 3 battles/week (apprentice), post requires knight (>=5/wk)
    UUID userId = UUID.randomUUID();

    QuestPostEntity tierGatedPost = QuestPostEntity.builder()
        .postIndex(0)
        .title("Knight-Only")
        .description("Secret")
        .images("[]")
        .requiredTier("knight")
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(List.of(tierGatedPost));
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(0L);
    // 3 battles over ~52 weeks = ~0.06 battles/week → below knight threshold
    when(battleRepository.countCompletedByUserIdAndDateRange(eq(userId), any(), any()))
        .thenReturn(3L);

    // when
    QuestDto result = questService.getQuest("test-quest", userId);

    // then
    assertThat(result.getPosts()).hasSize(1);
    assertThat(result.getPosts().get(0).getLocked()).isTrue();
  }

  @Test
  @DisplayName("should unlock post when user tier matches required")
  void shouldUnlockPostWhenUserTierMatchesRequired() {
    // given — quest runs 1 week, user has 7 battles → 7/wk = knight
    QuestEntity shortQuest = QuestEntity.builder()
        .id(2L)
        .questId("short-quest")
        .title("Short")
        .description("Short quest")
        .goal(10)
        .startDate(OffsetDateTime.of(2026, 3, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .endDate(OffsetDateTime.of(2026, 3, 8, 0, 0, 0, 0, ZoneOffset.UTC))
        .activePostIndex(0)
        .build();

    UUID userId = UUID.randomUUID();

    QuestPostEntity tierGatedPost = QuestPostEntity.builder()
        .postIndex(0)
        .title("Knight-Only")
        .description("Welcome, Knight!")
        .images("[]")
        .requiredTier("knight")
        .build();

    when(questRepository.findByQuestId("short-quest")).thenReturn(Optional.of(shortQuest));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(2L))
        .thenReturn(List.of(tierGatedPost));
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(0L);
    // 7 battles over 1 week = 7.0 battles/wk → knight (>=5)
    when(battleRepository.countCompletedByUserIdAndDateRange(eq(userId), any(), any()))
        .thenReturn(7L);

    // when
    QuestDto result = questService.getQuest("short-quest", userId);

    // then
    assertThat(result.getPosts()).hasSize(1);
    assertThat(result.getPosts().get(0).getLocked()).isFalse();
    assertThat(result.getPosts().get(0).getDescription()).isEqualTo("Welcome, Knight!");
  }

  @Test
  @DisplayName("should unlock post when user tier exceeds required")
  void shouldUnlockPostWhenUserTierExceedsRequired() {
    // given — quest runs 1 week, user has 15 battles → 15/wk = champion, post requires knight
    QuestEntity shortQuest = QuestEntity.builder()
        .id(2L)
        .questId("short-quest")
        .title("Short")
        .description("Short quest")
        .goal(10)
        .startDate(OffsetDateTime.of(2026, 3, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .endDate(OffsetDateTime.of(2026, 3, 8, 0, 0, 0, 0, ZoneOffset.UTC))
        .activePostIndex(0)
        .build();

    UUID userId = UUID.randomUUID();

    QuestPostEntity tierGatedPost = QuestPostEntity.builder()
        .postIndex(0)
        .title("Knight-Only")
        .description("Champion can see this too")
        .images("[]")
        .requiredTier("knight")
        .build();

    when(questRepository.findByQuestId("short-quest")).thenReturn(Optional.of(shortQuest));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(2L))
        .thenReturn(List.of(tierGatedPost));
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(0L);
    // 15 battles over 1 week = 15.0 battles/wk → champion (>=10)
    when(battleRepository.countCompletedByUserIdAndDateRange(eq(userId), any(), any()))
        .thenReturn(15L);

    // when
    QuestDto result = questService.getQuest("short-quest", userId);

    // then
    assertThat(result.getPosts()).hasSize(1);
    assertThat(result.getPosts().get(0).getLocked()).isFalse();
  }

  // ── createQuestPost ───────────────────────────────────────────────────────

  @Test
  @DisplayName("should append at next index when creating post")
  void shouldAppendAtNextIndexWhenCreatingPost() {
    // given — existing posts have max index 2
    QuestPostCreateOrUpdateDto dto = QuestPostCreateOrUpdateDto.builder()
        .title("New Post")
        .description("Content")
        .images(List.of())
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findMaxPostIndex(1L)).thenReturn(2);
    when(questPostRepository.save(any(QuestPostEntity.class))).thenAnswer(invocation -> {
      QuestPostEntity saved = invocation.getArgument(0);
      saved.setId(10L);
      return saved;
    });

    // when
    QuestPostDto result = questService.createQuestPost("test-quest", dto);

    // then — appended at index 3
    assertThat(result.getPostIndex()).isEqualTo(3);
    assertThat(result.getTitle()).isEqualTo("New Post");
  }

  // ── deleteQuestPost — reindexing & activePostIndex ────────────────────────

  @Test
  @DisplayName("should reindex remaining posts when deleting post")
  void shouldReindexRemainingPostsWhenDeletingPost() {
    // given — posts [0, 1, 2], delete index 1
    QuestPostEntity post0 = QuestPostEntity.builder()
        .id(10L).questId(1L).postIndex(0).title("Post 0")
        .description("A").images("[]").build();
    QuestPostEntity post1 = QuestPostEntity.builder()
        .id(11L).questId(1L).postIndex(1).title("Post 1")
        .description("B").images("[]").build();
    QuestPostEntity post2 = QuestPostEntity.builder()
        .id(12L).questId(1L).postIndex(2).title("Post 2")
        .description("C").images("[]").build();

    questEntity.setActivePostIndex(0); // before deleted post

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdAndPostIndex(1L, 1)).thenReturn(Optional.of(post1));
    // After delete, remaining posts returned in order
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(new ArrayList<>(List.of(post0, post2)));
    when(questPostRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    questService.deleteQuestPost("test-quest", 1);

    // then — verify delete was called
    verify(questPostRepository).delete(post1);
    // After reindexing, post2 should have been assigned index 1 (final pass)
    // The two-pass algorithm first sets negatives, then 0-based contiguous
  }

  @Test
  @DisplayName("should set active to previous when deleting active post")
  void shouldSetActiveToPreviousWhenDeletingActivePost() {
    // given — activePostIndex=2, delete post at index 2
    questEntity.setActivePostIndex(2);

    QuestPostEntity post2 = QuestPostEntity.builder()
        .id(12L).questId(1L).postIndex(2).title("Active Post")
        .description("X").images("[]").build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdAndPostIndex(1L, 2)).thenReturn(Optional.of(post2));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(new ArrayList<>(List.of(
            QuestPostEntity.builder().id(10L).questId(1L).postIndex(0).title("P0")
                .description("A").images("[]").build(),
            QuestPostEntity.builder().id(11L).questId(1L).postIndex(1).title("P1")
                .description("B").images("[]").build()
        )));
    when(questPostRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    questService.deleteQuestPost("test-quest", 2);

    // then — activePostIndex should be set to previous (2-1=1)
    assertThat(questEntity.getActivePostIndex()).isEqualTo(1);
    verify(questRepository).save(questEntity);
  }

  @Test
  @DisplayName("should decrement active when deleting post before active")
  void shouldDecrementActiveWhenDeletingPostBeforeActive() {
    // given — activePostIndex=2, delete post at index 1
    questEntity.setActivePostIndex(2);

    QuestPostEntity post1 = QuestPostEntity.builder()
        .id(11L).questId(1L).postIndex(1).title("Middle")
        .description("X").images("[]").build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdAndPostIndex(1L, 1)).thenReturn(Optional.of(post1));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(new ArrayList<>(List.of(
            QuestPostEntity.builder().id(10L).questId(1L).postIndex(0).title("P0")
                .description("A").images("[]").build(),
            QuestPostEntity.builder().id(12L).questId(1L).postIndex(2).title("P2")
                .description("C").images("[]").build()
        )));
    when(questPostRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    questService.deleteQuestPost("test-quest", 1);

    // then — activePostIndex shifts down: 2 → 1
    assertThat(questEntity.getActivePostIndex()).isEqualTo(1);
    verify(questRepository).save(questEntity);
  }

  @Test
  @DisplayName("should keep active when deleting post after active")
  void shouldKeepActiveWhenDeletingPostAfterActive() {
    // given — activePostIndex=0, delete post at index 2
    questEntity.setActivePostIndex(0);

    QuestPostEntity post2 = QuestPostEntity.builder()
        .id(12L).questId(1L).postIndex(2).title("Last")
        .description("X").images("[]").build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdAndPostIndex(1L, 2)).thenReturn(Optional.of(post2));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(new ArrayList<>(List.of(
            QuestPostEntity.builder().id(10L).questId(1L).postIndex(0).title("P0")
                .description("A").images("[]").build(),
            QuestPostEntity.builder().id(11L).questId(1L).postIndex(1).title("P1")
                .description("B").images("[]").build()
        )));
    when(questPostRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    questService.deleteQuestPost("test-quest", 2);

    // then — activePostIndex unchanged (0 < 2, no shift needed)
    assertThat(questEntity.getActivePostIndex()).isEqualTo(0);
  }

  // ── reorderQuestPosts ─────────────────────────────────────────────────────

  @Test
  @DisplayName("should reindex posts when reordering")
  void shouldReindexPostsWhenReordering() {
    // given — posts [0, 1, 2], reorder to [2, 0, 1]
    QuestPostEntity post0 = QuestPostEntity.builder()
        .id(10L).questId(1L).postIndex(0).title("P0")
        .description("A").images("[]").build();
    QuestPostEntity post1 = QuestPostEntity.builder()
        .id(11L).questId(1L).postIndex(1).title("P1")
        .description("B").images("[]").build();
    QuestPostEntity post2 = QuestPostEntity.builder()
        .id(12L).questId(1L).postIndex(2).title("P2")
        .description("C").images("[]").build();

    QuestPostReorderDto dto = QuestPostReorderDto.builder()
        .postIndexes(List.of(2, 0, 1))
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(new ArrayList<>(List.of(post0, post1, post2)))
        // Second call after reorder for the return value
        .thenReturn(new ArrayList<>(List.of(post2, post0, post1)));
    when(questPostRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(0L);

    // when
    QuestDto result = questService.reorderQuestPosts("test-quest", dto);

    // then
    assertThat(result).isNotNull();
    verify(questPostRepository).flush();
  }

  @Test
  @DisplayName("should throw exception when reorder indexes mismatch")
  void shouldThrowExceptionWhenReorderIndexesMismatch() {
    // given — posts [0, 1], but request has [0, 1, 2]
    QuestPostEntity post0 = QuestPostEntity.builder()
        .id(10L).questId(1L).postIndex(0).title("P0")
        .description("A").images("[]").build();
    QuestPostEntity post1 = QuestPostEntity.builder()
        .id(11L).questId(1L).postIndex(1).title("P1")
        .description("B").images("[]").build();

    QuestPostReorderDto dto = QuestPostReorderDto.builder()
        .postIndexes(List.of(0, 1, 2))
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(new ArrayList<>(List.of(post0, post1)));

    // when/then
    assertThatThrownBy(() -> questService.reorderQuestPosts("test-quest", dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("exactly all existing post indexes");
  }

  @Test
  @DisplayName("should throw exception when reorder has duplicate indexes")
  void shouldThrowExceptionWhenReorderHasDuplicateIndexes() {
    // given
    QuestPostEntity post0 = QuestPostEntity.builder()
        .id(10L).questId(1L).postIndex(0).title("P0")
        .description("A").images("[]").build();
    QuestPostEntity post1 = QuestPostEntity.builder()
        .id(11L).questId(1L).postIndex(1).title("P1")
        .description("B").images("[]").build();

    QuestPostReorderDto dto = QuestPostReorderDto.builder()
        .postIndexes(List.of(0, 0))
        .build();

    when(questRepository.findByQuestId("test-quest")).thenReturn(Optional.of(questEntity));
    when(questPostRepository.findByQuestIdOrderByPostIndexAsc(1L))
        .thenReturn(new ArrayList<>(List.of(post0, post1)));

    // when/then
    assertThatThrownBy(() -> questService.reorderQuestPosts("test-quest", dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Duplicate");
  }

  // ── listQuestContributors ─────────────────────────────────────────────────

  @Test
  @DisplayName("should calculate tiers correctly when listing contributors")
  void shouldCalculateTiersCorrectlyWhenListingContributors() {
    // given — quest runs 1 week (7 days)
    QuestEntity weekQuest = QuestEntity.builder()
        .id(3L)
        .questId("week-quest")
        .title("Week Quest")
        .description("One week")
        .goal(100)
        .startDate(OffsetDateTime.of(2026, 3, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .endDate(OffsetDateTime.of(2026, 3, 8, 0, 0, 0, 0, ZoneOffset.UTC))
        .activePostIndex(0)
        .build();

    // Champion: 10 battles/week, Knight: 5 battles/week, Apprentice: 2 battles/week
    ContributorProjection champion = new ContributorProjection() {
      @Override public String getUsername() { return "champion-user"; }
      @Override public Integer getBattleCount() { return 10; }
    };
    ContributorProjection knight = new ContributorProjection() {
      @Override public String getUsername() { return "knight-user"; }
      @Override public Integer getBattleCount() { return 5; }
    };
    ContributorProjection apprentice = new ContributorProjection() {
      @Override public String getUsername() { return "apprentice-user"; }
      @Override public Integer getBattleCount() { return 2; }
    };

    when(questRepository.findByQuestId("week-quest")).thenReturn(Optional.of(weekQuest));
    when(battleRepository.findContributorsByDateRange(any(), any(), eq(1), any()))
        .thenReturn(List.of(champion, knight, apprentice));

    // when
    QuestContributorsDto result = questService.listQuestContributors("week-quest", 1, 100);

    // then
    assertThat(result.getContributors()).hasSize(3);
    assertThat(result.getContributors().get(0).getTier().getValue()).isEqualTo("champion");
    assertThat(result.getContributors().get(1).getTier().getValue()).isEqualTo("knight");
    assertThat(result.getContributors().get(2).getTier().getValue()).isEqualTo("apprentice");
    assertThat(result.getQuestId()).isEqualTo("week-quest");
    assertThat(result.getTotalContributors()).isEqualTo(3);
  }
}
