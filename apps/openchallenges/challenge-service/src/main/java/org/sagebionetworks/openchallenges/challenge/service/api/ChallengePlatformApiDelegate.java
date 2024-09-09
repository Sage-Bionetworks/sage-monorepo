package org.sagebionetworks.openchallenges.challenge.service.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformsPageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link ChallengePlatformApiController}}. Implement this interface
 * with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ChallengePlatformApiDelegate {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  /**
   * GET /challengePlatforms/{challengePlatformName} : Get a challenge platform Returns the
   * challenge platform specified
   *
   * @param challengePlatformName The unique identifier of the challenge platform. (required)
   * @return Success (status code 200) or The specified resource was not found (status code 404) or
   *     The request cannot be fulfilled due to an unexpected server error (status code 500)
   * @see ChallengePlatformApi#getChallengePlatform
   */
  default ResponseEntity<ChallengePlatformDto> getChallengePlatform(String challengePlatformName) {
    getRequest()
      .ifPresent(request -> {
        for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
            String exampleString =
              "{ \"createdAt\" : \"2022-07-04T22:19:11Z\", \"avatarUrl\" : \"https://via.placeholder.com/300.png\", \"websiteUrl\" : \"https://example.com\", \"name\" : \"name\", \"id\" : 1, \"slug\" : \"example-challenge-platform\", \"updatedAt\" : \"2022-07-04T22:19:11Z\" }";
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

  /**
   * GET /challengePlatforms : List challenge platforms List challenge platforms
   *
   * @param challengePlatformSearchQuery The search query used to find challenge platforms.
   *     (optional)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   * @see ChallengePlatformApi#listChallengePlatforms
   */
  default ResponseEntity<ChallengePlatformsPageDto> listChallengePlatforms(
    ChallengePlatformSearchQueryDto challengePlatformSearchQuery
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
