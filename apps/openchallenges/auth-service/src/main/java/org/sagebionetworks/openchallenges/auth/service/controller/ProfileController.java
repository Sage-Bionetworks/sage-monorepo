package org.sagebionetworks.openchallenges.auth.service.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Profile controller for displaying user profile page.
 */
@Controller
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ApiKeyService apiKeyService;

    /**
     * Display user profile page.
     */
    @GetMapping("/profile")
    public String profile(
        HttpServletRequest request,
        Authentication authentication,
        Model model
    ) {
        logger.debug("Displaying profile page");

        // Extract tokens and username from secure cookies
        String accessToken = getCookieValue(request, "oc_access_token");
        String refreshToken = getCookieValue(request, "oc_refresh_token");
        String username = getCookieValue(request, "oc_username");
        
        logger.debug("Retrieved from cookies - username: {}, accessToken: {}, refreshToken: {}", 
                     username, accessToken != null ? "present" : "null", refreshToken != null ? "present" : "null");

        User user = null;
        
        // Try to get user from authentication first (if user is logged in via JWT)
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
            logger.debug("Got user from authentication: {}", user.getUsername());
        }
        // Fallback to username from cookie (from OAuth2 callback)
        else if (username != null) {
            try {
                user = userService.findByUsername(username).orElse(null);
                logger.debug("Got user from username cookie: {}", username);
            } catch (Exception e) {
                logger.error("Error finding user by username: {}", username, e);
            }
        }

        if (user == null) {
            logger.warn("No user found for profile page, redirecting to login");
            return "redirect:/login";
        }

        // Add user information to model
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("role", user.getRole());
        model.addAttribute("accessToken", accessToken);
        model.addAttribute("refreshToken", refreshToken);

        // Get user's API keys
        List<ApiKey> apiKeys = apiKeyService.getUserApiKeys(user);
        model.addAttribute("apiKeys", apiKeys);

        // Create display name
        String displayName = user.getFirstName() != null && user.getLastName() != null 
            ? user.getFirstName() + " " + user.getLastName()
            : user.getUsername();
        model.addAttribute("displayName", displayName);

        logger.debug("Profile page prepared for user: {} with {} API keys", displayName, apiKeys.size());
        return "profile";
    }

    /**
     * Get cookie value by name from the request.
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Handle logout by clearing secure cookies.
     */
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        logger.debug("Logout requested, clearing secure cookies");
        
        // Clear all authentication cookies
        clearCookie(response, "oc_access_token");
        clearCookie(response, "oc_refresh_token");
        clearCookie(response, "oc_username");
        
        logger.debug("Secure cookies cleared, redirecting to login");
        return "redirect:/login";
    }

    /**
     * Clear a cookie by setting its max age to 0.
     */
    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete the cookie
        response.addCookie(cookie);
        
        logger.debug("Cleared cookie: {}", cookieName);
    }

    /**
     * Create a new API key for the user.
     */
    @PostMapping("/profile/api-keys/create")
    public String createApiKey(
        @RequestParam String name,
        @RequestParam(required = false) Integer expiresInDays,
        HttpServletRequest request,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        logger.debug("Creating API key with name: {}", name);
        
        User user = getAuthenticatedUser(request, authentication);
        if (user == null) {
            logger.warn("No authenticated user found for API key creation");
            redirectAttributes.addFlashAttribute("error", "Authentication required");
            return "redirect:/profile";
        }

        try {
            ApiKey newApiKey = apiKeyService.createApiKey(user, name, expiresInDays);
            logger.info("Successfully created API key '{}' for user: {}", name, user.getUsername());
            
            // Add the newly created key with the plain key to flash attributes
            // This is the only time the user will see the full API key
            redirectAttributes.addFlashAttribute("newApiKey", newApiKey);
            redirectAttributes.addFlashAttribute("success", "API key created successfully!");
            
        } catch (Exception e) {
            logger.error("Error creating API key '{}' for user: {}", name, user.getUsername(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to create API key: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    /**
     * Delete an API key.
     */
    @PostMapping("/profile/api-keys/delete")
    public String deleteApiKey(
        @RequestParam UUID keyId,
        HttpServletRequest request,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        logger.debug("Deleting API key with ID: {}", keyId);
        
        User user = getAuthenticatedUser(request, authentication);
        if (user == null) {
            logger.warn("No authenticated user found for API key deletion");
            redirectAttributes.addFlashAttribute("error", "Authentication required");
            return "redirect:/profile";
        }

        try {
            boolean deleted = apiKeyService.deleteApiKey(keyId, user);
            
            if (deleted) {
                logger.info("Successfully deleted API key {} for user: {}", keyId, user.getUsername());
                redirectAttributes.addFlashAttribute("success", "API key deleted successfully");
            } else {
                logger.warn("API key {} not found or not owned by user: {}", keyId, user.getUsername());
                redirectAttributes.addFlashAttribute("error", "API key not found or access denied");
            }
            
        } catch (Exception e) {
            logger.error("Error deleting API key {} for user: {}", keyId, user.getUsername(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete API key: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    /**
     * Helper method to get authenticated user from either Authentication or cookies.
     */
    private User getAuthenticatedUser(HttpServletRequest request, Authentication authentication) {
        // Try to get user from authentication first (if user is logged in via JWT)
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        
        // Fallback to username from cookie (from OAuth2 callback)
        String username = getCookieValue(request, "oc_username");
        if (username != null) {
            try {
                return userService.findByUsername(username).orElse(null);
            } catch (Exception e) {
                logger.error("Error finding user by username: {}", username, e);
            }
        }
        
        return null;
    }
}
