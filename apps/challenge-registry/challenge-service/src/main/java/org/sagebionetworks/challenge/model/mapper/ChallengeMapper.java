package org.sagebionetworks.challenge.model.mapper;

import org.sagebionetworks.challenge.model.dto.ChallengeDifficultyDto;
import org.sagebionetworks.challenge.model.dto.ChallengeDto;
import org.sagebionetworks.challenge.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.sagebionetworks.challenge.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class ChallengeMapper extends BaseMapper<ChallengeEntity, ChallengeDto> {

  private SimpleChallengePlatformMapper platformMapper = new SimpleChallengePlatformMapper();

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
      BeanUtils.copyProperties(entity, dto);
      dto.setStatus(ChallengeStatusDto.fromValue(entity.getStatus()));
      dto.setDifficulty(ChallengeDifficultyDto.fromValue(entity.getDifficulty()));
      dto.setPlatform(platformMapper.convertToDto(entity.getPlatform()));
      dto.submissionTypes(
          entity.getSubmissionTypes().stream()
              .map(o -> ChallengeSubmissionTypeDto.fromValue(o.getName()))
              .toList());
      dto.incentives(
          entity.getIncentives().stream()
              .map(o -> ChallengeIncentiveDto.fromValue(o.getName()))
              .toList());
    }
    return dto;
  }
}
