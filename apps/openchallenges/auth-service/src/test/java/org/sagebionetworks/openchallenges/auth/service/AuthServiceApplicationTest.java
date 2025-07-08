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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceApplication")
class AuthServiceApplicationTest {

  @Test
  @DisplayName("should have correct Spring Boot annotations")
  void shouldHaveCorrectSpringBootAnnotations() {
    // given
    Class<AuthServiceApplication> applicationClass = AuthServiceApplication.class;

    // when & then
    assertThat(applicationClass.isAnnotationPresent(SpringBootApplication.class)).isTrue();
    assertThat(applicationClass.isAnnotationPresent(EnableConfigurationProperties.class)).isTrue();
    assertThat(applicationClass.isAnnotationPresent(ComponentScan.class)).isTrue();
  }

  @Test
  @DisplayName("should configure SpringBootApplication with correct name generator")
  void shouldConfigureSpringBootApplicationWithCorrectNameGenerator() {
    // given
    Class<AuthServiceApplication> applicationClass = AuthServiceApplication.class;
    SpringBootApplication springBootAnnotation = applicationClass.getAnnotation(
      SpringBootApplication.class
    );

    // when & then
    assertThat(springBootAnnotation.nameGenerator()).isEqualTo(
      FullyQualifiedAnnotationBeanNameGenerator.class
    );
  }

  @Test
  @DisplayName("should configure ComponentScan with correct base packages")
  void shouldConfigureComponentScanWithCorrectBasePackages() {
    // given
    Class<AuthServiceApplication> applicationClass = AuthServiceApplication.class;
    ComponentScan componentScanAnnotation = applicationClass.getAnnotation(ComponentScan.class);

    // when & then
    assertThat(componentScanAnnotation.basePackages()).containsExactlyInAnyOrder(
      "org.sagebionetworks.openchallenges.auth.service",
      "org.sagebionetworks.openchallenges.auth.service.api",
      "org.sagebionetworks.openchallenges.auth.service.configuration"
    );
  }

  @Test
  @DisplayName("should configure ComponentScan with correct name generator")
  void shouldConfigureComponentScanWithCorrectNameGenerator() {
    // given
    Class<AuthServiceApplication> applicationClass = AuthServiceApplication.class;
    ComponentScan componentScanAnnotation = applicationClass.getAnnotation(ComponentScan.class);

    // when & then
    assertThat(componentScanAnnotation.nameGenerator()).isEqualTo(
      FullyQualifiedAnnotationBeanNameGenerator.class
    );
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
