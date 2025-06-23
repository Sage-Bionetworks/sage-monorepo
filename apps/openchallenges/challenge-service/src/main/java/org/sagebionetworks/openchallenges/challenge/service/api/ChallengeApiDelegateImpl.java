package org.sagebionetworks.openchallenges.challenge.service.api;

import java.util.List;
import java.util.Optional;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
public class ChallengeApiDelegateImpl implements ChallengeApiDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChallengeApiDelegateImpl.class);

  private static final MediaType APPLICATION_LD_JSON = MediaType.valueOf("application/ld+json");
  private static final MediaType APPLICATION_JSON = MediaType.valueOf("application/json");

  private final ChallengeService challengeService;

  private final NativeWebRequest request;

  public ChallengeApiDelegateImpl(ChallengeService challengeService, NativeWebRequest request) {
    this.challengeService = challengeService;
    this.request = request;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<?> getChallenge(Long challengeId) {
    for (MediaType mediaType : getAcceptedMediaTypes(getRequest())) {
      if (mediaType.isCompatibleWith(APPLICATION_LD_JSON)) {
        return ResponseEntity.ok(challengeService.getChallengeJsonLd(challengeId));
      }
      if (mediaType.isCompatibleWith(APPLICATION_JSON)) {
        return ResponseEntity.ok(challengeService.getChallenge(challengeId));
      }
    }
    // Return 406 Not Acceptable if no supported media type is found
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
      "Unsupported media type. Supported types: application/json, application/ld+json"
    );
  }

  @Override
  public ResponseEntity<ChallengesPageDto> listChallenges(ChallengeSearchQueryDto query) {
    return ResponseEntity.ok(challengeService.listChallenges(query));
  }

  public List<MediaType> getAcceptedMediaTypes(Optional<NativeWebRequest> requestOpt) {
    return requestOpt
      .map(request -> MediaType.parseMediaTypes(request.getHeader("Accept")))
      .orElseGet(List::of);
  }
}
