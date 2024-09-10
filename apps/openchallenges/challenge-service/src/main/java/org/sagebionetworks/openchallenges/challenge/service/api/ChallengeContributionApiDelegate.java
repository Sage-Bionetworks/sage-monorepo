package org.sagebionetworks.openchallenges.challenge.service.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link ChallengeContributionApiController}}. Implement this
 * interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ChallengeContributionApiDelegate {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  /**
   * GET /challenges/{challengeId}/contributions : List challenge contributions List challenge
   * contributions
   *
   * @param challengeId The unique identifier of the challenge. (required)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   * @see ChallengeContributionApi#listChallengeContributions
   */
  default ResponseEntity<ChallengeContributionsPageDto> listChallengeContributions(
    Long challengeId
  ) {
    getRequest()
      .ifPresent(request -> {
        for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
            String exampleString = "null";
            ApiUtil.setExampleResponse(request, "application/json", exampleString);
            break;
          }
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
            String exampleString =
              "Custom MIME type example not yet supported: application/problem+json";
            ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
            break;
          }
        }
      });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
