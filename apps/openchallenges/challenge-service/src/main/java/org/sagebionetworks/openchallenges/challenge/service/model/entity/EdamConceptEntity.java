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
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.sagebionetworks.openchallenges.challenge.service.model.search.EdamSectionValueBridge;

@Entity
@Table(name = "edam_concept")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Indexed(index = "openchallenges-edam-concept")
public class EdamConceptEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(name = "class_id", nullable = false)
  @FullTextField(name = "class_id")
  @KeywordField(
      name = "section",
      valueBridge = @ValueBridgeRef(type = EdamSectionValueBridge.class))
  private String classId;

  @Column(name = "preferred_label", nullable = false)
  @FullTextField(name = "preferred_label")
  @GenericField(name = "preferred_label_sort", sortable = Sortable.YES)
  private String preferredLabel;
}
