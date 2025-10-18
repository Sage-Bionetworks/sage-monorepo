package org.sagebionetworks.bixarena.auth.service.model.entity;

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
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
  name = "external_account",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = { "user_id", "provider" }),
    @UniqueConstraint(columnNames = { "provider", "external_id" }),
  }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "user" })
public class ExternalAccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private Provider provider;

  @Column(name = "external_id", nullable = false, length = 255)
  private String externalId;

  @Column(name = "external_username", length = 255)
  private String externalUsername;

  @Column(name = "external_email", length = 255)
  private String externalEmail;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  public enum Provider {
    synapse,
  }

  // Business logic methods

  /**
   * Gets the provider name as a string.
   * @return the provider name
   */
  public String getProviderName() {
    return this.provider != null ? this.provider.name() : null;
  }
}
