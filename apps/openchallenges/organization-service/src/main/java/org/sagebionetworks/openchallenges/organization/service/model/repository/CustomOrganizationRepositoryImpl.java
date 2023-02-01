package org.sagebionetworks.openchallenges.organization.service.model.repository;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.springframework.stereotype.Repository;

@Repository
public class CustomOrganizationRepositoryImpl implements CustomOrganizationRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Optional<OrganizationEntity> findBySimpleNaturalId(String naturalId) {
    return entityManager
        .unwrap(Session.class)
        .bySimpleNaturalId(OrganizationEntity.class)
        .loadOptional(naturalId);
  }
}
