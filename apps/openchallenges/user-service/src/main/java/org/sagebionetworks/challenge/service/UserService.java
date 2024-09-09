package org.sagebionetworks.challenge.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.sagebionetworks.challenge.exception.InvalidUserException;
import org.sagebionetworks.challenge.exception.UserNotFoundException;
import org.sagebionetworks.challenge.exception.UsernameAlreadyExistsException;
import org.sagebionetworks.challenge.model.dto.UserCreateRequestDto;
import org.sagebionetworks.challenge.model.dto.UserCreateResponseDto;
import org.sagebionetworks.challenge.model.dto.UserDto;
import org.sagebionetworks.challenge.model.dto.UserStatusDto;
import org.sagebionetworks.challenge.model.dto.UsersPageDto;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.sagebionetworks.challenge.model.mapper.UserMapper;
import org.sagebionetworks.challenge.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

  @Autowired
  private KeycloakUserService keycloakUserService;

  @Autowired
  private UserRepository userRepository;

  private UserMapper userMapper = new UserMapper();

  @Transactional
  public UserCreateResponseDto createUser(UserCreateRequestDto userCreateRequest) {
    if (keycloakUserService.getUserByUsername(userCreateRequest.getLogin()).isPresent()) {
      throw new UsernameAlreadyExistsException(
        String.format("The username %s already exist.", userCreateRequest.getLogin())
      );
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
      Optional<UserRepresentation> representation = keycloakUserService.getUserByUsername(
        userCreateRequest.getLogin()
      );
      UserEntity user = new UserEntity();
      user.setAuthId(representation.get().getId());
      user.setStatus(UserStatusDto.PENDING);
      UserEntity savedUser = userRepository.save(user);
      return UserCreateResponseDto.builder().id(savedUser.getId()).build();
    }

    throw new InvalidUserException(null);
  }

  @Transactional(readOnly = true)
  public UsersPageDto listUsers(Integer pageNumber, Integer pageSize) {
    Page<UserEntity> userEntitiesPage = userRepository.findAll(
      PageRequest.of(pageNumber, pageSize)
    );
    List<UserEntity> userEntities = userEntitiesPage.getContent();
    List<UserDto> users = new ArrayList<>();
    userEntities.forEach(userEntity -> {
      UserRepresentation userRepresentation = keycloakUserService.getUser(userEntity.getAuthId());
      UserDto user = userMapper.convertToDto(userEntity);
      user.email(userRepresentation.getEmail());
      user.login(userRepresentation.getUsername());
      users.add(user);
    });
    return UsersPageDto.builder().users(users).totalResults(0).paging(null).build();
  }

  @Transactional(readOnly = true)
  public UserDto getUser(Long userId) {
    UserEntity userEntity = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new UserNotFoundException(String.format("The user with ID %s does not exist.", userId))
      );

    UserRepresentation userRepresentation = keycloakUserService.getUser(userEntity.getAuthId());
    UserDto user = userMapper.convertToDto(userEntity);
    user.setEmail(userRepresentation.getEmail());
    return user;
  }
  // // TODO Review this function
  // public UserDto updateUser(Long id, UserUpdateRequestDto userUpdateRequest) {
  //   UserEntity userEntity =
  // userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

  //   if (userUpdateRequest.getStatus() == UserStatusDto.APPROVED) {
  //     UserRepresentation userRepresentation =
  // keycloakUserService.getUser(userEntity.getAuthId());
  //     userRepresentation.setEnabled(true);
  //     userRepresentation.setEmailVerified(true);
  //     keycloakUserService.updateUser(userRepresentation);
  //   }

  //   userEntity.setStatus(userUpdateRequest.getStatus());
  //   return userMapper.convertToDto(userRepository.save(userEntity));
  // }
}
