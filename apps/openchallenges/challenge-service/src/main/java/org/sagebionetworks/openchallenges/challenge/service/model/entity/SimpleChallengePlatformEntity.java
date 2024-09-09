package org.sagebionetworks.openchallenges.challenge.service.model.entity;

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
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

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
  @GenericField
  private String slug;

  @Column(nullable = false)
  @FullTextField
  private String name;

  @Column(name = "avatar_url", nullable = false)
  private String avatarUrl;

  @Column(name = "website_url", nullable = false)
  private String websiteUrl;
}
