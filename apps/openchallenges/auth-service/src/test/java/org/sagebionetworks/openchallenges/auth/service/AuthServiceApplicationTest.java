package org.sagebionetworks.openchallenges.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceApplication")
class AuthServiceApplicationTest {

  @Test
  @DisplayName("should have SpringBootApplication annotation")
  void shouldHaveSpringBootApplicationAnnotation() {
    assertThat(AuthServiceApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
  }


  @Test
  @DisplayName("should run Spring application when main method is called")
  void shouldRunSpringApplicationWhenMainMethodIsCalled() {
    // given
    String[] args = { "--spring.profiles.active=test" };

    // when & then
    try (
      MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)
    ) {
      AuthServiceApplication.main(args);

      springApplicationMock.verify(() ->
        SpringApplication.run(eq(AuthServiceApplication.class), eq(args))
      );
    }
  }

  @Test
  @DisplayName("should run Spring application with empty args")
  void shouldRunSpringApplicationWithEmptyArgs() {
    // given
    String[] emptyArgs = {};

    // when & then
    try (
      MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)
    ) {
      AuthServiceApplication.main(emptyArgs);

      springApplicationMock.verify(() ->
        SpringApplication.run(eq(AuthServiceApplication.class), eq(emptyArgs))
      );
    }
  }

  @Test
  @DisplayName("should run Spring application with null args")
  void shouldRunSpringApplicationWithNullArgs() {
    // given
    String[] nullArgs = null;

    // when & then
    try (
      MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)
    ) {
      AuthServiceApplication.main(nullArgs);

      springApplicationMock.verify(() ->
        SpringApplication.run(eq(AuthServiceApplication.class), eq(nullArgs))
      );
    }
  }

  @Test
  @DisplayName("should have public constructor")
  void shouldHavePublicConstructor() {
    // when & then
    assertThat(AuthServiceApplication.class.getConstructors()).hasSize(1);
    assertThat(AuthServiceApplication.class.getConstructors()[0].getParameterCount()).isZero();
  }

  @Test
  @DisplayName("should be able to instantiate application class")
  void shouldBeAbleToInstantiateApplicationClass() {
    // when
    AuthServiceApplication application = new AuthServiceApplication();

    // then
    assertThat(application).isNotNull();
    assertThat(application).isInstanceOf(AuthServiceApplication.class);
  }
}
