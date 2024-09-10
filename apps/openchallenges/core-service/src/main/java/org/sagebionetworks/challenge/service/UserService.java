package org.sagebionetworks.challenge.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.challenge.exception.EntityNotFoundException;
import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.sagebionetworks.challenge.model.mapper.UserMapper;
import org.sagebionetworks.challenge.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private UserMapper userMapper = new UserMapper();

  private final UserRepository userRepository;

  public User readUser(String identification) {
    UserEntity userEntity = userRepository
      .findByIdentificationNumber(identification)
      .orElseThrow(EntityNotFoundException::new);
    return userMapper.convertToDto(userEntity);
  }

  public List<User> readUsers(Pageable pageable) {
    return userMapper.convertToDtoList(userRepository.findAll(pageable).getContent());
  }
}
