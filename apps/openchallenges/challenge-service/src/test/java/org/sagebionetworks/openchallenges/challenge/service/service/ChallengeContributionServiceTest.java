package org.sagebionetworks.openchallenges.challenge.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.challenge.service.exception.DuplicateContributionException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateResponseDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeContributionRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeRepository;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ChallengeContributionServiceTest {

  @Mock
  private ChallengeContributionRepository challengeContributionRepository;

  @Mock
  private ChallengeRepository challengeRepository;

  @InjectMocks
  private ChallengeContributionService challengeContributionService;

  @Test
  @DisplayName("should create challenge contribution when valid request provided")
  void shouldCreateChallengeContributionWhenValidRequestProvided() {
    // given
    Long challengeId = 1L;
    Long organizationId = 123L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeContributionCreateRequestDto request = new ChallengeContributionCreateRequestDto(
      organizationId,
      role
    );

    ChallengeEntity challenge = ChallengeEntity.builder()
      .id(challengeId)
      .slug("test-challenge")
      .build();

    ChallengeContributionEntity savedEntity = ChallengeContributionEntity.builder()
      .id(456L)
      .challenge(challenge)
      .organizationId(organizationId)
      .role(role.getValue())
      .build();

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(challengeContributionRepository.save(any(ChallengeContributionEntity.class))).thenReturn(
      savedEntity
    );

    // when
    ChallengeContributionCreateResponseDto response =
      challengeContributionService.addChallengeContribution(challengeId, request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(456L);
    verify(challengeRepository).findById(challengeId);
    verify(challengeContributionRepository).save(any(ChallengeContributionEntity.class));
  }

  @Test
  @DisplayName("should throw exception when challenge not found")
  void shouldThrowExceptionWhenChallengeNotFound() {
    // given
    Long challengeId = 999L;
    Long organizationId = 123L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeContributionCreateRequestDto request = new ChallengeContributionCreateRequestDto(
      organizationId,
      role
    );

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.addChallengeContribution(challengeId, request)
    )
      .isInstanceOf(RuntimeException.class)
      .hasMessage("Challenge not found with id: " + challengeId);

    verify(challengeRepository).findById(challengeId);
  }

  @Test
  @DisplayName("should throw duplicate contribution exception when unique constraint is violated")
  void shouldThrowDuplicateContributionExceptionWhenUniqueConstraintIsViolated() {
    // given
    Long challengeId = 1L;
    Long organizationId = 123L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeContributionCreateRequestDto request = new ChallengeContributionCreateRequestDto(
      organizationId,
      role
    );

    ChallengeEntity challenge = ChallengeEntity.builder()
      .id(challengeId)
      .slug("test-challenge")
      .build();

    DataIntegrityViolationException dataIntegrityException = new DataIntegrityViolationException(
      "could not execute statement [ERROR: duplicate key value violates unique constraint \"unique_contribution\"]"
    );

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(challengeContributionRepository.save(any(ChallengeContributionEntity.class))).thenThrow(
      dataIntegrityException
    );

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.addChallengeContribution(challengeId, request)
    )
      .isInstanceOf(DuplicateContributionException.class)
      .hasMessageContaining("A contribution with role 'challenge_organizer' already exists")
      .hasMessageContaining("organization 123")
      .hasMessageContaining("challenge 1")
      .hasCause(dataIntegrityException);

    verify(challengeRepository).findById(challengeId);
    verify(challengeContributionRepository).save(any(ChallengeContributionEntity.class));
  }
}
