package org.sagebionetworks.amp.als.dataset.service.service;

import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.amp.als.dataset.service.exception.DatasetNotFoundException;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetSearchQueryDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetsPageDto;
import org.sagebionetworks.amp.als.dataset.service.model.entity.DatasetEntity;
import org.sagebionetworks.amp.als.dataset.service.model.mapper.DatasetMapper;
import org.sagebionetworks.amp.als.dataset.service.model.repository.DatasetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatasetService {

  private final DatasetRepository datasetRepository;

  public DatasetService(DatasetRepository datasetRepository) {
    this.datasetRepository = datasetRepository;
  }

  private DatasetMapper datasetMapper = new DatasetMapper();

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("name", "description");

  @Transactional(readOnly = true)
  public DatasetsPageDto listDatasets(DatasetSearchQueryDto query) {
    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<DatasetEntity> datasetEntitiesPage = datasetRepository.findAll(
      pageable,
      query,
      fieldsToSearchBy.toArray(new String[0])
    );

    List<DatasetDto> datasets = datasetMapper.convertToDtoList(datasetEntitiesPage.getContent());

    return DatasetsPageDto.builder()
      .datasets(datasets)
      .number(datasetEntitiesPage.getNumber())
      .size(datasetEntitiesPage.getSize())
      .totalElements(datasetEntitiesPage.getTotalElements())
      .totalPages(datasetEntitiesPage.getTotalPages())
      .hasNext(datasetEntitiesPage.hasNext())
      .hasPrevious(datasetEntitiesPage.hasPrevious())
      .build();
  }

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
