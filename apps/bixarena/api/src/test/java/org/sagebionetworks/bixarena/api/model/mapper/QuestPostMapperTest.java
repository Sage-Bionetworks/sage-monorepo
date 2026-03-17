package org.sagebionetworks.bixarena.api.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestPostEntity;

class QuestPostMapperTest {

  private final QuestPostMapper mapper = new QuestPostMapper();

  // ── convertToDto ──────────────────────────────────────────────────────────

  @Test
  @DisplayName("should map all fields when entity is valid")
  void shouldMapAllFieldsWhenEntityIsValid() {
    // given
    QuestPostEntity entity = QuestPostEntity.builder()
        .postIndex(0)
        .date(LocalDate.of(2026, 2, 3))
        .title("Chapter 1")
        .description("The arena walls began to rise.")
        .images("[\"https://example.com/img1.png\",\"https://example.com/img2.png\"]")
        .publishDate(OffsetDateTime.of(2026, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .requiredProgress(500)
        .requiredTier("knight")
        .build();

    // when
    QuestPostDto result = mapper.convertToDto(entity);

    // then
    assertThat(result.getPostIndex()).isEqualTo(0);
    assertThat(result.getDate()).isEqualTo(LocalDate.of(2026, 2, 3));
    assertThat(result.getTitle()).isEqualTo("Chapter 1");
    assertThat(result.getDescription()).isEqualTo("The arena walls began to rise.");
    assertThat(result.getImages()).containsExactly(
        URI.create("https://example.com/img1.png"),
        URI.create("https://example.com/img2.png")
    );
    assertThat(result.getPublishDate()).isEqualTo(entity.getPublishDate());
    assertThat(result.getRequiredProgress()).isEqualTo(500);
    assertThat(result.getRequiredTier()).isEqualTo(QuestPostDto.RequiredTierEnum.KNIGHT);
  }

  @Test
  @DisplayName("should set locked false when converting to dto")
  void shouldSetLockedFalseWhenConvertingToDto() {
    // given
    QuestPostEntity entity = QuestPostEntity.builder()
        .postIndex(0)
        .title("Post")
        .description("Content")
        .images("[]")
        .build();

    // when
    QuestPostDto result = mapper.convertToDto(entity);

    // then
    assertThat(result.getLocked()).isFalse();
  }

  @Test
  @DisplayName("should return null tier when required tier is null")
  void shouldReturnNullTierWhenRequiredTierIsNull() {
    // given
    QuestPostEntity entity = QuestPostEntity.builder()
        .postIndex(0)
        .title("Post")
        .description("Content")
        .images("[]")
        .requiredTier(null)
        .build();

    // when
    QuestPostDto result = mapper.convertToDto(entity);

    // then
    assertThat(result.getRequiredTier()).isNull();
  }

  @Test
  @DisplayName("should return empty list when images is null")
  void shouldReturnEmptyListWhenImagesIsNull() {
    // given
    QuestPostEntity entity = QuestPostEntity.builder()
        .postIndex(0)
        .title("Post")
        .description("Content")
        .images(null)
        .build();

    // when
    QuestPostDto result = mapper.convertToDto(entity);

    // then
    assertThat(result.getImages()).isEmpty();
  }

  @Test
  @DisplayName("should return empty list when images json is malformed")
  void shouldReturnEmptyListWhenImagesJsonIsMalformed() {
    // given
    QuestPostEntity entity = QuestPostEntity.builder()
        .postIndex(0)
        .title("Post")
        .description("Content")
        .images("not valid json")
        .build();

    // when
    QuestPostDto result = mapper.convertToDto(entity);

    // then
    assertThat(result.getImages()).isEmpty();
  }

  // ── convertToLockedDto ────────────────────────────────────────────────────

  @Test
  @DisplayName("should redact content when converting to locked dto")
  void shouldRedactContentWhenConvertingToLockedDto() {
    // given
    QuestPostEntity entity = QuestPostEntity.builder()
        .postIndex(1)
        .date(LocalDate.of(2026, 3, 1))
        .title("Secret Chapter")
        .description("Hidden content that should be redacted")
        .images("[\"https://example.com/secret.png\"]")
        .requiredProgress(1000)
        .requiredTier("champion")
        .build();

    // when
    QuestPostDto result = mapper.convertToLockedDto(entity);

    // then
    assertThat(result.getTitle()).isEqualTo("Secret Chapter");
    assertThat(result.getDescription()).isNull();
    assertThat(result.getImages()).isEmpty();
    assertThat(result.getLocked()).isTrue();
  }

  @Test
  @DisplayName("should preserve gate info when converting to locked dto")
  void shouldPreserveGateInfoWhenConvertingToLockedDto() {
    // given
    QuestPostEntity entity = QuestPostEntity.builder()
        .postIndex(1)
        .title("Gated Post")
        .description("Content")
        .images("[]")
        .requiredProgress(500)
        .requiredTier("knight")
        .build();

    // when
    QuestPostDto result = mapper.convertToLockedDto(entity);

    // then
    assertThat(result.getRequiredProgress()).isEqualTo(500);
    assertThat(result.getRequiredTier()).isEqualTo(QuestPostDto.RequiredTierEnum.KNIGHT);
  }

  // ── convertToEntity ───────────────────────────────────────────────────────

  @Test
  @DisplayName("should map all fields when converting to entity")
  void shouldMapAllFieldsWhenConvertingToEntity() {
    // given
    QuestPostCreateOrUpdateDto dto = QuestPostCreateOrUpdateDto.builder()
        .date(LocalDate.of(2026, 2, 3))
        .title("New Post")
        .description("Post content")
        .images(List.of(URI.create("https://example.com/img.png")))
        .publishDate(OffsetDateTime.of(2026, 3, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .requiredProgress(100)
        .requiredTier(QuestPostCreateOrUpdateDto.RequiredTierEnum.KNIGHT)
        .build();

    // when
    QuestPostEntity result = mapper.convertToEntity(dto, 42L, 3);

    // then
    assertThat(result.getQuestId()).isEqualTo(42L);
    assertThat(result.getPostIndex()).isEqualTo(3);
    assertThat(result.getDate()).isEqualTo(LocalDate.of(2026, 2, 3));
    assertThat(result.getTitle()).isEqualTo("New Post");
    assertThat(result.getDescription()).isEqualTo("Post content");
    assertThat(result.getImages()).isEqualTo("[\"https://example.com/img.png\"]");
    assertThat(result.getPublishDate()).isEqualTo(dto.getPublishDate());
    assertThat(result.getRequiredProgress()).isEqualTo(100);
    assertThat(result.getRequiredTier()).isEqualTo("knight");
  }

  // ── updateEntity ──────────────────────────────────────────────────────────

  @Test
  @DisplayName("should mutate entity when updating")
  void shouldMutateEntityWhenUpdating() {
    // given
    QuestPostEntity entity = QuestPostEntity.builder()
        .questId(1L)
        .postIndex(0)
        .title("Old Title")
        .description("Old description")
        .images("[]")
        .build();

    QuestPostCreateOrUpdateDto dto = QuestPostCreateOrUpdateDto.builder()
        .date(LocalDate.of(2026, 4, 1))
        .title("Updated Title")
        .description("Updated description")
        .images(List.of(URI.create("https://example.com/new.png")))
        .requiredProgress(200)
        .requiredTier(QuestPostCreateOrUpdateDto.RequiredTierEnum.CHAMPION)
        .build();

    // when
    mapper.updateEntity(entity, dto);

    // then
    assertThat(entity.getTitle()).isEqualTo("Updated Title");
    assertThat(entity.getDescription()).isEqualTo("Updated description");
    assertThat(entity.getDate()).isEqualTo(LocalDate.of(2026, 4, 1));
    assertThat(entity.getImages()).isEqualTo("[\"https://example.com/new.png\"]");
    assertThat(entity.getRequiredProgress()).isEqualTo(200);
    assertThat(entity.getRequiredTier()).isEqualTo("champion");
    // questId and postIndex should be unchanged
    assertThat(entity.getQuestId()).isEqualTo(1L);
    assertThat(entity.getPostIndex()).isEqualTo(0);
  }
}
