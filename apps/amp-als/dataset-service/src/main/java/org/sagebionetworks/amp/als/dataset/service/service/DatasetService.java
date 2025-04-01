package org.sagebionetworks.amp.als.dataset.service.service;

import org.sagebionetworks.amp.als.dataset.service.exception.DatasetNotFoundException;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetsPageDto;
import org.sagebionetworks.amp.als.dataset.service.model.entity.DatasetEntity;
import org.sagebionetworks.amp.als.dataset.service.model.mapper.DatasetMapper;
import org.sagebionetworks.amp.als.dataset.service.model.repository.DatasetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatasetService {

  private final DatasetRepository datasetRepository;

  public DatasetService(DatasetRepository datasetRepository) {
    this.datasetRepository = datasetRepository;
  }

  private DatasetMapper datasetMapper = new DatasetMapper();

  @Transactional(readOnly = true)
  public DatasetDto getDataset(Long datasetId) {
    DatasetEntity datasetEntity = getDatasetEntity(datasetId);
    return datasetMapper.convertToDto(datasetEntity);
  }

  private DatasetEntity getDatasetEntity(Long datasetId) throws DatasetNotFoundException {
    return datasetRepository
      .findById(datasetId)
      .orElseThrow(() ->
        new DatasetNotFoundException(
          String.format("The dataset with ID %d does not exist.", datasetId)
        )
      );
  }
}
