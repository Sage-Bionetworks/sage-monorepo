package org.sagebionetworks.openchallenges.service.registry.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaInstanceConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(EurekaInstanceConfiguration.class);

  /**
   * If the application is planned to be deployed to an AWS cloud, the Eureka instance must be
   * configured to be AWS-aware. This can be done by customizing the EurekaInstanceConfigBean.
   *
   * @see <a
   *     href="https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/#using-eureka-on-aws">Using
   *     Eureka on AWS</a>
   */
  @Bean
  @ConditionalOnProperty(
      value = "openchallenges-service-registry.is-deployed-on-aws",
      havingValue = "true",
      matchIfMissing = false)
  public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
    LOGGER.info("Configuring the Eureka instance to be AWS-aware.");
    EurekaInstanceConfigBean bean = new EurekaInstanceConfigBean(inetUtils);
    AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
    bean.setDataCenterInfo(info);
    return bean;
  }
}
