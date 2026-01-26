package org.sagebionetworks.openchallenges.auth.service.model.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "external_account",
    schema = "auth",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "provider"}),
        @UniqueConstraint(columnNames = {"provider", "external_id"})
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "accessTokenHash", "refreshTokenHash"})
public class ExternalAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Provider provider;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "external_username")
    private String externalUsername;

    @Column(name = "external_email")
    private String externalEmail;

    @Column(name = "access_token_hash")
    private String accessTokenHash;

    @Column(name = "refresh_token_hash")
    private String refreshTokenHash;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "provider_data", columnDefinition = "jsonb")
    private JsonNode providerData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public enum Provider {
        google,
        synapse
    }

    // Business logic methods

    /**
     * Checks if the external account's access token is expired.
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired() {
        return this.expiresAt != null && this.expiresAt.isBefore(OffsetDateTime.now());
    }

    /**
     * Checks if the external account has a refresh token.
     * @return true if refresh token exists, false otherwise
     */
    public boolean hasRefreshToken() {
        return this.refreshTokenHash != null && !this.refreshTokenHash.isEmpty();
    }

    /**
     * Gets the provider name as a string.
     * @return the provider name
     */
    public String getProviderName() {
        return this.provider != null ? this.provider.name() : null;
    }
}
