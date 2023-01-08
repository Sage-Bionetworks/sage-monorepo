/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.2.1).
 * https://openapi-generator.tech Do not edit the class manually.
 */
package org.sagebionetworks.challenge.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.sagebionetworks.challenge.model.dto.BasicErrorDto;
import org.sagebionetworks.challenge.model.dto.ChallengeDifficultyDto;
import org.sagebionetworks.challenge.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.sagebionetworks.challenge.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.challenge.model.dto.ChallengesPageDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "Challenge", description = "Operations about challenges.")
public interface ChallengeApi {

  default ChallengeApiDelegate getDelegate() {
    return new ChallengeApiDelegate() {};
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
   * @param minStartDate Keep the challenges that start at this date or later. (optional)
   * @param maxStartDate Keep the challenges that start at this date or sooner. (optional)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   */
  @Operation(
      operationId = "listChallenges",
      summary = "List challenges",
      tags = {"Challenge"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Success",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ChallengesPageDto.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ChallengesPageDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BasicErrorDto.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = BasicErrorDto.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "The request cannot be fulfilled due to an unexpected server error",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BasicErrorDto.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = BasicErrorDto.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/challenges",
      produces = {"application/json", "application/problem+json"})
  default ResponseEntity<ChallengesPageDto> listChallenges(
      @Min(0)
          @Parameter(name = "pageNumber", description = "The page number")
          @Valid
          @RequestParam(value = "pageNumber", required = false, defaultValue = "0")
          Integer pageNumber,
      @Min(1)
          @Parameter(name = "pageSize", description = "The number of items in a single page")
          @Valid
          @RequestParam(value = "pageSize", required = false, defaultValue = "100")
          Integer pageSize,
      @Parameter(
              name = "searchTerms",
              description = "A string of search terms used to filter the results.")
          @Valid
          @RequestParam(value = "searchTerms", required = false)
          String searchTerms,
      @Parameter(
              name = "status",
              description = "An array of challenge status used to filter the results.")
          @Valid
          @RequestParam(value = "status", required = false)
          List<ChallengeStatusDto> status,
      @Parameter(
              name = "platforms",
              description = "An array of challenge platform ids used to filter the results.")
          @Valid
          @RequestParam(value = "platforms", required = false)
          List<String> platforms,
      @Parameter(
              name = "difficulties",
              description = "An array of challenge difficulty levels used to filter the results.")
          @Valid
          @RequestParam(value = "difficulties", required = false)
          List<ChallengeDifficultyDto> difficulties,
      @Parameter(
              name = "submissionTypes",
              description = "An array of challenge submission types used to filter the results.")
          @Valid
          @RequestParam(value = "submissionTypes", required = false)
          List<ChallengeSubmissionTypeDto> submissionTypes,
      @Parameter(
              name = "incentives",
              description = "An array of challenge incentive types used to filter the results.")
          @Valid
          @RequestParam(value = "incentives", required = false)
          List<ChallengeIncentiveDto> incentives,
      @Parameter(
              name = "minStartDate",
              description = "Keep the challenges that start at this date or later.")
          @Valid
          @RequestParam(value = "minStartDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate minStartDate,
      @Parameter(
              name = "maxStartDate",
              description = "Keep the challenges that start at this date or sooner.")
          @Valid
          @RequestParam(value = "maxStartDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate maxStartDate) {
    return getDelegate()
        .listChallenges(
            pageNumber,
            pageSize,
            searchTerms,
            status,
            platforms,
            difficulties,
            submissionTypes,
            incentives,
            minStartDate,
            maxStartDate);
  }
}
