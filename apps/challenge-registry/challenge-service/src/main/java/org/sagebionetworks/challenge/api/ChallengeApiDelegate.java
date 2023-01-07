package org.sagebionetworks.challenge.api;

import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.challenge.model.dto.ChallengeDifficultyDto;
import org.sagebionetworks.challenge.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.sagebionetworks.challenge.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.challenge.model.dto.ChallengesPageDto;
import org.sagebionetworks.challenge.model.dto.DateRangeDto;
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
   * GET /challenges : List challenges List challenges
   *
   * @param pageNumber The page number (optional, default to 0)
   * @param pageSize The number of items in a single page (optional, default to 100)
   * @param searchTerms A string of search terms used to filter the results. (optional)
   * @param status An array of challenge status used to filter the results. (optional)
   * @param platforms An array of challenge platform ids used to filter the results. (optional)
   * @param difficulties An array of challenge difficulty levels used to filter the results.
   *     (optional)
   * @param submissionTypes An array of challenge submission types used to filter the results.
   *     (optional)
   * @param incentives An array of challenge incentive types used to filter the results. (optional)
   * @param startDateRange Return challenges that start during the date range specified. (optional)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   * @see ChallengeApi#listChallenges
   */
  default ResponseEntity<ChallengesPageDto> listChallenges(
      Integer pageNumber,
      Integer pageSize,
      String searchTerms,
      List<ChallengeStatusDto> status,
      List<String> platforms,
      List<ChallengeDifficultyDto> difficulties,
      List<ChallengeSubmissionTypeDto> submissionTypes,
      List<ChallengeIncentiveDto> incentives,
      DateRangeDto startDateRange) {
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
