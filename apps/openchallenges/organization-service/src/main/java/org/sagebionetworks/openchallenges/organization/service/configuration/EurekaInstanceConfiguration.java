package org.sagebionetworks.openchallenges.organization.service.configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
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
   *     href="https://medium.com/@harsugangwar/microservices-with-eureka-on-aws-ecs-a-guide-to-service-discovery-and-ci-with-aws-codebuild-0478a4a71ba2">Microservices
   *     with Eureka on AWS ECS: A Guide to Service Discovery and CI with AWS CodeBuild</a>
   */
  @Bean
  @ConditionalOnProperty(
      value = "openchallenges-organization-service.is-deployed-on-aws",
      havingValue = "true",
      matchIfMissing = false)
  public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
    LOGGER.info("Configuring the Eureka instance to be AWS-aware.");
    EurekaInstanceConfigBean bean = new EurekaInstanceConfigBean(inetUtils);
    String ip = null;
    try {
      ip = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      LOGGER.error("Unable to get the host address.", e);
    }
    bean.setIpAddress(ip);
    // bean.setPreferIpAddress(true);
    // bean.setNonSecurePortEnabled(true);
    // bean.setNonSecurePort(Integer.parseInt(port));
    return bean;
  }
}
