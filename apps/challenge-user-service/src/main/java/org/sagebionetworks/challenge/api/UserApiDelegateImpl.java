package org.sagebionetworks.challenge.api;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.challenge.model.dto.PageableDto;
import org.sagebionetworks.challenge.model.dto.UserDto;
import org.sagebionetworks.challenge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserApiDelegateImpl implements UserApiDelegate {

  @Autowired UserService userService;

  @Override
  public ResponseEntity<UserDto> createUser(UserDto user) {
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

  @Override
  public ResponseEntity<List<UserDto>> listUsers(PageableDto pageable) {
    log.info("List all the users");
    log.info(pageable.toString());
    List<UserDto> result =
        userService.listUsers(PageRequest.of(pageable.getPage(), pageable.getSize()));
    // return new ResponseEntity<>(result, HttpStatus.OK);
    return ResponseEntity.ok(result);
    // return new ResponseEntity<>(HttpStatus.ACCEPTED);
    // return ResponseEntity.ok(userService.listUsers(pageable));
  }

  //   List<AccountJson> result = accountService.findAllAccounts(PageRequest.of(page, limit))
  //   .getContent()
  //   .stream()
  //   .map(accountMapper::toDto)
  //   .collect(Collectors.toList());
  // return new ResponseEntity<>(result, HttpStatus.OK);
}
