package org.sagebionetworks.openchallenges.organization.service.model.repository;

import java.util.List;
import org.sagebionetworks.openchallenges.organization.service.model.entity.ChallengeParticipationEntity;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeParticipationRepository
  extends JpaRepository<ChallengeParticipationEntity, Long> {
  List<ChallengeParticipationEntity> findByOrganization(OrganizationEntity organization);
  void deleteByOrganization(OrganizationEntity organization);
}
