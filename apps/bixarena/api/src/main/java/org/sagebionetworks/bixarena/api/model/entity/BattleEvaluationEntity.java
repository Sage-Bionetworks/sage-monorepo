package org.sagebionetworks.bixarena.api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "battle_evaluation", schema = "api")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleEvaluationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "battle_id", nullable = false)
  private UUID battleId;

  @Enumerated(EnumType.STRING)
  @Column(name = "outcome", nullable = false, length = 20)
  private Outcome outcome;

  @Column(name = "is_valid", nullable = false)
  private Boolean isValid;

  @Column(name = "validation_error", length = 1000)
  private String validationError;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public enum Outcome {
    MODEL_1,
    MODEL_2,
    TIE,
  }
}
