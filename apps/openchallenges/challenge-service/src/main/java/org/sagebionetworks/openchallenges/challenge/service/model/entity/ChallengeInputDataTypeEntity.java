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
import org.sagebionetworks.openchallenges.challenge.service.model.search.ChallengeInputDataTypeNameValueBridge;

@Entity
@Table(name = "challenge_input_data_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Indexed(index = "openchallenges-challenge-input-data-type")
public class ChallengeInputDataTypeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false)
  private String slug;

  @Column(nullable = false)
  @FullTextField()
  @GenericField(
      name = "name_sort",
      valueBridge = @ValueBridgeRef(type = ChallengeInputDataTypeNameValueBridge.class),
      sortable = Sortable.YES)
  private String name;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
