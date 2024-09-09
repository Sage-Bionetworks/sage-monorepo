package org.sagebionetworks.openchallenges.organization.service.model.repository;

import java.util.Optional;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository
  extends JpaRepository<OrganizationEntity, Long>, CustomOrganizationRepository {
  Optional<OrganizationEntity> findByIdOrLogin(Long id, String login);
}
