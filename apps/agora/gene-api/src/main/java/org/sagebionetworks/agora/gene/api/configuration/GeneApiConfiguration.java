package org.sagebionetworks.agora.gene.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "agora-gene-api")
public class GeneApiConfiguration {

  private String pyroscopeServerAddress;
}
