package org.sagebionetworks.openchallenges.auth.service.controller;

import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        @RequestParam(required = false) String accessToken,
        @RequestParam(required = false) String refreshToken,
        @RequestParam(required = false) String username,
        Authentication authentication,
        Model model
    ) {
        logger.debug("Displaying profile page for user: {}", username);

        User user = null;
        
        // Try to get user from authentication first (if user is logged in via JWT)
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
            logger.debug("Got user from authentication: {}", user.getUsername());
        }
        // Fallback to username parameter (from OAuth2 callback)
        else if (username != null) {
            try {
                user = userService.findByUsername(username).orElse(null);
                logger.debug("Got user from username parameter: {}", username);
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
}
