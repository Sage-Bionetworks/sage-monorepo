package org.sagebionetworks.bixarena.api.model.repository;

import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.ModelErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelErrorRepository extends JpaRepository<ModelErrorEntity, UUID> {}
