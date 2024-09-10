package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.EdamConceptEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class EdamConceptMapper extends BaseMapper<EdamConceptEntity, EdamConceptDto> {

  @Override
  public EdamConceptEntity convertToEntity(EdamConceptDto dto, Object... args) {
    EdamConceptEntity entity = new EdamConceptEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public EdamConceptDto convertToDto(EdamConceptEntity entity, Object... args) {
    EdamConceptDto dto = new EdamConceptDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
