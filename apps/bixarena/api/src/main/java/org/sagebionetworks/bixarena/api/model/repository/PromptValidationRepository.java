package org.sagebionetworks.bixarena.api.model.repository;

import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.PromptValidationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptValidationRepository extends JpaRepository<PromptValidationEntity, UUID> {}
