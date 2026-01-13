package org.sagebionetworks.openchallenges.challenge.service.configuration;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
  name = "spring.jpa.properties.hibernate.search.enabled",
  havingValue = "true",
  matchIfMissing = false
)
@RequiredArgsConstructor
@Slf4j
public class HibernateSearchIndexBuild implements ApplicationListener<ApplicationReadyEvent> {

  private final EntityManager entityManager;

  @Override
  @Transactional
  public void onApplicationEvent(ApplicationReadyEvent event) {
    log.info("Started Initializing Indexes");
    SearchSession searchSession = Search.session(entityManager);
    MassIndexer indexer = searchSession
      .massIndexer()
      .idFetchSize(150)
      .batchSizeToLoadObjects(25)
      .threadsToLoadObjects(12);

    try {
      indexer.startAndWait();
    } catch (InterruptedException e) {
      log.warn("Failed to load data from database");
      Thread.currentThread().interrupt();
    }

    log.info("Completed Indexing");
  }
}
