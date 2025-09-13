package org.sagebionetworks.openchallenges.auth.service.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "api_key")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "key_hash", nullable = false, unique = true)
  private String keyHash;

  @Column(name = "key_prefix", nullable = false)
  private String keyPrefix;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "expires_at")
  private OffsetDateTime expiresAt;

  @Column(name = "last_used_at")
  private OffsetDateTime lastUsedAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  // OAuth2 service principal fields
  @Column(name = "client_id")
  private String clientId;

  @Column(name = "allowed_scopes")
  private String allowedScopes;

  // Transient field to temporarily store the plain key for API responses
  // This is NOT persisted to the database
  @Transient
  private String plainKey;

  // Helper method to check if API key is expired
  public boolean isExpired() {
    return expiresAt != null && OffsetDateTime.now().isAfter(expiresAt);
  }

  // Helper method to update last used timestamp
  public void updateLastUsed() {
    this.lastUsedAt = OffsetDateTime.now();
  }
}
