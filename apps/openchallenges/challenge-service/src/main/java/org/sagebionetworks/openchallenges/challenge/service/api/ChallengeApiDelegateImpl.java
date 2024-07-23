package org.sagebionetworks.openchallenges.challenge.service.api;

import java.util.Optional;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
public class ChallengeApiDelegateImpl implements ChallengeApiDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChallengeApiDelegateImpl.class);

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
  public ResponseEntity<ChallengeDto> getChallenge(Long challengeId) {
    LOGGER.info("getChallenge()");
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                LOGGER.info("mediaType: {}", mediaType);
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                  LOGGER.info("Requested application/json");
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/ld+json"))) {
                  LOGGER.info("Requested application/ld+json");
                }
              }
            });
    return ResponseEntity.ok(challengeService.getChallenge(challengeId));
  }

  @Override
  public ResponseEntity<ChallengesPageDto> listChallenges(ChallengeSearchQueryDto query) {
    return ResponseEntity.ok(challengeService.listChallenges(query));
  }
}
