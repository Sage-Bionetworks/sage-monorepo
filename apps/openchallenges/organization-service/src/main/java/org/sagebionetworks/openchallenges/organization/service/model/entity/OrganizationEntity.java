package org.sagebionetworks.openchallenges.organization.service.model.entity;

import java.time.OffsetDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.sagebionetworks.openchallenges.organization.service.model.search.OrganizationNameValueBridge;

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
  @IndexedEmbedded(name = "challenge_contributions", includePaths = { "role" })
  private List<ChallengeContributionEntity> challengeContributions;

  @Column(nullable = true)
  @FullTextField
  private String description;

  @Column(name = "created_at")
  @GenericField(name = "created_at", sortable = Sortable.YES)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  @Column(name = "acronym", nullable = true)
  private String acronym;
}
