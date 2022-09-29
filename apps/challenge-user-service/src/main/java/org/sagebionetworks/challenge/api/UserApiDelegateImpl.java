package org.sagebionetworks.challenge.api;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.challenge.model.dto.UserCreateRequestDto;
import org.sagebionetworks.challenge.model.dto.UserCreateResponseDto;
import org.sagebionetworks.challenge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserApiDelegateImpl implements UserApiDelegate {

  @Autowired UserService userService;

  public ResponseEntity<UserCreateResponseDto> createUser(UserCreateRequestDto userCreateRequest) {
    return ResponseEntity.ok(userService.createUser(userCreateRequest));
  }

  // @Override
  // public ResponseEntity<UserDto> createUser(UserDto userDto) {
  //   return ResponseEntity.ok(userService.createUser(userDto));
  // }

  // @Override
  // public ResponseEntity<UserDto> getUser(Long id) {
  //   return ResponseEntity.ok(userService.getUser(id));
  // }

  // @Override
  // public ResponseEntity<List<UserDto>> listUsers(PageableDto pageable) {
  //   // TODO Take into account pageable.getSort()
  //   List<UserDto> result =
  //       userService.listUsers(PageRequest.of(pageable.getPage(), pageable.getSize()));
  //   return ResponseEntity.ok(result);
  // }
}
