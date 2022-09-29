package org.sagebionetworks.challenge.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.sagebionetworks.challenge.exception.GlobalErrorCode;
import org.sagebionetworks.challenge.exception.InvalidUserException;
import org.sagebionetworks.challenge.exception.UserAlreadyRegisteredException;
import org.sagebionetworks.challenge.model.dto.UserCreateRequestDto;
import org.sagebionetworks.challenge.model.dto.UserCreateResponseDto;
import org.sagebionetworks.challenge.model.dto.UserDto;
import org.sagebionetworks.challenge.model.dto.UserStatusDto;
import org.sagebionetworks.challenge.model.dto.UserUpdateRequestDto;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.sagebionetworks.challenge.model.mapper.UserMapper;
import org.sagebionetworks.challenge.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

  @Autowired private KeycloakUserService keycloakUserService;

  @Autowired private UserRepository userRepository;

  private UserMapper userMapper = new UserMapper();

  @Transactional
  public UserCreateResponseDto createUser(UserCreateRequestDto userCreateRequest) {
    if (keycloakUserService.getUserByUsername(userCreateRequest.getLogin()).isPresent()) {
      throw new UserAlreadyRegisteredException(
          "This username is already registered.", GlobalErrorCode.ERROR_USERNAME_REGISTERED);
    }

    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setEmail(userCreateRequest.getEmail());
    userRepresentation.setEmailVerified(false);
    userRepresentation.setEnabled(true);
    userRepresentation.setUsername(userCreateRequest.getLogin());

    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
    credentialRepresentation.setValue(userCreateRequest.getPassword());
    credentialRepresentation.setTemporary(false);
    userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

    Integer userCreationResponse = keycloakUserService.createUser(userRepresentation);

    if (userCreationResponse == 201) {
      Optional<UserRepresentation> representation =
          keycloakUserService.getUserByUsername(userCreateRequest.getLogin());
      UserEntity user = new UserEntity();
      user.setAuthId(representation.get().getId());
      user.setStatus(UserStatusDto.PENDING);
      UserEntity savedUser = userRepository.save(user);
      return UserCreateResponseDto.builder().id(savedUser.getId()).build();
    }

    throw new InvalidUserException(
        "Unable to create the new user", GlobalErrorCode.ERROR_INVALID_USER);
  }

  @Transactional(readOnly = true)
  public List<UserDto> listUsers(Pageable pageable) {
    Page<UserEntity> userEntities = userRepository.findAll(pageable);
    List<UserDto> users = userMapper.convertToDtoList(userEntities.getContent());
    users.forEach(
        user -> {
          UserRepresentation userRepresentation = keycloakUserService.getUser(user.getAuthId());
          user.setEmail(userRepresentation.getEmail());
        });
    return users;
  }

  @Transactional(readOnly = true)
  public UserDto getUser(Long userId) {
    UserDto user =
        userMapper.convertToDto(
            userRepository.findById(userId).orElseThrow(EntityNotFoundException::new));
    UserRepresentation userRepresentation = keycloakUserService.getUser(user.getAuthId());
    user.setEmail(userRepresentation.getEmail());
    return user;
  }

  // TODO Review this function
  public UserDto updateUser(Long id, UserUpdateRequestDto userUpdateRequest) {
    UserEntity userEntity = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

    if (userUpdateRequest.getStatus() == UserStatusDto.APPROVED) {
      UserRepresentation userRepresentation = keycloakUserService.getUser(userEntity.getAuthId());
      userRepresentation.setEnabled(true);
      userRepresentation.setEmailVerified(true);
      keycloakUserService.updateUser(userRepresentation);
    }

    userEntity.setStatus(userUpdateRequest.getStatus());
    return userMapper.convertToDto(userRepository.save(userEntity));
  }
}
