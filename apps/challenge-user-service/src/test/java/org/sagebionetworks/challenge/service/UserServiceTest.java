package org.sagebionetworks.challenge.service;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.entity.UserEntity;
import org.sagebionetworks.challenge.model.mapper.UserMapper;
import org.sagebionetworks.challenge.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
// @SpringBootTest
@ActiveProfiles("unit")
public class UserServiceTest {

  @TestConfiguration
  static class UserServiceTestContextConfiguration {
    @Bean
    UserService userService() {
      return new UserService();
    }
  }

  @Autowired
  private UserService userService;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private KeycloakUserService keycloakUserService;

  private User user;

  @BeforeEach
  public void setup() {
    user = User.builder().username("test").email("test@gmail.com").password("changeme").build();
  }

  @Test
  void givenEmployeesList_whenGetAllEmployees_thenReturnEmployeesList() {


    // given
    User user2 =
        User.builder().username("test2").email("test2@gmail.com").password("changeme").build();
    UserMapper userMapper = new UserMapper();
    UserEntity userEntity = userMapper.convertToEntity(user);
    UserEntity user2Entity = userMapper.convertToEntity(user2);
    // given(userRepository.findAll()).willReturn(List.of(userEntity, user2Entity));

    // when
    // List<UserEntity> userList = new ArrayList<>();
    List<UserEntity> userList = List.of(userEntity, user2Entity);
    Page<UserEntity> userPage = new PageImpl<>(userList);
    when(userRepository.findAll(isA(Pageable.class))).thenReturn(userPage);



    List<User> users = userService.listUsers(Pageable.unpaged());

    log.info("USERS {}", users.size());
  }

  // @InjectMocks
  // private UserService userService;

  // @Mock
  // private UserRepository userRepository;

  // private User user;

  // @BeforeEach
  // public void setup() {
  // user = User.builder().username("test").email("test@gmail.com").password("changeme").build();
  // }

  // @Test
  // void givenEmployeesList_whenGetAllEmployees_thenReturnEmployeesList() {
  // // given
  // User user2 =
  // User.builder().username("test2").email("test2@gmail.com").password("changeme").build();
  // UserMapper userMapper = new UserMapper();
  // UserEntity userEntity = userMapper.convertToEntity(user);
  // UserEntity user2Entity = userMapper.convertToEntity(user2);
  // given(userRepository.findAll()).willReturn(List.of(userEntity, user2Entity));

  // // when
  // List<User> users = userService.listUsers();
  // }
}
