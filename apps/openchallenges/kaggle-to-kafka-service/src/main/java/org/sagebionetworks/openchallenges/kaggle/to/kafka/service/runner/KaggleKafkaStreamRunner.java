package org.sagebionetworks.openchallenges.kaggle.to.kafka.service.runner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.net.URIBuilder;
import org.sagebionetworks.openchallenges.kaggle.to.kafka.service.configuration.KaggleToKafkaServiceConfiguration;
import org.sagebionetworks.openchallenges.kaggle.to.kafka.service.model.dto.KaggleCompetitionDto;
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
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void listKaggleChallenges() throws IOException, URISyntaxException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      final String basicAuthToken =
          Base64.encodeBase64String(
              String.format("%s:%s", config.getKaggleUsername(), config.getKaggleKey()).getBytes());

      URIBuilder builder = new URIBuilder();
      builder
          .setScheme("https")
          .setHost("www.kaggle.com")
          .setPath("/api/v1/competitions/list")
          .setParameter("page", "1");
      URI uri = builder.build();

      // final HttpGet httpGet = new HttpGet(config.getKaggleBaseUrl() + "/competitions/list");
      final HttpGet httpGet = new HttpGet(uri);
      httpGet.setHeader("Content-Type", "application/json");
      httpGet.setHeader("Authorization", "Basic " + basicAuthToken);

      // Filter by category: e.g. "Research"
      // valid_competition_categories = [
      //   'all', 'featured', 'research', 'recruitment', 'gettingStarted',
      //   'masters', 'playground'
      // ]
      // valid_list_sort_by = [
      //   'hotness', 'commentCount', 'dateCreated', 'dateRun', 'relevance',
      //   'scoreAscending', 'scoreDescending', 'viewCount', 'voteCount'
      // ]
      // page: the page to return (default is 1)
      // search: a search term to use (default is empty string)

      HttpClientResponseHandler<String> responseHandler = new BasicHttpClientResponseHandler();
      final String responseBody = httpClient.execute(httpGet, responseHandler);
      System.out.println(responseBody);

      ObjectMapper mapper = new ObjectMapper();
      List<KaggleCompetitionDto> competitions =
          mapper.readValue(responseBody, new TypeReference<List<KaggleCompetitionDto>>() {});
      log.info("Competitions: {}", competitions);
      log.info("Competitions count: {}", competitions.size());
    }
  }
}
