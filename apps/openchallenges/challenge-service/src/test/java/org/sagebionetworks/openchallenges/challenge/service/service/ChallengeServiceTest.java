package org.sagebionetworks.openchallenges.challenge.service.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeCategoryRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeContributionRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeIncentiveRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeInputDataTypeRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeStarRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeSubmissionTypeRepository;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

  @Mock
  private ChallengeRepository challengeRepository;

  @Mock
  private ChallengeContributionRepository challengeContributionRepository;

  @Mock
  private ChallengeIncentiveRepository challengeIncentiveRepository;

  @Mock
  private ChallengeSubmissionTypeRepository challengeSubmissionTypeRepository;

  @Mock
  private ChallengeCategoryRepository challengeCategoryRepository;

  @Mock
  private ChallengeStarRepository challengeStarRepository;

  @Mock
  private ChallengeInputDataTypeRepository challengeInputDataTypeRepository;

  @InjectMocks
  private ChallengeService challengeService;

  @Test
  @DisplayName("should delete challenge and all child entities when challenge id is valid")
  void shouldDeleteChallengeAndAllChildEntitiesWhenChallengeIdIsValid() {
    // given
    Long challengeId = 1L;
    ChallengeEntity mockChallenge = ChallengeEntity.builder().id(challengeId).build();
    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(mockChallenge));

    // when
    challengeService.deleteChallenge(challengeId);

    // then
    InOrder inOrder = inOrder(
      challengeContributionRepository,
      challengeIncentiveRepository,
      challengeSubmissionTypeRepository,
      challengeCategoryRepository,
      challengeStarRepository,
      challengeInputDataTypeRepository,
      challengeRepository
    );

    // Verify child entities are deleted first (using JPA methods)
    inOrder.verify(challengeContributionRepository).deleteByChallengeId(challengeId);
    inOrder.verify(challengeIncentiveRepository).deleteByChallengeId(challengeId);
    inOrder.verify(challengeSubmissionTypeRepository).deleteByChallengeId(challengeId);
    inOrder.verify(challengeCategoryRepository).deleteByChallengeId(challengeId);
    inOrder.verify(challengeStarRepository).deleteByChallengeId(challengeId);

    // Verify input data types are deleted using JPQL method
    inOrder.verify(challengeInputDataTypeRepository).deleteAllByChallengeId(challengeId);

    // Verify challenge is deleted last
    inOrder.verify(challengeRepository).deleteById(challengeId);

    // Verify no other interactions
    verifyNoMoreInteractions(
      challengeContributionRepository,
      challengeIncentiveRepository,
      challengeSubmissionTypeRepository,
      challengeCategoryRepository,
      challengeStarRepository,
      challengeInputDataTypeRepository,
      challengeRepository
    );
  }

  @Test
  @DisplayName("should call all repository delete methods exactly once when deleting challenge")
  void shouldCallAllRepositoryDeleteMethodsExactlyOnceWhenDeletingChallenge() {
    // given
    Long challengeId = 42L;
    ChallengeEntity mockChallenge = ChallengeEntity.builder().id(challengeId).build();
    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(mockChallenge));

    // when
    challengeService.deleteChallenge(challengeId);

    // then
    verify(challengeContributionRepository).deleteByChallengeId(challengeId);
    verify(challengeIncentiveRepository).deleteByChallengeId(challengeId);
    verify(challengeSubmissionTypeRepository).deleteByChallengeId(challengeId);
    verify(challengeCategoryRepository).deleteByChallengeId(challengeId);
    verify(challengeStarRepository).deleteByChallengeId(challengeId);
    verify(challengeInputDataTypeRepository).deleteAllByChallengeId(challengeId);
    verify(challengeRepository).deleteById(challengeId);
  }

  @Test
  @DisplayName("should use correct method names for JPA and JPQL deletion")
  void shouldUseCorrectMethodNamesForJpaAndJpqlDeletion() {
    // given
    Long challengeId = 123L;
    ChallengeEntity mockChallenge = ChallengeEntity.builder().id(challengeId).build();
    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(mockChallenge));

    // when
    challengeService.deleteChallenge(challengeId);

    // then
    // Verify JPA method-based deletion for entities with @OneToMany(mappedBy = "challenge")
    verify(challengeContributionRepository).deleteByChallengeId(anyLong());
    verify(challengeIncentiveRepository).deleteByChallengeId(anyLong());
    verify(challengeSubmissionTypeRepository).deleteByChallengeId(anyLong());
    verify(challengeCategoryRepository).deleteByChallengeId(anyLong());
    verify(challengeStarRepository).deleteByChallengeId(anyLong());

    // Verify JPQL method for input data types (due to @JoinTable mapping)
    verify(challengeInputDataTypeRepository).deleteAllByChallengeId(anyLong());

    // Verify standard JPA delete for the main entity
    verify(challengeRepository).deleteById(anyLong());
  }
}
