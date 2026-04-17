package org.sagebionetworks.bixarena.api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "example_prompt_categorization_category", schema = "api")
@IdClass(ExamplePromptCategorizationCategoryEntity.CategoryKey.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamplePromptCategorizationCategoryEntity {

  @Id
  @Column(name = "categorization_id", nullable = false)
  private UUID categorizationId;

  @Id
  @Column(name = "category", nullable = false, length = 100)
  private String category;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CategoryKey implements Serializable {

    private UUID categorizationId;
    private String category;
  }
}
