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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

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

  @Column(nullable = false)
  @FullTextField()
  private String name;

  @Column(nullable = false)
  @FullTextField()
  private String headline;

  @Column(nullable = false)
  @FullTextField()
  private String description;

  @Column(nullable = false)
  @GenericField()
  private String status;

  @Column(nullable = false)
  @GenericField()
  private String difficulty;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "platform_id", nullable = false)
  @IndexedEmbedded(includePaths = {"name"})
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private SimpleChallengePlatformEntity platform;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(includePaths = {"name"})
  private List<ChallengeSubmissionTypeEntity> submissionTypes;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(includePaths = {"name"})
  private List<ChallengeIncentiveEntity> incentives;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @LazyCollection(LazyCollectionOption.EXTRA)
  private List<ChallengeStar> stars;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
