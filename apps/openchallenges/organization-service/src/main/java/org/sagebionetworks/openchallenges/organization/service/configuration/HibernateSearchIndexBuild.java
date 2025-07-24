package org.sagebionetworks.openchallenges.organization.service.configuration;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
  value = "spring.jpa.properties.hibernate.search.enabled",
  havingValue = "true"
)
public class HibernateSearchIndexBuild implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger logger = LoggerFactory.getLogger(HibernateSearchIndexBuild.class);

  private final EntityManager entityManager;

  public HibernateSearchIndexBuild(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  public void onApplicationEvent(ApplicationReadyEvent event) {
    logger.info("Started Initializing Indexes");
    SearchSession searchSession = Search.session(entityManager);
    MassIndexer indexer = searchSession
      .massIndexer()
      .idFetchSize(150)
      .batchSizeToLoadObjects(25)
      .threadsToLoadObjects(12);

    try {
      indexer.startAndWait();
    } catch (InterruptedException e) {
      logger.warn("Failed to load data from database");
      Thread.currentThread().interrupt();
    }

    logger.info("Completed Indexing");
  }
}
