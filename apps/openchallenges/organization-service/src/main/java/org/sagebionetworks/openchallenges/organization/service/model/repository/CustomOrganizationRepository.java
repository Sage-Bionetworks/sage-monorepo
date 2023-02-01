package org.sagebionetworks.openchallenges.organization.service.model.repository;

import java.util.Optional;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;

public interface CustomOrganizationRepository {

  Optional<OrganizationEntity> findBySimpleNaturalId(String naturalId);
}
