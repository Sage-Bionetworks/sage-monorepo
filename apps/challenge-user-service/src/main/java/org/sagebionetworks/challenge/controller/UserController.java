package org.sagebionetworks.challenge.controller;

import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.dto.UserUpdateRequest;
import org.sagebionetworks.challenge.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
// import javax.annotation.security.RolesAllowed;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping(value = "/register")
  public ResponseEntity<User> createUser(@RequestBody User request) {
    log.info("Create the user with {}", request.toString());
    return ResponseEntity.ok(userService.createUser(request));
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<User> updateUser(@PathVariable("id") Long userId,
      @RequestBody UserUpdateRequest userUpdateRequest) {
    log.info("Updates the user with {}", userUpdateRequest.toString());
    return ResponseEntity.ok(userService.updateUser(userId, userUpdateRequest));
  }

  // @RolesAllowed("user")
  @GetMapping
  public ResponseEntity<List<User>> listUsers(Pageable pageable) {
    log.info("Lists all the users");
    return ResponseEntity.ok(userService.listUsers(pageable));
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
    log.info("Gets the user by id {}", id);
    return ResponseEntity.ok(userService.getUser(id));
  }

}
