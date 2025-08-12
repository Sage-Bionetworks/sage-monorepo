package org.sagebionetworks.openchallenges.auth.service.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "app_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "apiKeys") // Exclude lazy-loaded collection from toString
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private Role role = Role.user;

  @Column(nullable = false)
  @Builder.Default
  private Boolean enabled = true;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<ApiKey> apiKeys = new ArrayList<>();

  public enum Role {
    admin,
    user,
    readonly,
    service,
  }

  // Business logic methods to ensure Jacoco coverage instrumentation

  /**
   * Checks if the user has admin privileges.
   * @return true if the user has admin role, false otherwise
   */
  public boolean isAdmin() {
    return this.role == Role.admin;
  }

  /**
   * Checks if the user account is active (enabled and not null).
   * @return true if the user is active, false otherwise
   */
  public boolean isActive() {
    return this.enabled != null && this.enabled;
  }

  /**
   * Gets the number of API keys associated with this user.
   * @return the count of API keys
   */
  public int getApiKeyCount() {
    return this.apiKeys != null ? this.apiKeys.size() : 0;
  }

  /**
   * Checks if the user has any API keys.
   * @return true if the user has at least one API key, false otherwise
   */
  public boolean hasApiKeys() {
    return this.apiKeys != null && !this.apiKeys.isEmpty();
  }
}
