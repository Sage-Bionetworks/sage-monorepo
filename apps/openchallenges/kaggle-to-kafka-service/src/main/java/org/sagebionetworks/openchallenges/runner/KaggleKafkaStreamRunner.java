package org.sagebionetworks.openchallenges.runner;

import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.sagebionetworks.openchallenges.configuration.KaggleToKafkaServiceConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void listKaggleChallenges() throws IOException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      final HttpGet httpGet = new HttpGet(config.getKaggleBaseUrl() + "/competitions/list");
      HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
      final String responseBody = httpClient.execute(httpGet, responseHandler);
      System.out.println(responseBody);
    }
  }
}
