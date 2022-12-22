package org.sagebionetworks.challenge.model.mapper;

import org.sagebionetworks.challenge.model.dto.SimpleChallengePlatformDto;
import org.sagebionetworks.challenge.model.entity.SimpleChallengePlatformEntity;
import org.sagebionetworks.challenge.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class SimpleChallengePlatformMapper
    extends BaseMapper<SimpleChallengePlatformEntity, SimpleChallengePlatformDto> {
  @Override
  public SimpleChallengePlatformEntity convertToEntity(
      SimpleChallengePlatformDto dto, Object... args) {
    SimpleChallengePlatformEntity entity = new SimpleChallengePlatformEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public SimpleChallengePlatformDto convertToDto(
      SimpleChallengePlatformEntity entity, Object... args) {
    SimpleChallengePlatformDto dto = new SimpleChallengePlatformDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
