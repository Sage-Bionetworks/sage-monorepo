package org.sagebionetworks.openchallenges.challenge.service.model.entity;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.sagebionetworks.openchallenges.challenge.service.model.search.ChallengePlatformNameValueBridge;

@Entity
@Table(name = "challenge_platform")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Indexed(index = "openchallenges-challenge-platform")
public class ChallengePlatformEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false)
  @KeywordField()
  private String slug;

  @Column(nullable = false)
  @FullTextField()
  @GenericField(
      name = "name_sort",
      valueBridge = @ValueBridgeRef(type = ChallengePlatformNameValueBridge.class),
      sortable = Sortable.YES)
  private String name;

  @Column(name = "avatar_url", nullable = false)
  private String avatarUrl;

  @Column(name = "website_url", nullable = false)
  private String websiteUrl;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
