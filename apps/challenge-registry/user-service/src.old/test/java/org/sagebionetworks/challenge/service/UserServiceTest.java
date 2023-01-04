package org.sagebionetworks.challenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.dto.UserStatus;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.sagebionetworks.challenge.model.mapper.UserMapper;
import org.sagebionetworks.challenge.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

// @Slf4j
@SpringBootTest
public class UserServiceTest {

  @Autowired private UserService userService;

  @MockBean private UserRepository userRepository;

  @MockBean private KeycloakUserService keycloakUserService;

  private User user;
  private UserMapper userMapper = new UserMapper();

  @Value("${example.firstProperty}")
  String firstProperty;

  @BeforeEach
  public void setup() {
    user =
        User.builder()
            .id(1L)
            .username("test")
            .email("test@gmail.com")
            .password("changeme")
            .authId("1")
            .status(UserStatus.PENDING)
            .build();
  }

  @Test
  public void test() {
    assertThat(firstProperty).isEqualTo("test");
  }

  @Test
  void givenUserList_whenGetAllUsers_thenReturnUserList() {
    // given
    User user2 =
        User.builder()
            .id(2L)
            .username("test2")
            .email("test2@gmail.com")
            .password("changeme")
            .authId("2")
            .status(UserStatus.PENDING)
            .build();
    List<User> givenUsers = List.of(user, user2);
    List<UserEntity> givenUserList =
        List.of(userMapper.convertToEntity(user), userMapper.convertToEntity(user2));
    Page<UserEntity> userPage = new PageImpl<>(givenUserList);
    when(userRepository.findAll(isA(Pageable.class))).thenReturn(userPage);
    when(keycloakUserService.getUser(anyString()))
        .thenAnswer(
            input -> {
              String authId = input.getArgument(0);
              User user =
                  givenUsers.stream().filter(u -> authId.equals(u.getAuthId())).findFirst().get();
              UserRepresentation representation = new UserRepresentation();
              representation.setId(user.getId().toString());
              representation.setEmail(user.getEmail());
              return representation;
            });

    // when
    List<User> users = userService.listUsers(Pageable.unpaged());

    // then
    assertThat(users.size()).isEqualTo(givenUserList.size());
  }
}
