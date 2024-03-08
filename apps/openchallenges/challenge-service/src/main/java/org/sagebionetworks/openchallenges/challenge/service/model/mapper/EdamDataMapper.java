package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamDataDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.EdamDataEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class EdamDataMapper extends BaseMapper<EdamDataEntity, EdamDataDto> {
  @Override
  public EdamDataEntity convertToEntity(EdamDataDto dto, Object... args) {
    EdamDataEntity entity = new EdamDataEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public EdamDataDto convertToDto(EdamDataEntity entity, Object... args) {
    EdamDataDto dto = new EdamDataDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
