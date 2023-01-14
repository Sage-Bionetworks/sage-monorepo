package org.sagebionetworks.challenge.model.mapper;

import org.sagebionetworks.challenge.model.dto.OrganizationDto;
import org.sagebionetworks.challenge.model.entity.OrganizationEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class OrganizationMapper extends BaseMapper<OrganizationEntity, OrganizationDto> {
  @Override
  public OrganizationEntity convertToEntity(OrganizationDto dto, Object... args) {
    OrganizationEntity OrganizationEntity = new OrganizationEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, OrganizationEntity);
    }
    return OrganizationEntity;
  }

  @Override
  public OrganizationDto convertToDto(OrganizationEntity entity, Object... args) {
    OrganizationDto user = new OrganizationDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, user);
    }
    return user;
  }
}
