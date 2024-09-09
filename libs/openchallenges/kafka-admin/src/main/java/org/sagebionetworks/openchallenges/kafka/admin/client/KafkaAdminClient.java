package org.sagebionetworks.openchallenges.kafka.admin.client;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.sagebionetworks.openchallenges.app.config.data.KafkaConfigData;
import org.sagebionetworks.openchallenges.app.config.data.RetryConfigData;
import org.sagebionetworks.openchallenges.kafka.admin.exception.KafkaClientException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class KafkaAdminClient {

  private KafkaConfigData kafkaConfigData;

  private RetryConfigData retryConfigData;

  private AdminClient adminClient;

  private RetryTemplate retryTemplate;

  private WebClient webClient;

  public KafkaAdminClient(
    KafkaConfigData kafkaConfigData,
    RetryConfigData retryConfigData,
    AdminClient client,
    RetryTemplate restTemplate,
    WebClient webClient
  ) {
    this.kafkaConfigData = kafkaConfigData;
    this.retryConfigData = retryConfigData;
    this.adminClient = client;
    this.retryTemplate = restTemplate;
    this.webClient = webClient;
  }

  public void createTopics() {
    CreateTopicsResult createTopicsResult;
    try {
      createTopicsResult = retryTemplate.execute(this::doCreateTopics);
      log.info("Create topic result {}", createTopicsResult.values().values());
    } catch (Throwable t) {
      throw new KafkaClientException("Reached max number of retry for creating Kafka topic(s).", t);
    }
    checkTopicsCreated();
  }

  public void checkTopicsCreated() {
    Collection<TopicListing> topics = getTopics();
    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    Integer multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();
    for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
      while (!isTopicCreated(topics, topic)) {
        checkMaxRetry(retryCount++, maxRetry);
        sleep(sleepTimeMs);
        sleepTimeMs *= multiplier;
        topics = getTopics();
      }
    }
  }

  public void checkSchemaRegistry() {
    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    Integer multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();
    while (!getSchemaRegistryStatus().is2xxSuccessful()) {
      checkMaxRetry(retryCount++, maxRetry);
      sleep(sleepTimeMs);
      sleepTimeMs *= multiplier;
    }
  }

  private HttpStatus getSchemaRegistryStatus() {
    try {
      return webClient
        .method(HttpMethod.GET)
        .uri(kafkaConfigData.getSchemaRegistryUrl())
        .exchange()
        .map(ClientResponse::statusCode)
        .block();
    } catch (Exception e) {
      return HttpStatus.SERVICE_UNAVAILABLE;
    }
  }

  private void sleep(long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      throw new KafkaClientException("Error while sleeping for waiting new created topic(s).");
    }
  }

  private void checkMaxRetry(int retry, Integer maxRetry) {
    if (retry > maxRetry) {
      throw new KafkaClientException("Reached max number of retry for reading Kafka topic(s).");
    }
  }

  private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
    if (topics == null) {
      return false;
    }
    return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
  }

  private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
    List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
    log.info("Creating {} topic(s), attempt {}", topicNames.size(), retryContext.getRetryCount());
    List<NewTopic> kafkaTopics = topicNames
      .stream()
      .map(topic ->
        new NewTopic(
          topic.trim(),
          kafkaConfigData.getNumOfPartitions(),
          kafkaConfigData.getReplicationFactor()
        )
      )
      .collect(Collectors.toList());
    return adminClient.createTopics(kafkaTopics);
  }

  private Collection<TopicListing> getTopics() {
    Collection<TopicListing> topics;
    try {
      topics = retryTemplate.execute(this::doGetTopics);
    } catch (Throwable t) {
      throw new KafkaClientException("Reached max number of retry for reading Kafka topic(s).", t);
    }
    return topics;
  }

  private Collection<TopicListing> doGetTopics(RetryContext retryContext)
    throws InterruptedException, ExecutionException {
    log.info(
      "Reading Kafka topic {}, attempt {}",
      kafkaConfigData.getTopicNamesToCreate().toArray(),
      retryContext.getRetryCount()
    );
    Collection<TopicListing> topics = adminClient.listTopics().listings().get();
    if (topics != null) {
      topics.forEach(topic -> log.debug("Topic with name {}", topic.name()));
    }
    return topics;
  }
}
