package org.sagebionetworks.openchallenges.challenge.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.security.AuthenticatedUser;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeContributionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ChallengeContributionApiDelegateImplTest {

  @Mock
  private ChallengeContributionService challengeContributionService;

  @InjectMocks
  private ChallengeContributionApiDelegateImpl apiDelegate;

  @BeforeEach
  void setUp() {
    // Set up authenticated user in security context
    AuthenticatedUser authenticatedUser = new AuthenticatedUser(
      java.util.UUID.randomUUID(),
      "admin-user",
      "admin",
      List.of("challenges:write")
    );
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(
      authenticatedUser,
      null
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  @DisplayName("should create challenge contribution and return created status")
  void shouldCreateChallengeContributionAndReturnCreatedStatus() {
    // given
    Long challengeId = 1L;
    Long organizationId = 123L;
    ChallengeContributionRoleDto role = ChallengeContributionRoleDto.CHALLENGE_ORGANIZER;

    ChallengeContributionCreateRequestDto request = new ChallengeContributionCreateRequestDto(
      organizationId,
      role
    );

    ChallengeContributionDto expectedResponse = new ChallengeContributionDto()
      .id(456L)
      .challengeId(challengeId)
      .organizationId(organizationId)
      .role(role);

    when(challengeContributionService.createChallengeContribution(challengeId, request)).thenReturn(
      expectedResponse
    );

    // when
    ResponseEntity<ChallengeContributionDto> response = apiDelegate.createChallengeContribution(
      challengeId,
      request
    );

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(456L);
    assertThat(response.getBody().getChallengeId()).isEqualTo(challengeId);
    assertThat(response.getBody().getOrganizationId()).isEqualTo(organizationId);
    assertThat(response.getBody().getRole()).isEqualTo(role);
    verify(challengeContributionService).createChallengeContribution(challengeId, request);
  }

  @Test
  @DisplayName("should delete challenge contribution and return no content status")
  void shouldDeleteChallengeContributionAndReturnNoContentStatus() {
    // given
    Long challengeId = 1L;
    Long contributionId = 456L;

    // when
    ResponseEntity<Void> response = apiDelegate.deleteChallengeContribution(
      challengeId,
      contributionId
    );

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(challengeContributionService).deleteChallengeContribution(challengeId, contributionId);
  }

  @Test
  @DisplayName("should get challenge contribution and return ok status")
  void shouldGetChallengeContributionAndReturnOkStatus() {
    // given
    Long challengeId = 1L;
    Long contributionId = 456L;

    ChallengeContributionDto expectedResponse = new ChallengeContributionDto()
      .id(contributionId)
      .challengeId(challengeId)
      .organizationId(123L)
      .role(ChallengeContributionRoleDto.CHALLENGE_ORGANIZER);

    when(
      challengeContributionService.getChallengeContribution(challengeId, contributionId)
    ).thenReturn(expectedResponse);

    // when
    ResponseEntity<ChallengeContributionDto> response = apiDelegate.getChallengeContribution(
      challengeId,
      contributionId
    );

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(contributionId);
    assertThat(response.getBody().getChallengeId()).isEqualTo(challengeId);
    assertThat(response.getBody().getOrganizationId()).isEqualTo(123L);
    assertThat(response.getBody().getRole()).isEqualTo(
      ChallengeContributionRoleDto.CHALLENGE_ORGANIZER
    );
    verify(challengeContributionService).getChallengeContribution(challengeId, contributionId);
  }
}
