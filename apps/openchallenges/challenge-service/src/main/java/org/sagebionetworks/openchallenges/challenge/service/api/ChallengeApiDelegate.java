package org.sagebionetworks.openchallenges.challenge.service.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link ChallengeApiController}}. Implement this interface with a
 * {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ChallengeApiDelegate {

  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  /**
   * GET /challenges/{challengeId} : Get a challenge Returns the challenge specified
   *
   * @param challengeId The unique identifier of the challenge. (required)
   * @return A challenge (status code 200) or The specified resource was not found (status code 404)
   *     or The request cannot be fulfilled due to an unexpected server error (status code 500)
   * @see ChallengeApi#getChallenge
   */
  default ResponseEntity<ChallengeDto> getChallenge(Long challengeId) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                  String exampleString =
                      "{ \"avatarUrl\" : \"https://openchallenges.io\", \"endDate\" : \"2017-07-21T00:00:00.000+00:00\", \"description\" : \"This is an example description of the challenge.\", \"platform\" : { \"name\" : \"name\", \"id\" : 1, \"slug\" : \"example-challenge-platform\" }, \"starredCount\" : 100, \"createdAt\" : \"2022-07-04T22:19:11Z\", \"incentives\" : [ \"publication\", \"publication\" ], \"submissionTypes\" : [ \"container_image\", \"container_image\" ], \"websiteUrl\" : \"https://openchallenges.io\", \"name\" : \"name\", \"id\" : 1, \"categories\" : [ \"featured\", \"featured\" ], \"headline\" : \"Example challenge headline\", \"slug\" : \"awesome-challenge\", \"startDate\" : \"2017-07-21T00:00:00.000+00:00\", \"doi\" : \"https://doi.org/123/abc\", \"status\" : \"active\", \"inputDataTypes\" : [ { \"name\" : \"gene expression\", \"id\" : 1, \"slug\" : \"gene-expression\" }, { \"name\" : \"gene expression\", \"id\" : 1, \"slug\" : \"gene-expression\" } ], \"updatedAt\" : \"2022-07-04T22:19:11Z\" }";
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
   * GET /challenges : List challenges List challenges
   *
   * @param challengeSearchQuery The search query used to find challenges. (optional)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   * @see ChallengeApi#listChallenges
   */
  default ResponseEntity<ChallengesPageDto> listChallenges(
      ChallengeSearchQueryDto challengeSearchQuery) {
    getRequest()
        .ifPresent(
            request -> {
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
