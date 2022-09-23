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
import java.util.List;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.sagebionetworks.challenge.model.dto.PageableDto;
import org.sagebionetworks.challenge.model.dto.UserDto;
import org.sagebionetworks.challenge.model.dto.UserUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "User", description = "Operations about users")
@RequestMapping("${openapi.challengeUser.base-path:}")
public interface UserApi {

  default UserApiDelegate getDelegate() {
    return new UserApiDelegate() {};
  }

  /**
   * POST /api/v1/users/register
   *
   * @param userDto (required)
   * @return OK (status code 200)
   */
  @Operation(
      operationId = "createUser",
      tags = {"User"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
              @Content(mediaType = "*/*", schema = @Schema(implementation = UserDto.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/api/v1/users/register",
      produces = {"*/*"},
      consumes = {"application/json"})
  default ResponseEntity<UserDto> createUser(
      @Parameter(name = "UserDto", description = "", required = true) @Valid @RequestBody
          UserDto userDto) {
    return getDelegate().createUser(userDto);
  }

  /**
   * GET /api/v1/users/{id}
   *
   * @param id (required)
   * @return OK (status code 200)
   */
  @Operation(
      operationId = "getUser",
      tags = {"User"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
              @Content(mediaType = "*/*", schema = @Schema(implementation = UserDto.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/api/v1/users/{id}",
      produces = {"*/*"})
  default ResponseEntity<UserDto> getUser(
      @Parameter(name = "id", description = "", required = true) @PathVariable("id") Long id) {
    return getDelegate().getUser(id);
  }

  /**
   * GET /api/v1/users/
   *
   * @param pageable (optional)
   * @return OK (status code 200)
   */
  @Operation(
      operationId = "listUsers",
      tags = {"User"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
              @Content(mediaType = "*/*", schema = @Schema(implementation = UserDto.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/api/v1/users/",
      produces = {"*/*"})
  default ResponseEntity<List<UserDto>> listUsers(
      @Parameter(name = "pageable", description = "") @Valid PageableDto pageable) {
    return getDelegate().listUsers(pageable);
  }

  /**
   * PATCH /api/v1/users/{id}
   *
   * @param id (required)
   * @param userUpdateRequestDto (required)
   * @return OK (status code 200)
   */
  @Operation(
      operationId = "updateUser",
      tags = {"User"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
              @Content(mediaType = "*/*", schema = @Schema(implementation = UserDto.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.PATCH,
      value = "/api/v1/users/{id}",
      produces = {"*/*"},
      consumes = {"application/json"})
  default ResponseEntity<UserDto> updateUser(
      @Parameter(name = "id", description = "", required = true) @PathVariable("id") Long id,
      @Parameter(name = "UserUpdateRequestDto", description = "", required = true)
          @Valid
          @RequestBody
          UserUpdateRequestDto userUpdateRequestDto) {
    return getDelegate().updateUser(id, userUpdateRequestDto);
  }
}
