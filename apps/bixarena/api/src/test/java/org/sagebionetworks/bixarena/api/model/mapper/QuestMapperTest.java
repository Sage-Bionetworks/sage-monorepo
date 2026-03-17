package org.sagebionetworks.bixarena.api.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.bixarena.api.model.dto.QuestDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;

class QuestMapperTest {

  private final QuestMapper mapper = new QuestMapper();

  @Test
  @DisplayName("should map all fields when entity is valid")
  void shouldMapAllFieldsWhenEntityIsValid() {
    // given
    QuestEntity entity = QuestEntity.builder()
        .id(1L)
        .questId("test-quest")
        .title("Test Quest")
        .description("A test quest")
        .goal(100)
        .startDate(OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .endDate(OffsetDateTime.of(2026, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC))
        .activePostIndex(2)
        .build();

    List<QuestPostDto> posts = List.of(
        QuestPostDto.builder().postIndex(0).title("Post 0").build(),
        QuestPostDto.builder().postIndex(1).title("Post 1").build()
    );

    Integer totalBlocks = 42;

    // when
    QuestDto result = mapper.convertToDto(entity, posts, totalBlocks);

    // then
    assertThat(result.getQuestId()).isEqualTo("test-quest");
    assertThat(result.getTitle()).isEqualTo("Test Quest");
    assertThat(result.getDescription()).isEqualTo("A test quest");
    assertThat(result.getGoal()).isEqualTo(100);
    assertThat(result.getStartDate()).isEqualTo(entity.getStartDate());
    assertThat(result.getEndDate()).isEqualTo(entity.getEndDate());
    assertThat(result.getActivePostIndex()).isEqualTo(2);
    assertThat(result.getTotalBlocks()).isEqualTo(42);
    assertThat(result.getPosts()).hasSize(2);
    assertThat(result.getPosts()).isSameAs(posts);
  }

  @Test
  @DisplayName("should return null when entity is null")
  void shouldReturnNullWhenEntityIsNull() {
    // when
    QuestDto result = mapper.convertToDto(null, List.of(), 0);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("should return empty list when posts are empty")
  void shouldReturnEmptyListWhenPostsAreEmpty() {
    // given
    QuestEntity entity = QuestEntity.builder()
        .questId("empty-quest")
        .title("Empty Quest")
        .description("No posts")
        .goal(0)
        .startDate(OffsetDateTime.now(ZoneOffset.UTC))
        .endDate(OffsetDateTime.now(ZoneOffset.UTC))
        .activePostIndex(0)
        .build();

    // when
    QuestDto result = mapper.convertToDto(entity, Collections.emptyList(), 0);

    // then
    assertThat(result.getPosts()).isEmpty();
  }
}
