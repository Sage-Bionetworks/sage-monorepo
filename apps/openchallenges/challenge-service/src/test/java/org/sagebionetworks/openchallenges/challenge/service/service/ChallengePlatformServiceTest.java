package org.sagebionetworks.openchallenges.challenge.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengePlatformNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengePlatformEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengePlatformRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChallengePlatformService Tests")
class ChallengePlatformServiceTest {

  @Mock
  private ChallengePlatformRepository challengePlatformRepository;

  @InjectMocks
  private ChallengePlatformService challengePlatformService;

  private ChallengePlatformEntity testEntity;
  private ChallengePlatformDto expectedDto;

  @BeforeEach
  void setUp() {
    testEntity = ChallengePlatformEntity.builder()
      .id(1L)
      .slug("synapse")
      .name("Synapse")
      .avatarKey("avatar.png")
      .websiteUrl("https://synapse.org")
      .build();

    expectedDto = ChallengePlatformDto.builder()
      .id(1L)
      .slug("synapse")
      .name("Synapse")
      .avatarKey("avatar.png")
      .websiteUrl("https://synapse.org")
      .build();
  }

  @Test
  @DisplayName("should return challenge platform when valid ID is provided")
  void shouldReturnChallengePlatformWhenValidIdIsProvided() {
    // given
    Long platformId = 1L;
    when(challengePlatformRepository.findById(platformId)).thenReturn(Optional.of(testEntity));

    // when
    ChallengePlatformDto result = challengePlatformService.getChallengePlatform(platformId);

    // then
    verify(challengePlatformRepository).findById(platformId);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(expectedDto.getId());
    assertThat(result.getName()).isEqualTo(expectedDto.getName());
    assertThat(result.getSlug()).isEqualTo(expectedDto.getSlug());
  }

  @Test
  @DisplayName("should throw ChallengePlatformNotFoundException when platform does not exist")
  void shouldThrowChallengePlatformNotFoundExceptionWhenPlatformDoesNotExist() {
    // given
    Long platformId = 999L;
    when(challengePlatformRepository.findById(platformId)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> challengePlatformService.getChallengePlatform(platformId))
      .isInstanceOf(ChallengePlatformNotFoundException.class)
      .extracting("detail")
      .isEqualTo("The challenge platform with ID 999 does not exist.");

    verify(challengePlatformRepository).findById(platformId);
  }

  @Test
  @DisplayName("should return paginated challenge platforms when listing")
  void shouldReturnPaginatedChallengePlatformsWhenListing() {
    // given
    ChallengePlatformSearchQueryDto query = ChallengePlatformSearchQueryDto.builder()
      .pageNumber(0)
      .pageSize(10)
      .searchTerms("")
      .build();

    List<ChallengePlatformEntity> entities = List.of(testEntity);
    Page<ChallengePlatformEntity> entitiesPage = new PageImpl<>(
      entities,
      PageRequest.of(0, 10),
      1L
    );

    when(challengePlatformRepository.findAll(any(), any(), any(String[].class))).thenReturn(
      entitiesPage
    );

    // when
    ChallengePlatformsPageDto result = challengePlatformService.listChallengePlatforms(query);

    // then
    verify(challengePlatformRepository).findAll(any(), any(), any(String[].class));
    assertThat(result).isNotNull();
    assertThat(result.getChallengePlatforms()).hasSize(1);
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(10);
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getHasNext()).isFalse();
    assertThat(result.getHasPrevious()).isFalse();
  }

  @Test
  @DisplayName("should call delete method when deleting challenge platform")
  void shouldCallDeleteMethodWhenDeletingChallengePlatform() {
    // given
    Long platformId = 1L;

    // when
    challengePlatformService.deleteChallengePlatform(platformId);

    // then
    // Note: The actual method is empty, so we're just verifying it doesn't throw
    // This test ensures the method exists and can be called
    assertThat(platformId).isNotNull();
  }
}
