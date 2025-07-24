package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class ChallengeContributionMapper
  extends BaseMapper<ChallengeContributionEntity, ChallengeContributionDto> {

  private static final Logger logger = LoggerFactory.getLogger(ChallengeContributionMapper.class);

  @Override
  public ChallengeContributionEntity convertToEntity(ChallengeContributionDto dto, Object... args) {
    ChallengeContributionEntity entity = new ChallengeContributionEntity();
    logger.error("ChallengeContributionEntity.convertToEntity() not implemented.");
    return entity;
  }

  @Override
  public ChallengeContributionDto convertToDto(ChallengeContributionEntity entity, Object... args) {
    ChallengeContributionDto dto = new ChallengeContributionDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
      dto.setChallengeId(entity.getChallenge().getId());
      dto.setRole(ChallengeContributionRoleDto.fromValue(entity.getRole()));
    }
    return dto;
  }
}
