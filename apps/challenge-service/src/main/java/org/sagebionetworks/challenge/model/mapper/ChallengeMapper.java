package org.sagebionetworks.challenge.model.mapper;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.challenge.model.dto.ChallengeDto;
import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.challenge.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

@Slf4j
public class ChallengeMapper extends BaseMapper<ChallengeEntity, ChallengeDto> {
  @Override
  public ChallengeEntity convertToEntity(ChallengeDto dto, Object... args) {
    log.info("DTO to Entity");
    ChallengeEntity ChallengeEntity = new ChallengeEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, ChallengeEntity);
    }
    return ChallengeEntity;
  }

  @Override
  public ChallengeDto convertToDto(ChallengeEntity entity, Object... args) {
    log.info("Entity to DTO");
    ChallengeDto user = new ChallengeDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, user);
      user.setStatus(ChallengeStatusDto.fromValue(entity.getStatus()));
    }
    return user;
  }
}
