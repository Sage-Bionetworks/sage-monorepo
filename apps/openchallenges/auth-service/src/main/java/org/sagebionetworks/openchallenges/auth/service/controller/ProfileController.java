package org.sagebionetworks.openchallenges.auth.service.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Profile controller for displaying user profile page.
 */
@Controller
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserService userService;

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

        // Create display name
        String displayName = user.getFirstName() != null && user.getLastName() != null 
            ? user.getFirstName() + " " + user.getLastName()
            : user.getUsername();
        model.addAttribute("displayName", displayName);

        logger.debug("Profile page prepared for user: {}", displayName);
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
}
