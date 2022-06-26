package org.sagebionetworks.challenge.configuration;

// import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// @ComponentScan(basePackageClasses = MarvelousApiClientProxy.class)
@Configuration
@PropertySource(value = "classpath:data-access-application.yaml",
    factory = YamlPropertySourceFactory.class)
public class DataAccessConfiguration {
}
