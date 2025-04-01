package org.sagebionetworks.amp.als.dataset.service.model.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// import org.hibernate.annotations.LazyCollection;
// import org.hibernate.annotations.LazyCollectionOption;
// import org.hibernate.search.engine.backend.types.Sortable;
// import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
// import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
// import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtract;
// import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtraction;
// import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
// import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
// import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
// import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
// import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
// import org.sagebionetworks.openchallenges.challenge.service.model.search.CollectionSizeBridge;

@Entity
@Table(name = "dataset")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// @Indexed(index = "openchallenges-challenge")
public class DatasetEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false)
  // @FullTextField
  private String name;

  @Column(nullable = false)
  // @FullTextField
  private String description;

  @Column(name = "created_at")
  // @GenericField(name = "created_at", sortable = Sortable.YES)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
