package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamOperationDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.EdamOperationEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class EdamOperationMapper extends BaseMapper<EdamOperationEntity, EdamOperationDto> {
  @Override
  public EdamOperationEntity convertToEntity(EdamOperationDto dto, Object... args) {
    EdamOperationEntity entity = new EdamOperationEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public EdamOperationDto convertToDto(EdamOperationEntity entity, Object... args) {
    EdamOperationDto dto = new EdamOperationDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
