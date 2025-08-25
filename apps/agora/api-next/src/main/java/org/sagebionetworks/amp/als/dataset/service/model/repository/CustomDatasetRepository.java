package org.sagebionetworks.amp.als.dataset.service.model.repository;

import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetSearchQueryDto;
import org.sagebionetworks.amp.als.dataset.service.model.entity.DatasetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomDatasetRepository {
  Page<DatasetEntity> findAll(Pageable pageable, DatasetSearchQueryDto query, String... fields);
}
