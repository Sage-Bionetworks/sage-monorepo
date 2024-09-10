package org.sagebionetworks.openchallenges.kaggle.to.kafka.service.runner;

import java.util.Arrays;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.app.config.data.KaggleToKafkaServiceConfigData;
import org.sagebionetworks.openchallenges.kaggle.to.kafka.service.exception.KaggleToKafkaServiceException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
  name = "kaggle-to-kafka-service.enable-mock-challenges",
  havingValue = "true"
)
public class MockKafkaStreamRunner implements StreamRunner {

  private final KaggleToKafkaServiceConfigData kaggleToKafkaServiceConfigData;

  // private static final Random RANDOM = new Random();

  // private static final String[] WORDS =
  //     new String[] {
  //       "Lorem",
  //       "ipsum",
  //       "dolor",
  //       "sit",
  //       "amet",
  //       "consectetur",
  //       "adipiscing",
  //       "elit",
  //       "sed",
  //       "do",
  //       "eiusmod",
  //       "tempor",
  //       "incididunt",
  //       "labore",
  //       "dolore",
  //       "magna",
  //       "aliqua"
  //     };

  public MockKafkaStreamRunner(KaggleToKafkaServiceConfigData kaggleToKafkaServiceConfigData) {
    this.kaggleToKafkaServiceConfigData = kaggleToKafkaServiceConfigData;
  }

  @Override
  public void start() {
    String[] searchTerms = kaggleToKafkaServiceConfigData
      .getKaggleSearchTerms()
      .toArray(String[]::new);
    // int minChallengeNameLength = config.getMockMinChallengeNameLength();
    // int maxChallengeNameLength = config.getMockMaxChallengeNameLength();
    long sleepMs = kaggleToKafkaServiceConfigData.getMockSleepMs();

    log.info(
      "Starting mock filtering Kaggle challenges for search terms {}",
      Arrays.toString(searchTerms)
    );
    simulateChallengeStream(sleepMs);
  }

  private void simulateChallengeStream(long sleepMs) {
    Executors.newSingleThreadExecutor()
      .submit(() -> {
        while (true) {
          log.info("plop");
          sleep(sleepMs);
        }
      });
  }

  private void sleep(long sleepMs) {
    try {
      Thread.sleep(sleepMs);
    } catch (InterruptedException e) {
      throw new KaggleToKafkaServiceException(
        "Error while sleeping for waiting new status to create."
      );
    }
  }
  // private String getRandomChallengeName(String[] words, int minChallengeNameLength,
  // maxChallengeNameLength) {
  //   StringBuilder challengeName = new StringBuilder();
  //   int challengeNameLength = RANDOM.nextInt(minChallengeNameLength, minChallengeNameLength);
  //   for (int i = 0; i < challengeNameLength)
  // }
}
