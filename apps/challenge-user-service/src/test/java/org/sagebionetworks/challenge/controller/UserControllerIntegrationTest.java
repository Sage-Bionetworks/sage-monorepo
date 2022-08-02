package org.sagebionetworks.challenge.controller;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagebionetworks.challenge.ChallengeUserServiceApplication;
import org.sagebionetworks.challenge.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setup() {
    userRepository.deleteAll();
  }

  // @DisplayName("Single test successful")
  // @Disabled("Not implemented yet")
  @Test
  public void givenUserObject_whenCreateUser_thenStatus201() throws Exception {
    // log.info("HELLO");
    assumeTrue(5 > 1);
  }
}
