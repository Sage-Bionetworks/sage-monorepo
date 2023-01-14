package org.sagebionetworks.challenge.controller;

import javax.servlet.http.HttpServletRequest;
import org.sagebionetworks.challenge.model.dto.LoginRequest;
import org.sagebionetworks.challenge.model.dto.LoginResponse;
import org.sagebionetworks.challenge.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/auth")
public class LoginController {

  @Autowired
  LoginService loginService;

  @PostMapping(value = "/login")
  public ResponseEntity<LoginResponse> login(HttpServletRequest request,
      @RequestBody LoginRequest loginRequest) throws Exception {
    log.info("Executing login");
    log.info("Username: {}", loginRequest.getUsername());

    ResponseEntity<LoginResponse> response = null;
    response = loginService.login(loginRequest);

    return response;
  }
}
