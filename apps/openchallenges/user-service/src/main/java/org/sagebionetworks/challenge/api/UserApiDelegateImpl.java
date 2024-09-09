package org.sagebionetworks.challenge.api;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.challenge.model.dto.UserCreateRequestDto;
import org.sagebionetworks.challenge.model.dto.UserCreateResponseDto;
import org.sagebionetworks.challenge.model.dto.UserDto;
import org.sagebionetworks.challenge.model.dto.UsersPageDto;
import org.sagebionetworks.challenge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserApiDelegateImpl implements UserApiDelegate {

  @Autowired
  UserService userService;

  @Override
  public ResponseEntity<UserCreateResponseDto> createUser(UserCreateRequestDto userCreateRequest) {
    return new ResponseEntity<>(userService.createUser(userCreateRequest), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<UserDto> getUser(Long userId) {
    return ResponseEntity.ok(userService.getUser(userId));
  }

  @Override
  public ResponseEntity<UsersPageDto> listUsers(Integer pageNumber, Integer pageSize) {
    return ResponseEntity.ok(userService.listUsers(pageNumber, pageSize));
  }
}
