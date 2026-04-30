package org.sagebionetworks.qtl.api.configuration;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.qtl.api.interceptor.ApiLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

  private final ApiLoggingInterceptor apiLoggingInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(apiLoggingInterceptor);
  }
}
