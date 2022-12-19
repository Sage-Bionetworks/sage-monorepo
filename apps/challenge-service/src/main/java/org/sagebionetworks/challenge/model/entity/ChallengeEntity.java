package org.sagebionetworks.challenge.model.entity;

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

/** The Challenge information saved to DB. */
@Entity
@Table(name = "challenge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String status;

  // @Column(nullable = false)
  // private String email;

  // @Column(nullable = false, unique = true)
  // private String login;

  // @Column(name = "avatar_url", nullable = true)
  // private String avatarUrl;

  // @Column(name = "website_url")
  // private String websiteUrl;

  // @Column() private String description;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
