package org.sagebionetworks.openchallenges.organization.service.model.repository;

import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository
    extends JpaRepository<OrganizationEntity, Long>, CustomOrganizationRepository {}
