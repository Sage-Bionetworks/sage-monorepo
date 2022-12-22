package org.sagebionetworks.challenge.model.mapper;

import org.sagebionetworks.challenge.model.dto.ChallengeDifficultyDto;
import org.sagebionetworks.challenge.model.dto.ChallengeDto;
import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.challenge.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class ChallengeMapper extends BaseMapper<ChallengeEntity, ChallengeDto> {
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
    }
    return dto;
  }
}
