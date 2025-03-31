package org.sagebionetworks.amp.als.dataset.service;

import org.sagebionetworks.amp.als.app.config.data.DatasetServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "org.sagebionetworks.amp.als" })
public class DatasetServiceApplication implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(DatasetServiceApplication.class);

  private final DatasetServiceConfigData datasetServiceConfigData;

  public DatasetServiceApplication(DatasetServiceConfigData datasetServiceConfigData) {
    this.datasetServiceConfigData = datasetServiceConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(DatasetServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    LOG.info(datasetServiceConfigData.getWelcomeMessage());
  }
}
