/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.2.1).
 * https://openapi-generator.tech Do not edit the class manually.
 */
package org.sagebionetworks.openchallenges.image.service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.sagebionetworks.openchallenges.image.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "Image", description = "Operations about images")
public interface ImageApi {

  default ImageApiDelegate getDelegate() {
    return new ImageApiDelegate() {};
  }

  /**
   * GET /images/{image} : Get an image Returns the image specified
   *
   * @param image The unique identifier of the image. (required)
   * @return An image (status code 200) or The specified resource was not found (status code 404) or
   *     The request cannot be fulfilled due to an unexpected server error (status code 500)
   */
  @Operation(
      operationId = "getImage",
      summary = "Get an image",
      tags = {"Image"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "An image",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ImageDto.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ImageDto.class))
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
      value = "/images/{image}",
      produces = {"application/json", "application/problem+json"})
  default ResponseEntity<ImageDto> getImage(
      @Parameter(
              name = "image",
              description = "The unique identifier of the image.",
              required = true)
          @PathVariable("image")
          String image) {
    return getDelegate().getImage(image);
  }
}
