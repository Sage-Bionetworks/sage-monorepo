package org.sagebionetworks.challenge.model.mapper;

import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.sagebionetworks.challenge.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class UserMapper extends BaseMapper<UserEntity, User> {
  @Override
  public UserEntity convertToEntity(User dto, Object... args) {
    UserEntity userEntity = new UserEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, userEntity);
    }
    return userEntity;
  }

  @Override
  public User convertToDto(UserEntity entity, Object... args) {
    User user = new User();
    if (entity != null) {
      BeanUtils.copyProperties(entity, user);
    }
    return user;
  }
}
