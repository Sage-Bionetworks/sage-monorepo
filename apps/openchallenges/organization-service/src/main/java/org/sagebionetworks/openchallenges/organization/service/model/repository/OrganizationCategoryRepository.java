package org.sagebionetworks.openchallenges.organization.service.model.repository;

import java.util.List;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationCategoryEntity;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationCategoryRepository
  extends JpaRepository<OrganizationCategoryEntity, Long> {
  List<OrganizationCategoryEntity> findByOrganization(OrganizationEntity organization);
  void deleteByOrganization(OrganizationEntity organization);
}
