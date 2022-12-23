package org.sagebionetworks.challenge.model.mapper;

import org.sagebionetworks.challenge.model.dto.ChallengePlatformDto;
import org.sagebionetworks.challenge.model.entity.ChallengePlatformEntity;
import org.sagebionetworks.challenge.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class ChallengePlatformMapper
    extends BaseMapper<ChallengePlatformEntity, ChallengePlatformDto> {
  @Override
  public ChallengePlatformEntity convertToEntity(ChallengePlatformDto dto, Object... args) {
    ChallengePlatformEntity entity = new ChallengePlatformEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public ChallengePlatformDto convertToDto(ChallengePlatformEntity entity, Object... args) {
    ChallengePlatformDto dto = new ChallengePlatformDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
