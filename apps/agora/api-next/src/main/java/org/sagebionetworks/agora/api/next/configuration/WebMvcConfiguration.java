package org.sagebionetworks.agora.api.next.configuration;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.agora.api.next.interceptor.ApiLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for the Agora API.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

  private final ApiLoggingInterceptor apiLoggingInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(apiLoggingInterceptor);
  }
}
