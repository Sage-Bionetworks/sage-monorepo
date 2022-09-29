package org.sagebionetworks.challenge.api;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.challenge.model.dto.UserCreateRequestDto;
import org.sagebionetworks.challenge.model.dto.UserCreateResponseDto;
import org.sagebionetworks.challenge.model.dto.UserDto;
import org.sagebionetworks.challenge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserApiDelegateImpl implements UserApiDelegate {

  @Autowired UserService userService;

  @Override
  public ResponseEntity<UserCreateResponseDto> createUser(UserCreateRequestDto userCreateRequest) {
    return new ResponseEntity<UserCreateResponseDto>(
        userService.createUser(userCreateRequest), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<UserDto> getUser(Long userId) {
    return ResponseEntity.ok(userService.getUser(userId));
  }

  // @Override
  // public ResponseEntity<List<UserDto>> listUsers(PageableDto pageable) {
  //   // TODO Take into account pageable.getSort()
  //   List<UserDto> result =
  //       userService.listUsers(PageRequest.of(pageable.getPage(), pageable.getSize()));
  //   return ResponseEntity.ok(result);
  // }
}
