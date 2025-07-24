package org.sagebionetworks.openchallenges.organization.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBinderRef;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtract;
import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtraction;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.sagebionetworks.openchallenges.organization.service.model.search.OrganizationNameValueBridge;
import org.sagebionetworks.openchallenges.organization.service.model.search.UniqueChallengeCountValueBinder;

@Entity
@Table(name = "organization")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Indexed(index = "openchallenges-organization")
public class OrganizationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  @GenericField(name = "id")
  private Long id;

  @Column(nullable = false)
  @FullTextField
  @GenericField(
    name = "name_sort",
    valueBridge = @ValueBridgeRef(type = OrganizationNameValueBridge.class),
    sortable = Sortable.YES
  )
  private String name;

  @NaturalId(mutable = true)
  @Column(nullable = false, unique = true)
  private String login;

  @Column(name = "avatar_key", nullable = true)
  private String avatarKey;

  @Column(name = "website_url", nullable = true)
  private String websiteUrl;

  @Column(name = "challenge_count", nullable = false)
  @GenericField(name = "challenge_count", sortable = Sortable.YES)
  private Integer challengeCount;

  @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
  @IndexedEmbedded(name = "categories", includePaths = { "category" })
  private List<OrganizationCategoryEntity> categories;

  @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
  @IndexedEmbedded(name = "challenge_participations", includePaths = { "role" })
  @GenericField(
    name = "challenge_count_2",
    valueBinder = @ValueBinderRef(type = UniqueChallengeCountValueBinder.class),
    extraction = @ContainerExtraction(extract = ContainerExtract.NO),
    sortable = Sortable.YES
  )
  private List<ChallengeParticipationEntity> challengeParticipations;

  @Column(nullable = true)
  @FullTextField
  private String description;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  @GenericField(name = "created_at", sortable = Sortable.YES)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime updatedAt;

  @Column(name = "acronym", nullable = true)
  private String acronym;
}
