package org.sagebionetworks.challenge.api;

import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.dto.UserUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link UserApiController}}. Implement this interface with a {@link
 * org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface UserApiDelegate {

  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  /**
   * POST /api/v1/users/register
   *
   * @param user (required)
   * @return OK (status code 200)
   * @see UserApi#createUser
   */
  default ResponseEntity<User> createUser(User user) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                  String exampleString =
                      "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
                  ApiUtil.setExampleResponse(request, "*/*", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /api/v1/users/{id}
   *
   * @param id (required)
   * @return OK (status code 200)
   * @see UserApi#getUser
   */
  default ResponseEntity<User> getUser(Long id) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                  String exampleString =
                      "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
                  ApiUtil.setExampleResponse(request, "*/*", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /api/v1/users/
   *
   * @param pageable (optional)
   * @return OK (status code 200)
   * @see UserApi#listUsers
   */
  default ResponseEntity<List<User>> listUsers(Pageable pageable) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                  String exampleString =
                      "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
                  ApiUtil.setExampleResponse(request, "*/*", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * PATCH /api/v1/users/{id}
   *
   * @param id (required)
   * @param userUpdateRequest (required)
   * @return OK (status code 200)
   * @see UserApi#updateUser
   */
  default ResponseEntity<User> updateUser(Long id, UserUpdateRequest userUpdateRequest) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                  String exampleString =
                      "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
                  ApiUtil.setExampleResponse(request, "*/*", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
