package org.sagebionetworks.agora.api.service;

import org.sagebionetworks.agora.api.model.dto.DataversionDto;
import org.sagebionetworks.agora.api.model.entity.DataVersionEntity;
import org.sagebionetworks.agora.api.model.repository.DataVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataVersionService {

  private final DataVersionRepository dataVersionRepository;

  public DataVersionService(DataVersionRepository dataVersionRepository) {
    this.dataVersionRepository = dataVersionRepository;
  }

  @Transactional(readOnly = true)
  public DataversionDto getDataVersion() {
    DataVersionEntity entity = this.dataVersionRepository.get();

    return DataversionDto.builder()
      .dataFile(entity.dataFile)
      .dataVersion(entity.dataVersion)
      .teamImagesId(entity.teamImagesId)
      .build();
  }
}
