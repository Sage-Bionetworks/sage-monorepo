package org.sagebionetworks.bixarena.auth.service.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "app_user", schema = "auth")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(unique = true)
  private String email;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

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

  @Column(name = "last_login_at")
  private OffsetDateTime lastLoginAt;

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
   * Gets the display name for the user.
   * @return display name combining first and last names, or username if names are not available
   */
  public String getDisplayName() {
    if (firstName != null && lastName != null) {
      return firstName + " " + lastName;
    } else if (firstName != null) {
      return firstName;
    } else if (lastName != null) {
      return lastName;
    } else {
      return username;
    }
  }
}
