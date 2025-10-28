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

  @Column(name = "outcome", nullable = false, length = 20)
  private String outcome;

  @Column(name = "valid", nullable = false)
  private Boolean valid;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  // Lombok generates getters/setters.
}
