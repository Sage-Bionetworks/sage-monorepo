package org.sagebionetworks.challenge.api;

import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserApiDelegateImpl implements UserApiDelegate {

  @Autowired UserService userService;

  @Override
  public ResponseEntity<User> createUser(User user) {
    // getRequest()
    //     .ifPresent(
    //         request -> {
    //           for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept")))
    // {
    //             if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
    //               String exampleString =
    //                   "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\",
    // \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
    //               ApiUtil.setExampleResponse(request, "*/*", exampleString);
    //               break;
    //             }
    //           }
    //         });
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }
}
