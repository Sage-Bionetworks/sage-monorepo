package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class ChallengeMapper extends BaseMapper<ChallengeEntity, ChallengeDto> {

  private SimpleChallengePlatformMapper platformMapper = new SimpleChallengePlatformMapper();
  private SimpleChallengeInputDataTypeMapper inputDataTypeMapper =
      new SimpleChallengeInputDataTypeMapper();

  @Override
  public ChallengeEntity convertToEntity(ChallengeDto dto, Object... args) {
    ChallengeEntity entity = new ChallengeEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public ChallengeDto convertToDto(ChallengeEntity entity, Object... args) {
    ChallengeDto dto = new ChallengeDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto, "stars", "inputDataTypes");
      dto.setStatus(ChallengeStatusDto.fromValue(entity.getStatus()));
      dto.setPlatform(platformMapper.convertToDto(entity.getPlatform()));
      dto.submissionTypes(
          entity.getSubmissionTypes().stream()
              .map(o -> ChallengeSubmissionTypeDto.fromValue(o.getName()))
              .toList());
      dto.incentives(
          entity.getIncentives().stream()
              .map(o -> ChallengeIncentiveDto.fromValue(o.getName()))
              .toList());
      dto.inputDataTypes(inputDataTypeMapper.convertToDtoList(entity.getInputDataTypes()));
      dto.starredCount(entity.getStars().size());
    }
    return dto;
  }
}
