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
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.sagebionetworks.challenge.model.dto.BasicErrorDto;
import org.sagebionetworks.challenge.model.dto.ChallengePlatformDto;
import org.sagebionetworks.challenge.model.dto.ChallengePlatformsPageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "ChallengePlatform", description = "Operations about challenge platforms.")
public interface ChallengePlatformApi {

  default ChallengePlatformApiDelegate getDelegate() {
    return new ChallengePlatformApiDelegate() {};
  }

  /**
   * GET /challengePlatforms/{challengePlatformId} : Get a challenge platform Returns the challenge
   * platform specified
   *
   * @param challengePlatformId The unique identifier of the challenge platform. (required)
   * @return Success (status code 200) or The specified resource was not found (status code 404) or
   *     The request cannot be fulfilled due to an unexpected server error (status code 500)
   */
  @Operation(
      operationId = "getChallengePlatform",
      summary = "Get a challenge platform",
      tags = {"ChallengePlatform"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Success",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ChallengePlatformDto.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ChallengePlatformDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "The specified resource was not found",
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
      value = "/challengePlatforms/{challengePlatformId}",
      produces = {"application/json", "application/problem+json"})
  default ResponseEntity<ChallengePlatformDto> getChallengePlatform(
      @Parameter(
              name = "challengePlatformId",
              description = "The unique identifier of the challenge platform.",
              required = true)
          @PathVariable("challengePlatformId")
          Long challengePlatformId) {
    return getDelegate().getChallengePlatform(challengePlatformId);
  }

  /**
   * GET /challengePlatforms : List challenge platforms List challenge platforms
   *
   * @param pageNumber The page number (optional, default to 0)
   * @param pageSize The number of items in a single page (optional, default to 100)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   */
  @Operation(
      operationId = "listChallengePlatforms",
      summary = "List challenge platforms",
      tags = {"ChallengePlatform"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Success",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ChallengePlatformsPageDto.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ChallengePlatformsPageDto.class))
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
      value = "/challengePlatforms",
      produces = {"application/json", "application/problem+json"})
  default ResponseEntity<ChallengePlatformsPageDto> listChallengePlatforms(
      @Min(0)
          @Parameter(name = "pageNumber", description = "The page number")
          @Valid
          @RequestParam(value = "pageNumber", required = false, defaultValue = "0")
          Integer pageNumber,
      @Min(1)
          @Parameter(name = "pageSize", description = "The number of items in a single page")
          @Valid
          @RequestParam(value = "pageSize", required = false, defaultValue = "100")
          Integer pageSize) {
    return getDelegate().listChallengePlatforms(pageNumber, pageSize);
  }
}
