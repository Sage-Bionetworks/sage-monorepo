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

@Entity
@Table(name = "example_prompt_categorization", schema = "api")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamplePromptCategorizationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "prompt_id", nullable = false)
  private UUID promptId;

  @Column(name = "method", nullable = false, length = 100)
  private String method;

  @Column(name = "categorized_by")
  private UUID categorizedBy;

  @Column(name = "reason", length = 1000)
  private String reason;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;
}
