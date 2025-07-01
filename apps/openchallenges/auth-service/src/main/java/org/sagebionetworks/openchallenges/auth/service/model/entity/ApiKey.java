package org.sagebionetworks.openchallenges.auth.service.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "api_keys")
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
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  // Default constructor
  public ApiKey() {}

  // Constructor for creating new API keys
  public ApiKey(
    User user,
    String keyHash,
    String keyPrefix,
    String name,
    OffsetDateTime expiresAt
  ) {
    this.user = user;
    this.keyHash = keyHash;
    this.keyPrefix = keyPrefix;
    this.name = name;
    this.expiresAt = expiresAt;
  }

  // Helper method to check if API key is expired
  public boolean isExpired() {
    return expiresAt != null && OffsetDateTime.now().isAfter(expiresAt);
  }

  // Helper method to update last used timestamp
  public void updateLastUsed() {
    this.lastUsedAt = OffsetDateTime.now();
  }

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getKeyHash() {
    return keyHash;
  }

  public void setKeyHash(String keyHash) {
    this.keyHash = keyHash;
  }

  public String getKeyPrefix() {
    return keyPrefix;
  }

  public void setKeyPrefix(String keyPrefix) {
    this.keyPrefix = keyPrefix;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public OffsetDateTime getLastUsedAt() {
    return lastUsedAt;
  }

  public void setLastUsedAt(OffsetDateTime lastUsedAt) {
    this.lastUsedAt = lastUsedAt;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
