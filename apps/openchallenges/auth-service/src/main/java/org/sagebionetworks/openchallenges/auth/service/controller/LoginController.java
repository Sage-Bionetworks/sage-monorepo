package org.sagebionetworks.openchallenges.auth.service.controller;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.auth.service.configuration.AppProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple login page controller for OAuth2 Authorization Server.
 */
@Controller
@RequiredArgsConstructor
public class LoginController {

  private final AppProperties appProperties;

  /**
   * Display login page for OAuth2 Authorization Server.
   */
  @GetMapping("/login")
  public String login(Model model) {
    // Pass provider enabled flags to the template
    model.addAttribute("googleEnabled", appProperties.getOauth2().getGoogle().isEnabled());
    model.addAttribute("synapseEnabled", appProperties.getOauth2().getSynapse().isEnabled());
    
    // Return the existing OAuth2 login page
    return "oauth2-login";
  }

  /**
   * Handle logout request and redirect to login page.
   */
  @GetMapping("/logout")
  public String logout() {
    // Redirect to login page after logout
    return "redirect:/login";
  }
}
