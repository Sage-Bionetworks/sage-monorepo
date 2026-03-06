package org.sagebionetworks.bixarena.api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "battle_validation", schema = "api")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleValidationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "battle_id", nullable = false)
  private UUID battleId;

  @Column(name = "method", nullable = false, length = 100)
  private String method;

  @Column(name = "confidence", nullable = false, precision = 4, scale = 3)
  private BigDecimal confidence;

  @Column(name = "is_biomedical", nullable = false)
  private Boolean isBiomedical;

  @Column(name = "validated_by")
  private UUID validatedBy;

  @Column(name = "reason", length = 1000)
  private String reason;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;
}
