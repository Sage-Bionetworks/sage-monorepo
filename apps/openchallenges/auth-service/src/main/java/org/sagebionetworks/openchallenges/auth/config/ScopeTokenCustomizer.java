package org.sagebionetworks.openchallenges.auth.config;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
public class ScopeTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        if (context.getPrincipal() != null) {
            // Map user roles to OAuth2 scopes
            Set<String> scopes = mapRolesToScopes(getUserRoles(context));
            
            // Add scopes to the "scp" claim
            context.getClaims().claim("scp", scopes);
            
            // Add user context for resource servers
            context.getClaims().claim("sub", getUserId(context));
            context.getClaims().claim("login", getUserLogin(context));
            context.getClaims().claim("email", getUserEmail(context));
        }
    }

    private Set<String> mapRolesToScopes(Set<String> roles) {
        return roles.stream()
            .flatMap(role -> getScopesForRole(role).stream())
            .collect(Collectors.toSet());
    }

    private Set<String> getScopesForRole(String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> Set.of(
                "read:org", "write:org", "delete:org", "admin:org",
                "read:challenge", "write:challenge", "delete:challenge", "admin:challenge",
                "admin:system"
            );
            case "moderator" -> Set.of(
                "read:org", "write:org",
                "read:challenge", "write:challenge",
                "moderate:challenge"
            );
            case "user" -> Set.of(
                "read:org", "read:challenge"
            );
            case "service" -> Set.of(
                "read:org", "write:org", "delete:org",
                "read:challenge", "write:challenge", "delete:challenge"
            );
            default -> Set.of("read:org", "read:challenge");
        };
    }

    private Set<String> getUserRoles(JwtEncodingContext context) {
        // Extract roles from authenticated user
        return context.getPrincipal().getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .filter(auth -> auth.startsWith("ROLE_"))
            .map(auth -> auth.substring(5)) // Remove "ROLE_" prefix
            .collect(Collectors.toSet());
    }

    private String getUserId(JwtEncodingContext context) {
        // Extract user ID from principal
        return context.getPrincipal().getName();
    }

    private String getUserLogin(JwtEncodingContext context) {
        // Extract login from user details if available
        // For now, use the principal name as login
        return context.getPrincipal().getName();
    }

    private String getUserEmail(JwtEncodingContext context) {
        // Extract email from user details if available
        // This would need to be implemented based on your UserDetailsService
        return ""; // Placeholder - implement based on your user model
    }
}
