package org.sagebionetworks.openchallenges.organization.service.model.repository;

import java.util.List;
import org.sagebionetworks.openchallenges.organization.service.model.entity.ChallengeContributionEntity;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeContributionRepository
  extends JpaRepository<ChallengeContributionEntity, Long> {
  List<ChallengeContributionEntity> findByOrganization(OrganizationEntity organization);
  void deleteByOrganization(OrganizationEntity organization);
}
