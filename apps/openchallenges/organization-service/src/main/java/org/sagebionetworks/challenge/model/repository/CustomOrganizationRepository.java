package org.sagebionetworks.challenge.model.repository;

import java.util.Optional;
import org.sagebionetworks.challenge.model.entity.OrganizationEntity;

public interface CustomOrganizationRepository {

  Optional<OrganizationEntity> findBySimpleNaturalId(String naturalId);
}
