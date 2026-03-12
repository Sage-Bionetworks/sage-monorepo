package org.sagebionetworks.bixarena.api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "quest_post", schema = "api")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestPostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "quest_id", nullable = false)
  private Long questId;

  @Column(name = "post_index", nullable = false)
  private Integer postIndex;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "title", nullable = false, length = 300)
  private String title;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "images", nullable = false, columnDefinition = "jsonb")
  private String images;

  @Column(name = "publish_date")
  private OffsetDateTime publishDate;

  @Column(name = "required_progress")
  private Integer requiredProgress;

  @Column(name = "required_tier", length = 20)
  private String requiredTier;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
