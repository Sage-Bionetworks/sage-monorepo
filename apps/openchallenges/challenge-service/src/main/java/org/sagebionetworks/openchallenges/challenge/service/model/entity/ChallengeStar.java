package org.sagebionetworks.openchallenges.challenge.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

@Entity
@Table(name = "challenge_star", schema = "challenge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeStar {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "challenge_id", nullable = false)
  private ChallengeEntity challenge;

  @Column(name = "user_id", nullable = false)
  @GenericField
  private Long userId;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private OffsetDateTime createdAt;
}
