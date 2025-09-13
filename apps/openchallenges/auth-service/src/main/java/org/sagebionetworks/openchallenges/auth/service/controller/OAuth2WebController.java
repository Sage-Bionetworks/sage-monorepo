package org.sagebionetworks.openchallenges.auth.service.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2CallbackResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.sagebionetworks.openchallenges.auth.service.service.AuthenticationService;
import org.sagebionetworks.openchallenges.auth.service.service.OAuth2ConfigurationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * OAuth2 web controller for handling OAuth2 login flows.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class OAuth2WebController {

  private final OAuth2ConfigurationService oAuth2ConfigurationService;
  private final AuthenticationService authenticationService;

  /**
   * Initiate Google OAuth2 login flow.
   */
  @GetMapping("/auth/oauth2/google")
  public String loginWithGoogle() {
    log.debug("Initiating Google OAuth2 authorization");

    // Generate authorization URL for Google
    String authUrl = oAuth2ConfigurationService.getAuthorizationUrl(
      ExternalAccount.Provider.google,
      "google_state_" + System.currentTimeMillis() // Provider-specific state for identification
    );

    log.debug("Redirecting to Google authorization URL");
    return "redirect:" + authUrl;
  }

  /**
   * Initiate Synapse OAuth2 login flow.
   */
  @GetMapping("/auth/oauth2/synapse")
  public String loginWithSynapse() {
    log.debug("Initiating Synapse OAuth2 authorization");

    // Generate authorization URL for Synapse
    String authUrl = oAuth2ConfigurationService.getAuthorizationUrl(
      ExternalAccount.Provider.synapse,
      "synapse_state_" + System.currentTimeMillis() // Provider-specific state for identification
    );

    log.debug("Redirecting to Synapse authorization URL");
    return "redirect:" + authUrl;
  }

  /**
   * Handle OAuth2 callback.
   */
  @GetMapping("/auth/callback")
  public String oauth2Callback(
    @RequestParam(required = false) String code,
    @RequestParam(required = false) String error,
    @RequestParam(required = false) String state,
    Model model,
    HttpServletResponse response
  ) {
    log.debug(
      "OAuth2 callback received - code: {}, error: {}, state: {}",
      code != null ? "present" : "null",
      error,
      state
    );

    if (error != null) {
      log.error("OAuth2 authorization error: {}", error);
      model.addAttribute("error", error);
      return "oauth2-error";
    }

    if (code == null) {
      log.error("No authorization code received in callback");
      model.addAttribute("error", "No authorization code received");
      return "oauth2-error";
    }

    try {
      // Process OAuth2 callback through AuthenticationService
      log.debug("Processing OAuth2 callback through AuthenticationService");

      // Extract provider from state parameter
      String provider = extractProviderFromState(state);
      if (provider == null) {
        log.error("Unable to determine OAuth2 provider from state: {}", state);
        model.addAttribute("error", "Invalid state parameter - unable to determine provider");
        return "oauth2-error";
      }

      log.debug("Detected OAuth2 provider: {}", provider);

      OAuth2CallbackResponseDto authResponse = authenticationService.handleOAuth2Callback(
        provider,
        code,
        state
      );

      log.debug("OAuth2 authentication successful, setting secure cookies");

      // Set secure HTTP-only cookies with appropriate expiration times
      setSecureCookie(response, "oc_access_token", authResponse.getAccessToken(), 3600); // 1 hour
      setSecureCookie(response, "oc_refresh_token", authResponse.getRefreshToken(), 604800); // 7 days
      setSecureCookie(response, "oc_username", authResponse.getUsername(), 604800); // 7 days - longer session

      log.debug("Secure cookies set, redirecting to profile page");

      // Redirect to clean profile URL without sensitive data
      return "redirect:/profile";
    } catch (Exception e) {
      log.error("Error exchanging authorization code for tokens", e);
      model.addAttribute("error", "Failed to exchange authorization code: " + e.getMessage());
      return "oauth2-error";
    }
  }

  /**
   * Set a secure HTTP-only cookie.
   */
  private void setSecureCookie(
    HttpServletResponse response,
    String name,
    String value,
    int maxAge
  ) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true); // Prevent XSS attacks
    cookie.setSecure(false); // Set to true in production with HTTPS
    cookie.setPath("/"); // Available for entire application
    cookie.setMaxAge(maxAge); // Cookie expiration in seconds
    response.addCookie(cookie);

    log.debug("Set secure cookie: {} with maxAge: {} seconds", name, maxAge);
  }

  /**
   * Extract OAuth2 provider from state parameter.
   * State format: "{provider}_state_{timestamp}"
   */
  private String extractProviderFromState(String state) {
    if (state == null || state.isEmpty()) {
      return null;
    }

    // Check for provider prefixes
    if (state.startsWith("google_state_")) {
      return "google";
    } else if (state.startsWith("synapse_state_")) {
      return "synapse";
    }

    // Fallback: assume it's an old-style state (defaults to Google for backwards compatibility)
    log.warn(
      "Unable to determine provider from state parameter: {}. Defaulting to Google for backwards compatibility.",
      state
    );
    return "google";
  }
}
