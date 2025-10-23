package org.sagebionetworks.bixarena.api.model.repository;

import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository
  extends JpaRepository<VoteEntity, UUID>, JpaSpecificationExecutor<VoteEntity> {}
