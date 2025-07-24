package org.sagebionetworks.openchallenges.challenge.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
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

  @Column(nullable = false, length = 255)
  @KeywordField
  private String slug;

  @Column(nullable = false, length = 255)
  @FullTextField
  @GenericField(
    name = "name_sort",
    valueBridge = @ValueBridgeRef(type = ChallengePlatformNameValueBridge.class),
    sortable = Sortable.YES
  )
  private String name;

  @Column(name = "avatar_key", nullable = true, length = 255)
  private String avatarKey;

  @Column(name = "website_url", nullable = false, length = 500)
  private String websiteUrl;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime updatedAt;
}
