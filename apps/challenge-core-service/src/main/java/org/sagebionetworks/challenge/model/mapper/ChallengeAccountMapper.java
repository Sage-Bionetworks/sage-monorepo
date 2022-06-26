package org.sagebionetworks.challenge.model.mapper;

import org.sagebionetworks.challenge.model.dto.ChallengeAccount;
import org.sagebionetworks.challenge.model.entity.ChallengeAccountEntity;
import org.springframework.beans.BeanUtils;

public class ChallengeAccountMapper extends BaseMapper<ChallengeAccountEntity, ChallengeAccount> {

  @Override
  public ChallengeAccountEntity convertToEntity(ChallengeAccount dto, Object... args) {
    ChallengeAccountEntity entity = new ChallengeAccountEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity, "user");
    }
    return entity;
  }

  @Override
  public ChallengeAccount convertToDto(ChallengeAccountEntity entity, Object... args) {
    ChallengeAccount dto = new ChallengeAccount();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto, "user");
    }
    return dto;
  }
}
