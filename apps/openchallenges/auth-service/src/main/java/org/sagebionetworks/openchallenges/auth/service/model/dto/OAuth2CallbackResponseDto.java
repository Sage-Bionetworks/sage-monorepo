package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for OAuth2 callback containing OpenChallenges JWT tokens.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2CallbackResponseDto {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
    private UUID userId;
    private String username;
    private RoleEnum role;
    
    public enum RoleEnum {
        USER("user"),
        ADMIN("admin"),
        ORG_ADMIN("org-admin");
        
        private String value;
        
        RoleEnum(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static RoleEnum fromValue(String value) {
            for (RoleEnum e : RoleEnum.values()) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
            throw new IllegalArgumentException("Unknown role: " + value);
        }
    }
}
