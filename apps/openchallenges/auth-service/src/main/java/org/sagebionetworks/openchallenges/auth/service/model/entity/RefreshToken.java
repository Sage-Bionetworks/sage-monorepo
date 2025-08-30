package org.sagebionetworks.openchallenges.auth.service.model.entity;

import jakarta.persistence.*;
import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "tokenHash"})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean revoked = false;

    @Column(name = "issued_at", nullable = false)
    @Builder.Default
    private OffsetDateTime issuedAt = OffsetDateTime.now();

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "ip_address")
    private InetAddress ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Business logic methods

    /**
     * Checks if the refresh token is expired.
     * @return true if the token is expired, false otherwise
     */
    public boolean isExpired() {
        return this.expiresAt != null && this.expiresAt.isBefore(OffsetDateTime.now());
    }

    /**
     * Checks if the refresh token is valid (not expired and not revoked).
     * @return true if the token is valid, false otherwise
     */
    public boolean isValid() {
        return !isExpired() && !isRevoked();
    }

    /**
     * Checks if the refresh token is revoked.
     * @return true if the token is revoked, false otherwise
     */
    public boolean isRevoked() {
        return this.revoked != null && this.revoked;
    }

    /**
     * Marks the refresh token as used by updating the last used timestamp.
     */
    public void markAsUsed() {
        this.lastUsedAt = OffsetDateTime.now();
    }

    /**
     * Revokes the refresh token.
     */
    public void revoke() {
        this.revoked = true;
    }

    /**
     * Gets the number of days until the token expires.
     * @return days until expiration, or -1 if already expired
     */
    public long getDaysUntilExpiry() {
        if (this.expiresAt == null) return -1;
        OffsetDateTime now = OffsetDateTime.now();
        if (this.expiresAt.isBefore(now)) return -1;
        return java.time.Duration.between(now, this.expiresAt).toDays();
    }
}
