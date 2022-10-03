package org.sagebionetworks.challenge.model.mapper;

import org.sagebionetworks.challenge.model.dto.UserDto;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.sagebionetworks.challenge.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class UserMapper extends BaseMapper<UserEntity, UserDto> {
  @Override
  public UserEntity convertToEntity(UserDto dto, Object... args) {
    UserEntity userEntity = new UserEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, userEntity);
    }
    return userEntity;
  }

  @Override
  public UserDto convertToDto(UserEntity entity, Object... args) {
    UserDto user = new UserDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, user);
    }
    return user;
  }
}
