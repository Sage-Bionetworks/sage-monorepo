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
@Table(name = "battle_round", schema = "api")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRoundEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "battle_id", nullable = false)
  private UUID battleId;

  @Column(name = "prompt_message_id")
  private UUID promptMessageId;

  @Column(name = "response_1_message_id")
  private UUID response1MessageId;

  @Column(name = "response_2_message_id")
  private UUID response2MessageId;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
