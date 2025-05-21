package org.sagebionetworks.agora.gene.api.configuration;

import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PyroscopeConfiguration {

  @PostConstruct
  public void init() {
    PyroscopeAgent.start(
      new Config.Builder()
        .setApplicationName("agora-gene-api")
        .setProfilingEvent(EventType.ITIMER)
        .setFormat(Format.JFR)
        .setServerAddress("http://localhost:8511")
        .build()
    );
  }
}
