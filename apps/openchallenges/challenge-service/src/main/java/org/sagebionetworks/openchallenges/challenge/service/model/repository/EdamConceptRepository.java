package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import org.sagebionetworks.openchallenges.challenge.service.model.entity.EdamConceptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdamConceptRepository
  extends JpaRepository<EdamConceptEntity, Integer>, CustomEdamConceptRepository {}
