package org.sagebionetworks.openchallenges.challenge.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

@Entity
@Table(name = "challenge_platform")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleChallengePlatformEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false)
  @KeywordField(aggregable = Aggregable.YES)
  private String slug;

  @Column(nullable = false)
  @KeywordField(aggregable = Aggregable.YES)
  private String name;

  @Column(name = "avatar_key", nullable = false)
  private String avatarKey;

  @Column(name = "website_url", nullable = false)
  private String websiteUrl;
}
