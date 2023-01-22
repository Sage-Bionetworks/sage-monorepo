package org.sagebionetworks.openchallenges.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.sagebionetworks.openchallenges.configuration.KaggleToKafkaServiceConfiguration;
import org.sagebionetworks.openchallenges.model.dto.KaggleCompetitionDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
    name = "kaggle-to-kafka-service.enable-mock-challenges",
    havingValue = "false",
    matchIfMissing = true)
public class KaggleKafkaStreamRunner implements StreamRunner {

  private final KaggleToKafkaServiceConfiguration config;

  public KaggleKafkaStreamRunner(KaggleToKafkaServiceConfiguration config) {
    this.config = config;
  }

  @Override
  public void start() {
    try {
      listKaggleChallenges();
    } catch (IOException e) {
      e.printStackTrace();
      // throw new KaggleUnauthorizedRequestException("Unauthorized request sent to Kaggle.");
    }
  }

  private void listKaggleChallenges() throws IOException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      final String basicAuthToken =
          Base64.encodeBase64String(
              String.format("%s:%s", config.getKaggleUsername(), config.getKaggleKey()).getBytes());

      final HttpGet httpGet = new HttpGet(config.getKaggleBaseUrl() + "/competitions/list");
      httpGet.setHeader("Content-Type", "application/json");
      httpGet.setHeader("Authorization", "Basic " + basicAuthToken);

      HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
      final String responseBody = httpClient.execute(httpGet, responseHandler);
      System.out.println(responseBody);

      ObjectMapper mapper = new ObjectMapper();
      KaggleCompetitionDto[] competitions =
          mapper.readValue(responseBody, KaggleCompetitionDto[].class);
      log.info("Competitions: {}", (Object) competitions);
    }
  }
}
