package org.sagebionetworks.challenge.controller;

import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(value = "/{identification}")
  public ResponseEntity<User> readUser(@PathVariable("identification") String identification) {
    return ResponseEntity.ok(userService.readUser(identification));
  }

  @GetMapping
  public ResponseEntity<List<User>> readUser(Pageable pageable) {
    return ResponseEntity.ok(userService.readUsers(pageable));
  }

}
