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
@Table(name = "model_error", schema = "api")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelErrorEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "model_id", nullable = false)
  private UUID modelId;

  @Column(name = "code")
  private Integer code;

  @Column(name = "message", nullable = false, length = 1000)
  private String message;

  @Column(name = "battle_id")
  private UUID battleId;

  @Column(name = "round_id")
  private UUID roundId;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;
}
