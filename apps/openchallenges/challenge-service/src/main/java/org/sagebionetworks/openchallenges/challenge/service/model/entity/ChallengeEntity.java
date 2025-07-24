package org.sagebionetworks.openchallenges.challenge.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.search.engine.backend.types.Searchable;
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

  @Column(nullable = false, length = 255)
  private String slug;

  @Column(nullable = false, length = 255)
  @FullTextField
  private String name;

  @Column(nullable = true, length = 80)
  @FullTextField
  private String headline;

  @Column(nullable = true, length = 1000)
  @FullTextField
  private String description;

  @Column(name = "avatar_url", nullable = true, length = 500)
  private String avatarUrl;

  @Column(name = "website_url", nullable = true, length = 500)
  @GenericField(name = "website_url", searchable = Searchable.NO)
  private String websiteUrl;

  @Column(nullable = true, length = 120)
  @GenericField
  private String doi;

  @Column(nullable = false, length = 20)
  @GenericField
  private String status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "platform_id", nullable = true)
  @IndexedEmbedded(includePaths = { "slug", "name" })
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private SimpleChallengePlatformEntity platform;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(name = "contributions", includePaths = { "role", "organization_id" })
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private List<ChallengeContributionEntity> contributions;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(name = "submission_types", includePaths = { "name" })
  private List<ChallengeSubmissionTypeEntity> submissionTypes;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "challenge_input_data_type",
    joinColumns = @JoinColumn(name = "challenge_id"),
    inverseJoinColumns = @JoinColumn(name = "edam_concept_id")
  )
  @IndexedEmbedded(
    name = "input_data_types",
    includePaths = { "id", "class_id", "preferred_label" }
  )
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private List<EdamConceptEntity> inputDataTypes;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(includePaths = { "name" })
  private List<ChallengeIncentiveEntity> incentives;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @LazyCollection(LazyCollectionOption.EXTRA)
  // @IndexedEmbedded(includePaths = {"userId"})
  @GenericField(
    name = "starred_count",
    valueBridge = @ValueBridgeRef(type = CollectionSizeBridge.class),
    extraction = @ContainerExtraction(extract = ContainerExtract.NO),
    sortable = Sortable.YES
  )
  private List<ChallengeStar> stars;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
  @IndexedEmbedded(includePaths = { "name" })
  private List<ChallengeCategoryEntity> categories;

  @Column(name = "start_date", columnDefinition = "DATE", nullable = true)
  @GenericField(name = "start_date", sortable = Sortable.YES)
  private LocalDate startDate;

  @Column(name = "end_date", columnDefinition = "DATE", nullable = true)
  @GenericField(name = "end_date", sortable = Sortable.YES)
  private LocalDate endDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "operation_id", nullable = true)
  @IndexedEmbedded(includePaths = { "id", "class_id", "preferred_label" })
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  private EdamConceptEntity operation;

  @Column(name = "created_at")
  @GenericField(name = "created_at", sortable = Sortable.YES)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
