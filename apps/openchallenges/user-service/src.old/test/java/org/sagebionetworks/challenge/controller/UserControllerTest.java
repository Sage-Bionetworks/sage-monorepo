package org.sagebionetworks.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.challenge.model.repository.UserRepository;
import org.sagebionetworks.challenge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @MockBean private UserService userService;

  // private UserMapper userMapper = new UserMapper();

  // @Autowired
  // private TestRestTemplate restTemplate;

  @BeforeEach
  void setup() {
    userRepository.deleteAll();
  }

  // @DisplayName("Single test successful")
  // @Disabled("Not implemented yet")
  @Test
  public void givenUserObject_whenCreateUser_thenStatus201() throws Exception {
    // User user = new User("test", "test@gmail.com", "changeme");
    // Mockito.when(userService.createUser(user)).thenReturn(user);
    // mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON)
    // .content(objectMapper.writeValueAsString(user))).andExpect(status().isCreated());

    // .accept(MediaType.APPLICATION_JSON))
    // .andExpect(status().isCreated())
    // .perform(post("/forums/{forumId}/register", 42L).contentType("application/json")
    // .param("sendWelcomeMail", "true").content(objectMapper.writeValueAsString(user)))
    // .andExpect(status().isOk());
  }
}
