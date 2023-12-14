package org.sagebionetworks.openchallenges.challenge.service.model.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtract;
import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtraction;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.sagebionetworks.openchallenges.challenge.service.model.search.CollectionSizeBridge;

@Entity
@Table(name = "challenge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Indexed(index = "openchallenges-challenge")
public class ChallengeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false)
  private String slug;

  @Column(nullable = false)
  @FullTextField()
  private String name;

  @Column(nullable = false)
  @FullTextField()
  private String headline;

  @Column(nullable = false)
  @FullTextField()
  private String description;

  @Column(name = "avatar_url", nullable = true)
  private String avatarUrl;

  @Column(name = "website_url", nullable = false)
  private String websiteUrl;

  @Column(nullable = true)
  @GenericField()
  private String doi;

  @Column(nullable = false)
  @GenericField()
  private String status;

  @Column(nullable = false)
  @GenericField()
  private String difficulty;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "platform_id", nullable = true)
  @IndexedEmbedded(includePaths = {"slug", "name"})
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private SimpleChallengePlatformEntity platform;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(
      name = "contributions",
      includePaths = {"role", "organization_id"})
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private List<ChallengeContributionEntity> contributions;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(
      name = "submission_types",
      includePaths = {"name"})
  private List<ChallengeSubmissionTypeEntity> submissionTypes;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "challenge_x_challenge_input_data_type",
      joinColumns = @JoinColumn(name = "challenge_id"),
      inverseJoinColumns = @JoinColumn(name = "challenge_input_data_type_id"))
  @IndexedEmbedded(
      name = "input_data_types",
      includePaths = {"slug", "name"})
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private List<SimpleChallengeInputDataTypeEntity> inputDataTypes;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(includePaths = {"name"})
  private List<ChallengeIncentiveEntity> incentives;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @LazyCollection(LazyCollectionOption.EXTRA)
  // @IndexedEmbedded(includePaths = {"userId"})
  @GenericField(
      name = "starred_count",
      valueBridge = @ValueBridgeRef(type = CollectionSizeBridge.class),
      extraction = @ContainerExtraction(extract = ContainerExtract.NO),
      sortable = Sortable.YES)
  private List<ChallengeStar> stars;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(
      name = "categories",
      includePaths = {"category"})
  private List<ChallengeCategoryEntity> categories;

  @Column(name = "start_date", columnDefinition = "DATE", nullable = true)
  @GenericField(name = "start_date", sortable = Sortable.YES)
  private LocalDate startDate;

  @Column(name = "end_date", columnDefinition = "DATE", nullable = true)
  @GenericField(name = "end_date", sortable = Sortable.YES)
  private LocalDate endDate;

  @Column(name = "created_at")
  @GenericField(name = "created_at", sortable = Sortable.YES)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
