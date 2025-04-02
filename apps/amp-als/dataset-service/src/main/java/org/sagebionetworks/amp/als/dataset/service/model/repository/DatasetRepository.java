package org.sagebionetworks.amp.als.dataset.service.model.repository;

import org.sagebionetworks.amp.als.dataset.service.model.entity.DatasetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRepository
  extends JpaRepository<DatasetEntity, Long>, CustomDatasetRepository {}
