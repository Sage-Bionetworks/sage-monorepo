/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.2.1).
 * https://openapi-generator.tech Do not edit the class manually.
 */
package org.sagebionetworks.openchallenges.organization.service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.sagebionetworks.openchallenges.organization.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "Organization", description = "Operations about organizations")
public interface OrganizationApi {

  default OrganizationApiDelegate getDelegate() {
    return new OrganizationApiDelegate() {};
  }

  /**
   * GET /organizations/{org} : Get an organization Returns the organization specified
   *
   * @param org The unique identifier of the organization. (required)
   * @return An organization (status code 200) or The specified resource was not found (status code
   *     404) or The request cannot be fulfilled due to an unexpected server error (status code 500)
   */
  @Operation(
      operationId = "getOrganization",
      summary = "Get an organization",
      tags = {"Organization"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "An organization",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = OrganizationDto.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = OrganizationDto.class))
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
      value = "/organizations/{org}",
      produces = {"application/json", "application/problem+json"})
  default ResponseEntity<OrganizationDto> getOrganization(
      @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
          @Size(min = 2, max = 64)
          @Parameter(
              name = "org",
              description = "The unique identifier of the organization.",
              required = true)
          @PathVariable("org")
          String org) {
    return getDelegate().getOrganization(org);
  }

  /**
   * GET /organizations : List organizations List organizations
   *
   * @param organizationSearchQuery The search query used to find organizations. (optional)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   */
  @Operation(
      operationId = "listOrganizations",
      summary = "List organizations",
      tags = {"Organization"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Success",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = OrganizationsPageDto.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = OrganizationsPageDto.class))
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
      value = "/organizations",
      produces = {"application/json", "application/problem+json"})
  default ResponseEntity<OrganizationsPageDto> listOrganizations(
      @Parameter(
              name = "organizationSearchQuery",
              description = "The search query used to find organizations.")
          @Valid
          OrganizationSearchQueryDto organizationSearchQuery) {
    return getDelegate().listOrganizations(organizationSearchQuery);
  }
}
