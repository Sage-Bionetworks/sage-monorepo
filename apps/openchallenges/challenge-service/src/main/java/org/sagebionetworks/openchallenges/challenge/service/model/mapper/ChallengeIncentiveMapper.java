package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeIncentiveEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;

public class ChallengeIncentiveMapper
  extends BaseMapper<ChallengeIncentiveEntity, ChallengeIncentiveDto> {

  @Override
  public ChallengeIncentiveEntity convertToEntity(ChallengeIncentiveDto dto, Object... args) {
    ChallengeIncentiveEntity entity = new ChallengeIncentiveEntity();
    if (dto != null && args.length > 0 && args[0] instanceof ChallengeEntity) {
      ChallengeEntity challenge = (ChallengeEntity) args[0];
      entity.setName(dto.getValue());
      entity.setChallenge(challenge);
      entity.setCreatedAt(OffsetDateTime.now());
    }
    return entity;
  }

  @Override
  public ChallengeIncentiveDto convertToDto(ChallengeIncentiveEntity entity, Object... args) {
    if (entity != null) {
      return ChallengeIncentiveDto.fromValue(entity.getName());
    }
    return null;
  }

  /**
   * Convert a list of ChallengeIncentiveDto to a list of ChallengeIncentiveEntity
   */
  public List<ChallengeIncentiveEntity> convertToEntityList(
    List<ChallengeIncentiveDto> dtos,
    ChallengeEntity challenge
  ) {
    if (dtos == null) {
      return new ArrayList<>();
    }
    return dtos.stream().map(dto -> convertToEntity(dto, challenge)).collect(Collectors.toList());
  }

  /**
   * Convert a list of ChallengeIncentiveEntity to a list of ChallengeIncentiveDto
   */
  public List<ChallengeIncentiveDto> convertToDtoList(List<ChallengeIncentiveEntity> entities) {
    if (entities == null) {
      return new ArrayList<>();
    }
    return entities.stream().map(this::convertToDto).collect(Collectors.toList());
  }
}
