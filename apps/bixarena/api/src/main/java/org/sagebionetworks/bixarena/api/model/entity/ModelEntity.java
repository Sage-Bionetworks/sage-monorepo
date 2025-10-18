package org.sagebionetworks.bixarena.api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "model", schema = "api")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "slug", unique = true, nullable = false, length = 200)
  private String slug;

  @Column(name = "name", nullable = false, length = 300)
  private String name;

  @Column(name = "license", nullable = false, length = 20)
  private String license;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "alias", length = 200)
  private String alias;

  @Column(name = "external_link", nullable = false, length = 300)
  private String externalLink;

  @Column(name = "organization", length = 200)
  private String organization;

  @Column(name = "description", length = 300)
  private String description;

  @Column(name = "api_model_name", nullable = false, length = 300)
  private String apiModelName;

  @Column(name = "api_base", nullable = false, length = 300)
  private String apiBase;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
