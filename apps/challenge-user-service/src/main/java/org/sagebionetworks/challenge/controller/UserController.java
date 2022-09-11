package org.sagebionetworks.challenge.controller;

import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.dto.UserUpdateRequest;
import org.sagebionetworks.challenge.service.UserService;
import org.springdoc.api.annotations.ParameterObject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
// import javax.annotation.security.RolesAllowed;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {

  @Autowired
  UserService userService;

  @PostMapping(value = "/register")
  public ResponseEntity<User> createUser(@RequestBody User request) {
    log.info("Create the user with {}", request.toString());
    return ResponseEntity.ok(userService.createUser(request));
    // return ResponseEntity.ok(null);
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<User> updateUser(@PathVariable("id") Long userId,
      @RequestBody UserUpdateRequest userUpdateRequest) {
    log.info("Update the user with {}", userUpdateRequest.toString());
    return ResponseEntity.ok(userService.updateUser(userId, userUpdateRequest));
    // return ResponseEntity.ok(null);
  }

  // @RolesAllowed("user")
  @GetMapping(value = "/")
  public ResponseEntity<List<User>> listUsers(@ParameterObject Pageable pageable) {
    log.info("List all the users");
    return ResponseEntity.ok(userService.listUsers(pageable));
    // return ResponseEntity.ok(null);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
    log.info("Get the user by id {}", id);
    return ResponseEntity.ok(userService.getUser(id));
    // return ResponseEntity.ok(null);
  }

}
