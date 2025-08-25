package org.sagebionetworks.amp.als.dataset.service.model.mapper;

import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetDto;
import org.sagebionetworks.amp.als.dataset.service.model.entity.DatasetEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class DatasetMapper extends BaseMapper<DatasetEntity, DatasetDto> {

  @Override
  public DatasetEntity convertToEntity(DatasetDto dto, Object... args) {
    DatasetEntity entity = new DatasetEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public DatasetDto convertToDto(DatasetEntity entity, Object... args) {
    DatasetDto dto = new DatasetDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
