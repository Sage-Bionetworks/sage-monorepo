package org.sagebionetworks.openchallenges.auth.service.web;

import org.sagebionetworks.openchallenges.auth.service.api.AuthenticationApiDelegate;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LogoutRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LogoutResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2AuthorizeRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2AuthorizeResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2CallbackRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Web controller for OAuth2 authentication flow.
 * Provides simple HTML pages for testing OAuth2 without requiring a frontend application.
 */
@Controller
public class OAuth2WebController {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2WebController.class);
    
    private final AuthenticationApiDelegate authenticationDelegate;

    public OAuth2WebController(AuthenticationApiDelegate authenticationDelegate) {
        this.authenticationDelegate = authenticationDelegate;
    }

    /**
     * Display login page with OAuth2 providers
     */
    @GetMapping("/login")
    public String loginPage() {
        logger.debug("Displaying OAuth2 login page");
        return "oauth2-login";
    }

    /**
     * Initiate OAuth2 flow with Google
     */
    @GetMapping("/auth/oauth2/google")
    public RedirectView initiateGoogleAuth(@RequestParam(required = false) String redirectUri) {
        logger.debug("Initiating Google OAuth2 flow");
        
        try {
            // Use default redirect URI if not provided
            if (redirectUri == null || redirectUri.trim().isEmpty()) {
                redirectUri = "http://localhost:8087/auth/callback";
            }
            
            OAuth2AuthorizeRequestDto request = new OAuth2AuthorizeRequestDto()
                    .provider(OAuth2AuthorizeRequestDto.ProviderEnum.GOOGLE)
                    .redirectUri(URI.create(redirectUri))
                    .state("web_flow_" + System.currentTimeMillis());
            
            OAuth2AuthorizeResponseDto response = authenticationDelegate.initiateOAuth2(request).getBody();
            
            if (response != null && response.getAuthorizationUrl() != null) {
                logger.debug("Redirecting to Google OAuth2: {}", response.getAuthorizationUrl());
                return new RedirectView(response.getAuthorizationUrl().toString());
            } else {
                logger.error("Failed to generate OAuth2 authorization URL");
                return new RedirectView("/login?error=oauth2_initiation_failed");
            }
            
        } catch (Exception e) {
            logger.error("Error initiating Google OAuth2 flow", e);
            return new RedirectView("/login?error=oauth2_error");
        }
    }

    /**
     * Handle OAuth2 callback from Google
     */
    @GetMapping("/auth/callback")
    public String handleOAuth2Callback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            Model model) {
        
        logger.debug("Handling OAuth2 callback - code: {}, state: {}, error: {}", 
                code != null ? "present" : "null", state, error);
        
        if (error != null) {
            logger.warn("OAuth2 callback received error: {}", error);
            model.addAttribute("error", "OAuth2 authentication failed: " + error);
            return "oauth2-error";
        }
        
        if (code == null || code.trim().isEmpty()) {
            logger.warn("OAuth2 callback missing authorization code");
            model.addAttribute("error", "Missing authorization code");
            return "oauth2-error";
        }
        
        try {
            OAuth2CallbackRequestDto request = new OAuth2CallbackRequestDto()
                    .code(code)
                    .state(state);
            
            LoginResponseDto response = authenticationDelegate.completeOAuth2(request).getBody();
            
            if (response != null && response.getAccessToken() != null) {
                logger.debug("OAuth2 authentication successful for user");
                
                // Add tokens to model for display
                model.addAttribute("accessToken", response.getAccessToken());
                model.addAttribute("refreshToken", response.getRefreshToken());
                model.addAttribute("tokenType", response.getTokenType());
                model.addAttribute("expiresIn", response.getExpiresIn());
                
                return "oauth2-success";
            } else {
                logger.error("OAuth2 callback completed but no tokens received");
                model.addAttribute("error", "Authentication completed but no tokens received");
                return "oauth2-error";
            }
            
        } catch (Exception e) {
            logger.error("Error processing OAuth2 callback", e);
            model.addAttribute("error", "Error processing authentication: " + e.getMessage());
            return "oauth2-error";
        }
    }

    /**
     * Display logout page
     */
    @GetMapping("/logout")
    public String logoutPage() {
        logger.debug("Displaying logout page");
        return "logout";
    }

    /**
     * Handle logout form submission
     */
    @PostMapping("/logout/token")
    public String handleLogout(
            @RequestParam String refreshToken,
            @RequestParam(required = false) Boolean revokeAllTokens,
            Model model) {
        
        logger.debug("Processing logout request with token - revokeAllTokens: {}", revokeAllTokens);
        
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            logger.warn("Logout request missing refresh token");
            return "redirect:/logout?error=Missing refresh token";
        }
        
        try {
            LogoutRequestDto request = new LogoutRequestDto()
                    .refreshToken(refreshToken.trim())
                    .revokeAllTokens(revokeAllTokens != null ? revokeAllTokens : false);
            
            LogoutResponseDto response = authenticationDelegate.logout(request).getBody();
            
            if (response != null) {
                logger.debug("Logout successful - {} tokens revoked", response.getRevokedTokens());
                
                // Add logout details to model for display
                model.addAttribute("revokedTokens", response.getRevokedTokens());
                model.addAttribute("revokeAllTokens", revokeAllTokens != null ? revokeAllTokens : false);
                model.addAttribute("timestamp", OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
                return "logout-success";
            } else {
                logger.error("Logout completed but no response received");
                return "redirect:/logout?error=Logout failed - no response received";
            }
            
        } catch (Exception e) {
            logger.error("Error processing logout", e);
            return "redirect:/logout?error=Error processing logout: " + e.getMessage();
        }
    }

    /**
     * Handle logout - clears session and shows logout success
     */
    @PostMapping("/logout")
    public String handleLogout(Model model) {
        logger.debug("Processing logout request");
        
        try {
            // For logout, we just clear the session and redirect to success
            // In a real application, you might want to:
            // - Clear any session cookies
            // - Invalidate any stored sessions
            // - Log the logout event
            
            logger.info("User performed logout");
            
            // Add logout details to model for display
            model.addAttribute("simpleLogout", true);
            model.addAttribute("timestamp", OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            return "logout-success";
            
        } catch (Exception e) {
            logger.error("Error processing logout", e);
            return "redirect:/logout?error=Error processing logout: " + e.getMessage();
        }
    }
}
