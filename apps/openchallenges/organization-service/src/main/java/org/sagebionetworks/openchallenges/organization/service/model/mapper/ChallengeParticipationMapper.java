package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationRoleDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.ChallengeParticipationEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class ChallengeParticipationMapper
  extends BaseMapper<ChallengeParticipationEntity, ChallengeParticipationDto> {

  @Override
  public ChallengeParticipationEntity convertToEntity(
    ChallengeParticipationDto dto,
    Object... args
  ) {
    ChallengeParticipationEntity entity = new ChallengeParticipationEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public ChallengeParticipationDto convertToDto(
    ChallengeParticipationEntity entity,
    Object... args
  ) {
    ChallengeParticipationDto dto = new ChallengeParticipationDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto, "organization", "role");
      dto.setOrganizationId(entity.getOrganization().getId());
      dto.setRole(ChallengeParticipationRoleDto.fromValue(entity.getRole()));
    }
    return dto;
  }
}
