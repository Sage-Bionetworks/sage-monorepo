package org.sagebionetworks.openchallenges.auth.service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple login page controller for OAuth2 Authorization Server.
 */
@Controller
public class LoginController {

  /**
   * Display login page for OAuth2 Authorization Server.
   */
  @GetMapping("/login")
  public String login() {
    // Return the existing OAuth2 login page
    return "oauth2-login";
  }
}
