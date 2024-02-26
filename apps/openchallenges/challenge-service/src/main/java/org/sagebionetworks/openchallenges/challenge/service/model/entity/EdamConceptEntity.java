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
import org.sagebionetworks.openchallenges.challenge.service.model.search.ChallengePlatformNameValueBridge;

@Entity
@Table(name = "edam_concept")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdamConceptEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(name = "class_id", nullable = false)
  private String classId;

  @Column(name = "preferred_label", nullable = false)
  private String preferredLabel;
}
