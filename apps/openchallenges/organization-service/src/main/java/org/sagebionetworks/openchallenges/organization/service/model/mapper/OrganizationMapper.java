package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class OrganizationMapper extends BaseMapper<OrganizationEntity, OrganizationDto> {

  @Override
  public OrganizationEntity convertToEntity(OrganizationDto dto, Object... args) {
    OrganizationEntity entity = new OrganizationEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public OrganizationDto convertToDto(OrganizationEntity entity, Object... args) {
    OrganizationDto dto = new OrganizationDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
