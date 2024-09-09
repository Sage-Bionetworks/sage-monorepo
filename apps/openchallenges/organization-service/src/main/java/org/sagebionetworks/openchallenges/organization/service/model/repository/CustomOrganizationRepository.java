package org.sagebionetworks.openchallenges.organization.service.model.repository;

import java.util.Optional;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomOrganizationRepository {
  Optional<OrganizationEntity> findBySimpleNaturalId(String naturalId);

  Page<OrganizationEntity> findAll(
    Pageable pageable,
    OrganizationSearchQueryDto query,
    String... fields
  );
}
