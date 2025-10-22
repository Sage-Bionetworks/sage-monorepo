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
@Table(name = "api.battle")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "title", length = 255)
  private String title;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "left_model_id", nullable = false)
  private UUID leftModelId;

  @Column(name = "right_model_id", nullable = false)
  private UUID rightModelId;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "ended_at")
  private OffsetDateTime endedAt;
}
