package org.sagebionetworks.agora.gene.api.configuration;

import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PyroscopeConfiguration {

  private final GeneApiConfiguration geneApiConfiguration;

  public PyroscopeConfiguration(GeneApiConfiguration geneApiConfiguration) {
    this.geneApiConfiguration = geneApiConfiguration;
  }

  @Value("${spring.application.name}")
  private String applicationName;

  @PostConstruct
  public void init() {
    PyroscopeAgent.start(
      new Config.Builder()
        .setApplicationName(applicationName)
        .setProfilingEvent(EventType.ITIMER)
        .setFormat(Format.JFR)
        .setServerAddress(geneApiConfiguration.getPyroscopeServerAddress())
        .build()
    );
  }
}
