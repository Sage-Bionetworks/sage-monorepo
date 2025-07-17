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
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPageDto;
import org.sagebionetworks.openchallenges.challenge.service.security.AuthenticatedUser;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.NativeWebRequest;

@ExtendWith(MockitoExtension.class)
class ChallengeApiDelegateImplTest {

  @Mock
  private ChallengeService challengeService;

  @Mock
  private NativeWebRequest request;

  @InjectMocks
  private ChallengeApiDelegateImpl apiDelegate;

  @BeforeEach
  void setUp() {
    // Set up authenticated admin user in security context
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
  @DisplayName("should delete challenge and return no content status when challenge id is valid")
  void shouldDeleteChallengeAndReturnNoContentStatusWhenChallengeIdIsValid() {
    // given
    Long challengeId = 1L;

    // when
    ResponseEntity<Void> response = apiDelegate.deleteChallengeById(challengeId);

    // then
    verify(challengeService).deleteChallenge(challengeId);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
  }

  @Test
  @DisplayName("should return challenge when challenge id is valid")
  void shouldReturnChallengeWhenChallengeIdIsValid() {
    // given
    Long challengeId = 1L;
    ChallengeDto expectedChallenge = ChallengeDto.builder()
      .id(challengeId)
      .name("Test Challenge")
      .build();
    when(challengeService.getChallenge(challengeId)).thenReturn(expectedChallenge);

    // when
    ResponseEntity<ChallengeDto> response = apiDelegate.getChallenge(challengeId);

    // then
    verify(challengeService).getChallenge(challengeId);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedChallenge);
  }

  @Test
  @DisplayName("should return challenges page when listing challenges with query")
  void shouldReturnChallengesPageWhenListingChallengesWithQuery() {
    // given
    ChallengeSearchQueryDto query = ChallengeSearchQueryDto.builder()
      .pageNumber(0)
      .pageSize(10)
      .build();
    ChallengesPageDto expectedPage = ChallengesPageDto.builder()
      .number(0)
      .size(10)
      .totalElements(1L)
      .totalPages(1)
      .build();
    when(challengeService.listChallenges(query)).thenReturn(expectedPage);

    // when
    ResponseEntity<ChallengesPageDto> response = apiDelegate.listChallenges(query);

    // then
    verify(challengeService).listChallenges(query);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedPage);
  }

  @Test
  @DisplayName("should return request when getRequest is called")
  void shouldReturnRequestWhenGetRequestIsCalled() {
    // when
    var result = apiDelegate.getRequest();

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(request);
  }
}
