package org.sagebionetworks.bixarena.auth.service.model.repository;

import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.bixarena.auth.service.model.entity.ExternalAccountEntity;
import org.sagebionetworks.bixarena.auth.service.model.entity.ExternalAccountEntity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalAccountRepository extends JpaRepository<ExternalAccountEntity, UUID> {
  Optional<ExternalAccountEntity> findByProviderAndExternalId(Provider provider, String externalId);
}
