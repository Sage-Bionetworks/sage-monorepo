package org.sagebionetworks.challenge.model.mapper;

import org.sagebionetworks.challenge.util.model.mapper.BaseMapper;
import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.springframework.beans.BeanUtils;

public class UserMapper extends BaseMapper<UserEntity, User> {
  private ChallengeAccountMapper challengeAccountMapper = new ChallengeAccountMapper();

  @Override
  public UserEntity convertToEntity(User dto, Object... args) {
    UserEntity entity = new UserEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity, "accounts");
      entity.setAccounts(challengeAccountMapper.convertToEntityList(dto.getChallengeAccounts()));
    }
    return entity;
  }

  @Override
  public User convertToDto(UserEntity entity, Object... args) {
    User dto = new User();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto, "accounts");
      dto.setChallengeAccounts(challengeAccountMapper.convertToDtoList(entity.getAccounts()));
    }
    return dto;
  }
}
