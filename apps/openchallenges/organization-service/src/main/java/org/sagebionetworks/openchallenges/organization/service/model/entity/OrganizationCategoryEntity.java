package org.sagebionetworks.openchallenges.organization.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

@Entity
@Table(name = "organization_category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_category_seq")
  @SequenceGenerator(
    name = "organization_category_seq",
    sequenceName = "organization_category_id_seq",
    allocationSize = 1
  )
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false)
  @GenericField
  private String category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id", nullable = false)
  private OrganizationEntity organization;
}
