package org.sagebionetworks.openchallenges.challenge.service.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPerYearDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link ChallengeAnalyticsApiController}}. Implement this interface
 * with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ChallengeAnalyticsApiDelegate {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  /**
   * GET /challengeAnalytics/challengesPerYear : Get the number of challenges tracked per year
   * Returns the number of challenges tracked per year
   *
   * @return An object (status code 200) or The request cannot be fulfilled due to an unexpected
   *     server error (status code 500)
   * @see ChallengeAnalyticsApi#getChallengesPerYear
   */
  default ResponseEntity<ChallengesPerYearDto> getChallengesPerYear() {
    getRequest()
      .ifPresent(request -> {
        for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
            String exampleString =
              "{ \"undatedChallengeCount\" : 0, \"challengeCounts\" : [ 0, 0 ], \"years\" : [ \"years\", \"years\" ] }";
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
