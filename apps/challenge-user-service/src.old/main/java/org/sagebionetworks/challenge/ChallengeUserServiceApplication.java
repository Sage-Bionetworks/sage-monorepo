package org.sagebionetworks.challenge;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import io.swagger.v3.oas.models.info.Info;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class ChallengeUserServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChallengeUserServiceApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  // @Bean
  // public GroupedOpenApi usersGroup(@Value("${springdoc.version}") String appVersion) {
  // return GroupedOpenApi.builder().group("users")
  // // .addOperationCustomizer((operation, handlerMethod) -> {
  // // operation.addSecurityItem(new SecurityRequirement().addList("basicScheme"));
  // // return operation;
  // // })
  // .addOpenApiCustomiser(
  // openApi -> openApi.info(new Info().title("Users API").version(appVersion)))
  // .packagesToScan("org.springdoc.demo.app2").build();
  // }
}
