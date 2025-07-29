package org.sagebionetworks.openchallenges.challenge.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import feign.FeignException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.challenge.service.client.OrganizationServiceClient;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeContributionNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.DuplicateContributionException;
import org.sagebionetworks.openchallenges.challenge.service.exception.OrganizationNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.OrganizationDto;
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

  @Mock
  private OrganizationServiceClient organizationServiceClient;

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

    OrganizationDto organization = new OrganizationDto();
    organization.setId(organizationId);
    organization.setName("Test Organization");

    ChallengeContributionEntity savedEntity = ChallengeContributionEntity.builder()
      .id(456L)
      .challenge(challenge)
      .organizationId(organizationId)
      .role(role.getValue())
      .build();

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(organizationServiceClient.getOrganization(organizationId)).thenReturn(organization);
    when(challengeContributionRepository.save(any(ChallengeContributionEntity.class))).thenReturn(
      savedEntity
    );

    // when
    ChallengeContributionDto response = challengeContributionService.createChallengeContribution(
      challengeId,
      request
    );

    // then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(456L);
    assertThat(response.getChallengeId()).isEqualTo(challengeId);
    assertThat(response.getOrganizationId()).isEqualTo(organizationId);
    assertThat(response.getRole()).isEqualTo(role);
    verify(challengeRepository).findById(challengeId);
    verify(organizationServiceClient).getOrganization(organizationId);
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
      challengeContributionService.createChallengeContribution(challengeId, request)
    )
      .isInstanceOf(ChallengeNotFoundException.class)
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

    OrganizationDto organization = new OrganizationDto();
    organization.setId(organizationId);
    organization.setName("Test Organization");

    DataIntegrityViolationException dataIntegrityException = new DataIntegrityViolationException(
      "could not execute statement [ERROR: duplicate key value violates unique constraint \"unique_contribution\"]"
    );

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(organizationServiceClient.getOrganization(organizationId)).thenReturn(organization);
    when(challengeContributionRepository.save(any(ChallengeContributionEntity.class))).thenThrow(
      dataIntegrityException
    );

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.createChallengeContribution(challengeId, request)
    )
      .isInstanceOf(DuplicateContributionException.class)
      .hasMessageContaining("A contribution with role 'challenge_organizer' already exists")
      .hasMessageContaining("organization 123")
      .hasMessageContaining("challenge 1")
      .hasCause(dataIntegrityException);

    verify(challengeRepository).findById(challengeId);
    verify(organizationServiceClient).getOrganization(organizationId);
    verify(challengeContributionRepository).save(any(ChallengeContributionEntity.class));
  }

  @Test
  @DisplayName("should throw exception when organization not found in organization service")
  void shouldThrowExceptionWhenOrganizationNotFoundInOrganizationService() {
    // given
    Long challengeId = 1L;
    Long organizationId = 999L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeContributionCreateRequestDto request = new ChallengeContributionCreateRequestDto(
      organizationId,
      role
    );

    ChallengeEntity challenge = ChallengeEntity.builder()
      .id(challengeId)
      .slug("test-challenge")
      .build();

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(organizationServiceClient.getOrganization(organizationId)).thenThrow(
      FeignException.NotFound.class
    );

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.createChallengeContribution(challengeId, request)
    )
      .isInstanceOf(OrganizationNotFoundException.class)
      .hasMessage("Organization not found with id: " + organizationId);

    verify(challengeRepository).findById(challengeId);
    verify(organizationServiceClient).getOrganization(organizationId);
  }

  @Test
  @DisplayName("should throw runtime exception when organization service fails")
  void shouldThrowRuntimeExceptionWhenOrganizationServiceFails() {
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

    FeignException serviceError = FeignException.errorStatus(
      "getOrganization",
      feign.Response.builder()
        .status(500)
        .reason("Internal Server Error")
        .request(
          feign.Request.create(
            feign.Request.HttpMethod.GET,
            "test",
            java.util.Map.of(),
            null,
            null,
            null
          )
        )
        .build()
    );

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(organizationServiceClient.getOrganization(organizationId)).thenThrow(serviceError);

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.createChallengeContribution(challengeId, request)
    )
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Failed to validate organization with id: 123")
      .hasCause(serviceError);

    verify(challengeRepository).findById(challengeId);
    verify(organizationServiceClient).getOrganization(organizationId);
  }

  @Test
  @DisplayName(
    "should throw organization not found exception when organization service returns 404"
  )
  void shouldThrowOrganizationNotFoundExceptionWhenOrganizationServiceReturns404() {
    // given
    Long challengeId = 1L;
    Long organizationId = 999L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeContributionCreateRequestDto request = new ChallengeContributionCreateRequestDto(
      organizationId,
      role
    );

    ChallengeEntity challenge = ChallengeEntity.builder()
      .id(challengeId)
      .slug("test-challenge")
      .build();

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(organizationServiceClient.getOrganization(organizationId)).thenThrow(
      FeignException.NotFound.class
    );

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.createChallengeContribution(challengeId, request)
    )
      .isInstanceOf(OrganizationNotFoundException.class)
      .hasMessage("Organization not found with id: " + organizationId);

    verify(challengeRepository).findById(challengeId);
    verify(organizationServiceClient).getOrganization(organizationId);
  }

  @Test
  @DisplayName(
    "should throw runtime exception when organization service fails with other feign exception"
  )
  void shouldThrowRuntimeExceptionWhenOrganizationServiceFailsWithOtherFeignException() {
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

    FeignException serviceException = FeignException.errorStatus(
      "getOrganization",
      feign.Response.builder()
        .status(503)
        .reason("Service Unavailable")
        .request(
          feign.Request.create(
            feign.Request.HttpMethod.GET,
            "test",
            java.util.Map.of(),
            null,
            null,
            null
          )
        )
        .build()
    );

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(organizationServiceClient.getOrganization(organizationId)).thenThrow(serviceException);

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.createChallengeContribution(challengeId, request)
    )
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Failed to validate organization with id: " + organizationId)
      .hasCause(serviceException);

    verify(challengeRepository).findById(challengeId);
    verify(organizationServiceClient).getOrganization(organizationId);
  }

  @Test
  @DisplayName("should list challenge contributions with proper id mapping")
  void shouldListChallengeContributionsWithProperIdMapping() {
    // given
    Long challengeId = 1L;
    Long contributionId1 = 101L;
    Long contributionId2 = 102L;
    Long organizationId1 = 201L;
    Long organizationId2 = 202L;

    ChallengeEntity challenge = ChallengeEntity.builder()
      .id(challengeId)
      .slug("test-challenge")
      .build();

    ChallengeContributionEntity entity1 = ChallengeContributionEntity.builder()
      .id(contributionId1)
      .challenge(challenge)
      .organizationId(organizationId1)
      .role("challenge_organizer")
      .build();

    ChallengeContributionEntity entity2 = ChallengeContributionEntity.builder()
      .id(contributionId2)
      .challenge(challenge)
      .organizationId(organizationId2)
      .role("data_contributor")
      .build();

    List<ChallengeContributionEntity> entities = List.of(entity1, entity2);

    when(challengeContributionRepository.findAllByChallengeId(challengeId)).thenReturn(entities);

    // when
    ChallengeContributionsPageDto result = challengeContributionService.listChallengeContributions(
      challengeId
    );

    // then
    assertThat(result).isNotNull();
    assertThat(result.getChallengeContributions()).hasSize(2);

    // Verify first contribution
    ChallengeContributionDto dto1 = result.getChallengeContributions().get(0);
    assertThat(dto1.getId()).isEqualTo(contributionId1);
    assertThat(dto1.getChallengeId()).isEqualTo(challengeId);
    assertThat(dto1.getOrganizationId()).isEqualTo(organizationId1);
    assertThat(dto1.getRole()).isEqualTo(ChallengeContributionRoleDto.CHALLENGE_ORGANIZER);

    // Verify second contribution
    ChallengeContributionDto dto2 = result.getChallengeContributions().get(1);
    assertThat(dto2.getId()).isEqualTo(contributionId2);
    assertThat(dto2.getChallengeId()).isEqualTo(challengeId);
    assertThat(dto2.getOrganizationId()).isEqualTo(organizationId2);
    assertThat(dto2.getRole()).isEqualTo(ChallengeContributionRoleDto.DATA_CONTRIBUTOR);

    // Verify pagination info
    assertThat(result.getSize()).isEqualTo(2);
    assertThat(result.getTotalElements()).isEqualTo(2L);
    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getHasNext()).isFalse();
    assertThat(result.getHasPrevious()).isFalse();

    verify(challengeContributionRepository).findAllByChallengeId(challengeId);
  }

  @Test
  @DisplayName("should delete challenge contribution when valid IDs provided")
  void shouldDeleteChallengeContributionWhenValidIdsProvided() {
    // given
    Long challengeId = 1L;
    Long organizationId = 123L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeEntity challenge = ChallengeEntity.builder()
      .id(challengeId)
      .slug("test-challenge")
      .build();

    ChallengeContributionEntity existingEntity = ChallengeContributionEntity.builder()
      .id(456L)
      .challenge(challenge)
      .organizationId(organizationId)
      .role(role.getValue())
      .build();

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(
      challengeContributionRepository.findByChallengeIdAndOrganizationIdAndRole(
        challengeId,
        organizationId,
        role.getValue()
      )
    ).thenReturn(Optional.of(existingEntity));

    // when
    challengeContributionService.deleteChallengeContribution(challengeId, organizationId, role);

    // then
    verify(challengeRepository).findById(challengeId);
    verify(challengeContributionRepository).findByChallengeIdAndOrganizationIdAndRole(
      challengeId,
      organizationId,
      role.getValue()
    );
    verify(organizationServiceClient).deleteChallengeParticipation(
      String.valueOf(organizationId),
      challengeId,
      role.getValue()
    );
    verify(challengeContributionRepository).delete(existingEntity);
  }

  @Test
  @DisplayName("should throw exception when challenge not found during delete")
  void shouldThrowExceptionWhenChallengeNotFoundDuringDelete() {
    // given
    Long challengeId = 999L;
    Long organizationId = 123L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.deleteChallengeContribution(challengeId, organizationId, role)
    )
      .isInstanceOf(ChallengeNotFoundException.class)
      .hasMessage("Challenge not found with id: " + challengeId);

    verify(challengeRepository).findById(challengeId);
  }

  @Test
  @DisplayName("should throw exception when contribution not found during delete")
  void shouldThrowExceptionWhenContributionNotFoundDuringDelete() {
    // given
    Long challengeId = 1L;
    Long organizationId = 999L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeEntity challenge = ChallengeEntity.builder()
      .id(challengeId)
      .slug("test-challenge")
      .build();

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(
      challengeContributionRepository.findByChallengeIdAndOrganizationIdAndRole(
        challengeId,
        organizationId,
        role.getValue()
      )
    ).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.deleteChallengeContribution(challengeId, organizationId, role)
    )
      .isInstanceOf(ChallengeContributionNotFoundException.class)
      .hasMessage(
        "Challenge contribution not found for challenge " +
        challengeId +
        ", organization " +
        organizationId +
        ", and role " +
        role.getValue()
      );

    verify(challengeRepository).findById(challengeId);
    verify(challengeContributionRepository).findByChallengeIdAndOrganizationIdAndRole(
      challengeId,
      organizationId,
      role.getValue()
    );
  }

  @Test
  @DisplayName("should return challenge contribution when valid IDs are provided")
  void shouldReturnChallengeContributionWhenValidIdsAreProvided() {
    // given
    Long challengeId = 1L;
    Long organizationId = 123L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeEntity challenge = ChallengeEntity.builder()
      .id(challengeId)
      .slug("test-challenge")
      .build();

    ChallengeContributionEntity entity = ChallengeContributionEntity.builder()
      .id(456L)
      .challenge(challenge)
      .organizationId(organizationId)
      .role(role.getValue())
      .build();

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(
      challengeContributionRepository.findByChallengeIdAndOrganizationIdAndRole(
        challengeId,
        organizationId,
        role.getValue()
      )
    ).thenReturn(Optional.of(entity));

    // when
    ChallengeContributionDto result = challengeContributionService.getChallengeContribution(
      challengeId,
      organizationId,
      role
    );

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(456L);
    assertThat(result.getChallengeId()).isEqualTo(challengeId);
    assertThat(result.getOrganizationId()).isEqualTo(organizationId);
    assertThat(result.getRole()).isEqualTo(role);

    verify(challengeRepository).findById(challengeId);
    verify(challengeContributionRepository).findByChallengeIdAndOrganizationIdAndRole(
      challengeId,
      organizationId,
      role.getValue()
    );
  }

  @Test
  @DisplayName("should throw exception when challenge not found for get operation")
  void shouldThrowExceptionWhenChallengeNotFoundForGetOperation() {
    // given
    Long challengeId = 999L;
    Long organizationId = 123L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.getChallengeContribution(challengeId, organizationId, role)
    )
      .isInstanceOf(ChallengeNotFoundException.class)
      .hasMessage("Challenge not found with id: " + challengeId);

    verify(challengeRepository).findById(challengeId);
  }

  @Test
  @DisplayName("should throw exception when contribution not found for get operation")
  void shouldThrowExceptionWhenContributionNotFoundForGetOperation() {
    // given
    Long challengeId = 1L;
    Long organizationId = 999L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeEntity challenge = ChallengeEntity.builder()
      .id(challengeId)
      .slug("test-challenge")
      .build();

    when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
    when(
      challengeContributionRepository.findByChallengeIdAndOrganizationIdAndRole(
        challengeId,
        organizationId,
        role.getValue()
      )
    ).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
      challengeContributionService.getChallengeContribution(challengeId, organizationId, role)
    )
      .isInstanceOf(ChallengeContributionNotFoundException.class)
      .hasMessage(
        "Challenge contribution not found for challenge " +
        challengeId +
        ", organization " +
        organizationId +
        ", and role " +
        role.getValue()
      );

    verify(challengeRepository).findById(challengeId);
    verify(challengeContributionRepository).findByChallengeIdAndOrganizationIdAndRole(
      challengeId,
      organizationId,
      role.getValue()
    );
  }
}
