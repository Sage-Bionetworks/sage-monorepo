/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.1.0).
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
import org.sagebionetworks.challenge.model.dto.ErrorDto;
import org.sagebionetworks.challenge.model.dto.OrganizationsPageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "Organization", description = "Operations about organizations")
@RequestMapping("${openapi.challengeOrganization.base-path:}")
public interface OrganizationApi {

  default OrganizationApiDelegate getDelegate() {
    return new OrganizationApiDelegate() {};
  }

  /**
   * GET /organizations : Get all organizations Returns the organizations
   *
   * @param pageNumber The page number (optional, default to 0)
   * @param pageSize The number of items in a single page (optional, default to 100)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   */
  @Operation(
      operationId = "listOrganizations",
      summary = "Get all organizations",
      tags = {"Organization"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Success",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = OrganizationsPageDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorDto.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "The request cannot be fulfilled due to an unexpected server error",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorDto.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/organizations",
      produces = {"application/json"})
  default ResponseEntity<OrganizationsPageDto> listOrganizations(
      @Min(0)
          @Parameter(name = "pageNumber", description = "The page number")
          @Valid
          @RequestParam(value = "pageNumber", required = false, defaultValue = "0")
          Integer pageNumber,
      @Min(10)
          @Parameter(name = "pageSize", description = "The number of items in a single page")
          @Valid
          @RequestParam(value = "pageSize", required = false, defaultValue = "100")
          Integer pageSize) {
    return getDelegate().listOrganizations(pageNumber, pageSize);
  }
}
