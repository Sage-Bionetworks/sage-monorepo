package org.sagebionetworks.openchallenges.auth.service.controller;

import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2TokenResponse;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.sagebionetworks.openchallenges.auth.service.service.OAuth2ConfigurationService;
import org.sagebionetworks.openchallenges.auth.service.service.OAuth2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * OAuth2 web controller for handling OAuth2 login flows.
 */
@Controller
public class OAuth2WebController {

  private static final Logger logger = LoggerFactory.getLogger(OAuth2WebController.class);

  @Autowired
  private OAuth2ConfigurationService oAuth2ConfigurationService;

  @Autowired
  private OAuth2Service oAuth2Service;

  /**
   * Initiate Google OAuth2 login flow.
   */
  @GetMapping("/v1/auth/oauth2/google")
  public String loginWithGoogle() {
    logger.debug("Initiating Google OAuth2 authorization");

    // Generate authorization URL for Google
    String authUrl = oAuth2ConfigurationService.getAuthorizationUrl(
      ExternalAccount.Provider.google,
      "state_" + System.currentTimeMillis() // Simple state parameter for security
    );

    logger.debug("Redirecting to Google authorization URL");
    return "redirect:" + authUrl;
  }

  /**
   * Handle OAuth2 callback.
   */
  @GetMapping("/v1/auth/callback")
  public String oauth2Callback(
    @RequestParam(required = false) String code,
    @RequestParam(required = false) String error,
    @RequestParam(required = false) String state,
    Model model
  ) {
    logger.debug(
      "OAuth2 callback received - code: {}, error: {}, state: {}",
      code != null ? "present" : "null",
      error,
      state
    );

    if (error != null) {
      logger.error("OAuth2 authorization error: {}", error);
      model.addAttribute("error", error);
      return "oauth2-error";
    }

    if (code == null) {
      logger.error("No authorization code received in callback");
      model.addAttribute("error", "No authorization code received");
      return "oauth2-error";
    }

    try {
      // Exchange authorization code for tokens
      logger.debug("Exchanging authorization code for tokens");
      String redirectUri = oAuth2ConfigurationService.getRedirectUri(
        ExternalAccount.Provider.google
      );

      OAuth2TokenResponse tokenResponse = oAuth2Service.exchangeCodeForToken(
        ExternalAccount.Provider.google,
        code,
        redirectUri
      );

      // Add token information to model for display
      model.addAttribute("accessToken", tokenResponse.getAccessToken());
      model.addAttribute("refreshToken", tokenResponse.getRefreshToken());
      model.addAttribute("tokenType", tokenResponse.getTokenType());
      model.addAttribute("expiresIn", tokenResponse.getExpiresIn());

      logger.debug(
        "Adding tokens to model - accessToken: {}, refreshToken: {}, tokenType: {}, expiresIn: {}",
        tokenResponse.getAccessToken() != null ? "present" : "null",
        tokenResponse.getRefreshToken() != null ? "present" : "null",
        tokenResponse.getTokenType(),
        tokenResponse.getExpiresIn()
      );

      logger.debug("OAuth2 token exchange successful");
      return "oauth2-success";
    } catch (Exception e) {
      logger.error("Error exchanging authorization code for tokens", e);
      model.addAttribute("error", "Failed to exchange authorization code: " + e.getMessage());
      return "oauth2-error";
    }
  }
}
