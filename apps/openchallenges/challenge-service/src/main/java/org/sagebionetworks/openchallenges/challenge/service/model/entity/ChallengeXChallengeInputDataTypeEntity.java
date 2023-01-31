package org.sagebionetworks.openchallenges.challenge.service.model.entity;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge_x_challenge_input_data_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeXChallengeInputDataTypeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "challenge_id", nullable = false)
  private ChallengeEntity challenge;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "challenge_input_data_type_id", nullable = false)
  private SimpleChallengeInputDataTypeEntity challenge_input_data_type;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;
}
