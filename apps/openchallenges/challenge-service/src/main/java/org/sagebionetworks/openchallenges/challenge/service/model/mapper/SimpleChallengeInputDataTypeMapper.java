package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.SimpleChallengeInputDataTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.SimpleChallengeInputDataTypeEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class SimpleChallengeInputDataTypeMapper
    extends BaseMapper<SimpleChallengeInputDataTypeEntity, SimpleChallengeInputDataTypeDto> {
  @Override
  public SimpleChallengeInputDataTypeEntity convertToEntity(
      SimpleChallengeInputDataTypeDto dto, Object... args) {
    SimpleChallengeInputDataTypeEntity entity = new SimpleChallengeInputDataTypeEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public SimpleChallengeInputDataTypeDto convertToDto(
      SimpleChallengeInputDataTypeEntity entity, Object... args) {
    SimpleChallengeInputDataTypeDto dto = new SimpleChallengeInputDataTypeDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
