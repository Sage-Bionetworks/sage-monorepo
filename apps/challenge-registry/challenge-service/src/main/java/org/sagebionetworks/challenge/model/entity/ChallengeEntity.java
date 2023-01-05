package org.sagebionetworks.challenge.model.entity;

import java.time.OffsetDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

@Entity
@Table(name = "challenge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Indexed(index = "challenge-registry-challenge")
public class ChallengeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @FullTextField()
  @NaturalId()
  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String headline;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private String difficulty;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "platform_id", nullable = false)
  private SimpleChallengePlatformEntity platform;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  private List<ChallengeSubmissionTypeEntity> submissionTypes;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  private List<ChallengeIncentiveEntity> incentives;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
